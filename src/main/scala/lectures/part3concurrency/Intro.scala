package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App {

  /*
  interface Runnable {
    public void run()
  }
   */
  // JVM threads
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Running in parallel")
  })

  aThread.start() // gives the signal to the JVM to start a JVM thread
  // create a JVM thread => OS thread

  val runnable = new Runnable {
    override def run(): Unit = println("Running in parallel")
  }
  val thread = new Thread(runnable)
  thread.start()

  runnable.run() // doesn't do anything in parallel!
  aThread.join() // blocks until aThread finishes running

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("Hello")))
  val threadHGoodbye = new Thread(() => (1 to 5).foreach(_ => println("Goodbye")))
  threadHello.start()
  threadHGoodbye.start()
  // different runs produce different results!

  // executors
  val pool = Executors.newFixedThreadPool(10)
  pool.execute(() => println("something in the thread pool"))
  pool.execute(() => {
    Thread.sleep(1000)
    println("done after one second")
  })
  pool.execute(() => {
    Thread.sleep(1000)
    println("almost done")
  })
  pool.execute(() => {
    Thread.sleep(1000)
    println("done after two seconds")
  })
//  pool.shutdown()
//  pool.execute(() => println("should not appear")) // throws an exception in the calling thread

//  pool.shutdownNow()
  println(pool.isShutdown)

}
