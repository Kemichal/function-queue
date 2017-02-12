package com.kemichal.functionqueue

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/** The `AsyncFunctionQueue` allows you to queue up a series of functions to run.
  * The functions is run one at a time (sequentially) and will not block the current thread (asynchronously).
  *
  * @param swallowExceptions if exceptions thrown in functions should be rethrown
  * @param ec the execution context on which the function is run
  */
class AsyncFunctionQueue(swallowExceptions: Boolean = false)(
    implicit ec: ExecutionContext) {

  private val queue = new mutable.Queue[() => Unit]

  private var running: Boolean = false

  /** Queues a function to be run sometime in the future.
    *
    * @param f the function to run
    */
  def add(f: => Unit): Unit = queue.synchronized {
    queue += f _

    // Start running functions in the queue if they aren't already
    if (!running) {
      runNext()
    }
  }

  /** Tries to run the next function in the queue. */
  private def runNext(): Unit = {
    if (queue.isEmpty) {
      running = false
    } else {
      running = true

      // Run the function in a Future
      Future(queue.dequeue()()).onComplete {
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
