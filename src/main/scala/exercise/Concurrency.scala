package exercise

object Concurrency extends App {

  /**
    * 1) construct 50 "inception" threads (threads that construct other threads)
    * Thread1 -> Thread2 -> Thread3 -> ...
    * println("hello from thread#x)
    * in reverse order
    */
  def inceptionThreads(n: Int, i: Int = 1): Thread = {
    new Thread(() => {
      if (i < n) {
        val newThread = inceptionThreads(n, i + 1)
        newThread.start()
        newThread.join()
      }
      println(s"Hello from thread $i")
    })

  }
  inceptionThreads(50).start()


  /**
    * 2)
    */
  var x = 0
  val threads = (1 to 100).map(_ => new Thread(() => x += 1))
  threads.foreach(_.start())
  /*
      1) what is the biggest value possible for x?
      2) what is the smallest value possible for x?
      1) 100: 0 -> 100 = 99, + 1 = 100
      2) 1
   */

  /*
      3 sleep fallacy
   */
  var message = ""
  val awesomeThread = new Thread(() => {
    Thread.sleep(1000)
    message = "Scala is awesome"
  })
  message = "Scala sucks"
  awesomeThread.start()
  Thread.sleep(2000)
  println(message)
  /*
      What's the value of message? Scala is awesome
      is it guaranteed? no
      why? why not?

   */

}
