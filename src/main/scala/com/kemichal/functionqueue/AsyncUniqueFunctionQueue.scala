package com.kemichal.functionqueue

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/** The `AsyncUniqueFunctionQueue` allows you to queue up a series of functions to run.
  * The functions is run one at a time (sequentially) and will not block the current thread (asynchronously).
  * Every function has an associated key which must be unique for the queue, or else the function can't be added.
  *
  * This is useful behavior when you expect several equivalent calls in a short time,
  * and only want to process the first one.
  *
  * @param swallowExceptions if exceptions thrown in functions should be rethrown
  * @param ec the execution context on which the function is run
  * @tparam A type of the key to be used to check for equality
  */
class AsyncUniqueFunctionQueue[A](swallowExceptions: Boolean = false)(
    implicit ec: ExecutionContext) {

  private val queue = new mutable.Queue[(A, () => Unit)]

  private var currentJob: Option[(A, () => Unit)] = None

  /** Queues a function to be run sometime in the future, assuming that the key is unique for the queue.
    *
    * @param key the key used to determine if the function is to be added
    * @param f the function to run
    * @return None if the function was queued successfully, otherwise Some error message
    */
  def add(key: A)(f: => Unit): Option[String] = queue.synchronized {
    currentJob match {
      case Some(job) if job._1 == key =>
        Some(s"A function with this key is currently running: $key")
      case _ =>
        if (!queue.exists(x => x._1 == key)) {
          val t = (key, f _)
          queue += t

          // Start running functions in the queue if they aren't already
          if (currentJob.isEmpty) {
            runNext()
          }
          None
        } else {
          Some(s"A function with this key is currently queued: $key")
        }
    }
  }

  /** Tries to run the next function in the queue. */
  private def runNext(): Unit = {
    if (queue.isEmpty) {
      currentJob = None
    } else {
      currentJob = Some(queue.dequeue())

      // Run the function in a Future
      Future(currentJob.foreach(_._2())).onComplete {
        case Success(_) =>
          // Function is done running, try next
          runNext()
        case Failure(e) =>
          runNext()
          if (!swallowExceptions) throw e
      }
    }
  }
}
