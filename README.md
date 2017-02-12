[licenseImg]: https://img.shields.io/:license-MIT-blue.svg
[licenseLink]: LICENSE

# function-queue [![License][licenseImg]][licenseLink]
`function-queue` is a collection of classes for queueing up execution of functions in Scala.

## AsyncFunctionQueue
Allows you to add functions to a queue which is then executed sequentially in `Future`s.

```scala
val asyncFunctionQueue = new AsyncFunctionQueue()
asyncFunctionQueue.add {
  println("Hello world")
}
```

## AsyncUniqueFunctionQueue
Similar to `AsyncFunctionQueue`, but the functions added must have a unique key to be accepted into the queue.

```scala
val asyncUniqueFunctionQueue = new AsyncUniqueFunctionQueue[String]()
asyncUniqueFunctionQueue.add("1") {
  println("Hello world")
}
```
