package com.kemichal.functionqueue

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class AsyncUniqueFunctionQueue[A](swallowExceptions: Boolean = false)(
    implicit ec: ExecutionContext) {

  private val queue = new mutable.Queue[(A, () => Unit)]

  private var currentJob: Option[(A, () => Unit)] = None

  def add(key: A)(f: => Unit): Unit = queue.synchronized {

    currentJob match {
      case Some(job) if job._1 == key =>
      // A function with this key is currently running
      case _ =>
        if (!queue.exists(x => x._1 == key)) {
          val t = (key, f _)
          queue += t

          // Start running functions in the queue if they aren't already
          if (currentJob.isEmpty) {
            runNext()
          }
        } else {
          // A function with this key is currently queued
        }
    }
  }

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
          if (!swallowExceptions) throw e
      }
    }
  }
}
