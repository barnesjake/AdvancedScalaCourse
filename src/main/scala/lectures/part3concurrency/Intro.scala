package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App {

  /*
  interface Runnable {
    public void run()
  }
   */
  // JVM threads
  //  val aThread = new Thread(new Runnable {
  //    override def run(): Unit = println("Running in parallel")
  //  })
  //
  ////  aThread.start() // gives the signal to the JVM to start a JVM thread
  //  // create a JVM thread => OS thread
  //
  //  val runnable = new Runnable {
  //    override def run(): Unit = println("Running in parallel")
  //  }
  //  val thread = new Thread(runnable)
  ////  thread.start()
  //
  //  runnable.run() // doesn't do anything in parallel!
  //  aThread.join() // blocks until aThread finishes running
  //
  //  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("Hello")))
  //  val threadHGoodbye = new Thread(() => (1 to 5).foreach(_ => println("Goodbye")))
  //  threadHello.start()
  //  threadHGoodbye.start()
  //  // different runs produce different results!
  //
  //  // executors
  //  val pool = Executors.newFixedThreadPool(10)
  //  pool.execute(() => println("something in the thread pool"))
  //  pool.execute(() => {
  //    Thread.sleep(1000)
  //    println("done after one second")
  //  })
  //  pool.execute(() => {
  //    Thread.sleep(1000)
  //    println("almost done")
  //  })
  //  pool.execute(() => {
  //    Thread.sleep(1000)
  //    println("done after two seconds")
  //  })
  ////  pool.shutdown()
  ////  pool.execute(() => println("should not appear")) // throws an exception in the calling thread
  //
  ////  pool.shutdownNow()
  //  println(pool.isShutdown)

  println("*******************\n\n*******************\n\n")

  def runInParralel = {
    var x = 0
    val thread1 = new Thread(() => {
      x = 1
    })
    val thread2 = new Thread(() => {
      x = 2
    })
    thread1.start()
    thread2.start()
    println(x)
  }

  //  for (_ <- 1 to 10000) runInParralel
  // race condition

  class BankAccount(@volatile var amount: Int) {
    override def toString: String = "" + amount
  }

  def buy(account: BankAccount, thing: String, price: Int): Unit = {
    account.amount -= price
//    println(s"I bought $thing")
//    println(s"my account is now $account")
  }

  for (_ <- 1 to 1000) {
    val account = new BankAccount(50000)
    val thread1 = new Thread(() => buySafe(account, "shoes", 3000))
    val thread2 = new Thread(() => buySafe(account, "iphone12", 4000))
    thread1.start()
    thread2.start()
    Thread.sleep(1)
    if (account.amount != 43000) println(s"AHA: ${account.amount}")
    //    println()
  }

  // option1: use synchronized()
  def buySafe(account: BankAccount, thing: String, price: Int)  =
    account.synchronized({
      //no two threads can ecaluate this at the same time
      account.amount -= price
      println(s"I bought $thing")
      println(s"my account is now $account")
    })

  //option2: use @volatile



}
