package lectures.part3concurrency

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success, Try}

object FuturesAndPromises extends App {

  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife
  }

  println("Waiting for future")
  aFuture.onComplete {
    case Success(value) => println(s"meaning of life is $value")
    case Failure(e)     => println(s"I failed with $e")
  } // SOME thread
  Thread.sleep(3000)


  // mini social network

  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile) = println(s"${this.name} poking ${anotherProfile.name}")
  }

  object SocialNetwork {
    // "database"
    val names: Map[String, String] = Map(
      "fb.id.1-zuck" -> "Mark le robot",
      "fb.id.2-bill" -> "Bill",
      "fb.id.0-dummy" -> "Dummy"
    )

    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val random = new Random()

    // API
    def fetchProfile(id: String): Future[Profile] = Future {
      // fetching from db or something
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }

  }

  //client : mark to poke bill
  val mark: Future[Profile] = SocialNetwork.fetchProfile("fb.id.1-zuck")
  //  mark.onComplete {
  //    case Success(markProfile) =>
  //      val bill = SocialNetwork.fetchBestFriend(markProfile)
  //      bill.onComplete {
  //        case Success(billProfile) => markProfile.poke(billProfile)
  //        case Failure(e)           => e.printStackTrace()
  //      }
  //    case Failure(e)           => e.printStackTrace()
  //  }
  //  Thread.sleep(1000)

  // functional composition of futures
  // map, flatMap, filter
  val nameOnTheWall = mark.map(profile => profile.name)
  val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("Z"))

  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)

  Thread.sleep(1000)

  // fallbacks
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover {
    case e: Throwable => Profile("fb.id.0-dummy", "Forever alone")
  }

  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
  }

  val fallbackResult = SocialNetwork.fetchProfile("unkown id").fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))

  // online banking app
  case class User(name: String)

  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "Jake's bank"

    def fetchUser(name: String): Future[User] = Future {
      // simulate fetching from db
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      // simulate some processes
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "success")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
      // fetch user from the db
      // create a transaction (username) => merchantname
      // wait for the transaction to finish
      val transactionStatusF = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      //blocking
      Await.result(transactionStatusF, 2.seconds) // implicit conversions -> pimp my library
    }
  }

  println(BankingApp.purchase("Jake", "popcorn", "tesco", 100))

  // Promises
  val promise = Promise[Int]()
  val future = promise.future
  // thread 1 - "consumer"
  future.onComplete {
    case Success(result) => println("[consumer] I've received " + result)
  }

  // thread 2 - "producer"
  val producer = new Thread(() => {
    println("[producer] crunching numbers...")
    Thread.sleep(500)
    // "fulfilling" the promise
    promise.success(42)
    //    promise.failure(Throwable)
    println("[producer] done")
  })
  producer.start()
  Thread.sleep(1000)


  /*
    1) fulfill a future immediately with a value
    2) inSequence(fa, fb)
    3) first(fa, fb) => new future with the first value of the two futures
    4) last(fa, fb) => new future with the last value completed
    5) retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T]
   */

  // 1.
  def immediateFuture[T](someValue: T) = Future(someValue)

  // 2.
  def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] = {
    first.flatMap(_ => second)
  }

  // 3.
  def first[A](fa: Future[A], fb: Future[A]): Future[A] = {
    val promise = Promise[A]

    def tryComplete(promise: Promise[A], result: Try[A]) = result match {
      case Success(result)    => try {
        promise.success(result)
      } catch {
        case _ => // dont care
      }
      case Failure(throwable) => try {
        promise.failure(throwable)
      } catch {
        case _ => // dont care
      }
    }

    //    fa.onComplete(tryComplete(promise, _))
    //    fb.onComplete(tryComplete(promise, _))
    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)
    promise.future
  }

  // 4.
  def last[A](fa: Future[A], fb: Future[A]): Future[A] = {
    val bothPromise = Promise[A]
    val lastPromise = Promise[A]
    val checkAndComplete = (result: Try[A]) => if (!bothPromise.tryComplete(result)) lastPromise.complete(result)
    fa.onComplete(checkAndComplete)
    fb.onComplete(checkAndComplete)
    lastPromise.future
  }
  // 5.





  println(immediateFuture(42))
  println(inSequence(immediateFuture(42), immediateFuture(42)))
  Thread.sleep(100)
}
