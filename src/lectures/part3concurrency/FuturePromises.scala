package lectures.part3concurrency

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Random, Success, Try}

object FuturePromises extends App{

  def calculateMeaningOfLife : Int ={
    Thread.sleep(2000)
    42
  }
  val aFuture = Future{
    calculateMeaningOfLife // calculates the meaning of life on ANOTHER thread
  }// (global) which is passed by compiler ExecutionContext.Implicits.global
  //println(aFuture.value) // Option[Try[Int]] as the value is of aFuture.value of the

  println("Waiting on the future")
  aFuture.onComplete{ // onCompletion of the aFuture Thread
    case Success(meaningOfLife) => println(s"the meaning of the life is $meaningOfLife")
    case Failure(e) => println(s"I have failed with $e")
  }// some thread
 // Thread.sleep(3000)


  // mini social network
  case class Profile(id: String, name : String) {
    def poke(anotherProfile : Profile) = println(s"${this.name} poking ${anotherProfile.name} ")
  }

  object SocialNetwork {
    // database
    val names = Map(
          "fb.id.1-zuck" -> "Mark",
          "fb.id.2-bill" -> "Bill",
          "fb.id.0-dummy" -> "Dummy"
        ) // stores names with id
        val friends = Map(
          "fb.id.1-zuck" -> "fb.id.2-bill"
        ) // store a friend id to bestFriend id
    val random = new Random()
    def fetchProfile(id: String) : Future[Profile] = Future {
      // fetching form the DB
      Thread.sleep(random.nextInt(300))
      Profile(id,names(id))
    } // Whenever the fetchProfile called it's create thread for execution.

    def fetchBestFriend(profile : Profile) : Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId =  friends(profile.id)
      Profile(bfId, names(bfId))
    } // same create's the execution of different thread.
  }

  // client : mark to poke bill
  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck") // this send execution on different thread to compute fetchProfile from database.
  mark.onComplete{  // OnCompletion
    case Success(markProfile) => {
      val bill = SocialNetwork.fetchBestFriend(markProfile)
      bill.onComplete{
        case Success(billProfile) => markProfile.poke(billProfile)
        case Failure(e) => e.printStackTrace()
      }
    }
    case Failure(ex) => ex.printStackTrace
  }
  Thread.sleep(3000)
  // Example of the nesting of thread

  //function composition of futures
  //map, flatMap, filter
  val nameOfTheWall = mark.map(profile => profile.name)
  val marksBesFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  val zucksBestFriend = marksBesFriend.filter(profile => profile.name.startsWith("Z"))
  // for-comprehension
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)
  Thread.sleep(1000)


  // fallbacks
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover{
    case e: Throwable => Profile("fb.id.0-dummy","Forever alone")
  }

  val aFetchProfileNoMatterWhat = SocialNetwork.fetchProfile("unknow id").recover {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
  }

  val fallbackResult = SocialNetwork.fetchProfile("unknown id").fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))

  Thread.sleep(1000)
  // cp block on future
  case class User(name : String)
  case class Transaction(sender : String, receiver : String,amount : Double, Status : String)

  object BankingApp {
    val name = "Rock the JVM Banking"

    def fetchUser(name : String): Future[User] = Future {
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName : String, amount : Double): Future[Transaction] = Future {
      Thread.sleep(500)
      Transaction(user.name,merchantName,amount,"Success")
    }

    def purchase(username : String, item: String, merchantName : String, cost : Double): String = {

      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user,merchantName,cost)
      }yield transaction.Status

      Await.result(transactionStatusFuture, 2.seconds) // implicit conversions -> pimp my library
    }

  }

  println(BankingApp.purchase("Daniel", "iphone 12", "rock the jvm store", 300))

  val promise = Promise[Int]() // "controller" over a future
  val future = promise.future

  // thread 1 - "consumer"
  future.onComplete {
    case Success(r) => println("[consumer] I've received " + r)
  }

  // thread 2 - "producer"
  val producer = new Thread(() => {
    println("[producer] crunching numbers...")
    Thread.sleep(500)
    // "fulfilling" the promise
    promise.success(42)
    println("[producer] done")
  })

  producer.start()
  Thread.sleep(1000)


    /*
      1) fulfill a future IMMEDIATELY with a value
      2) inSequence(fa, fb)
      3) first(fa, fb) => new future with the first value of the two futures
      4) last(fa, fb) => new future with the last value
      5) retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T]
     */
  def immediateReturn[T] (value : T) = Future(value)

  def inSequece[A,B](fa : Future[A] , fb : Future[B]): Future[B] ={
    fa.flatMap(_ => fb) // once future a is completed we have to apply value to future b
  }
  val fa = immediateReturn(42)
  val fb = immediateReturn(43)
  inSequece(fa,fb)
  // first out of two future(Thread)
  def first[A](fa : Future[A], fb : Future[A]): Future[A] ={
    val promise = Promise[A]
    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)
    promise.future
  }

  def last[A](fa: Future[A], fb: Future[A]): Future[A] = {
    // 1 promise which both futures will try to complete
    // 2 promise which the LAST future will complete
    val bothPromise = Promise[A]
    val lastPromise = Promise[A]
    val checkAndComplete = (result: Try[A]) =>
      if (!bothPromise.tryComplete(result))
        lastPromise.complete(result)

    fa.onComplete(checkAndComplete)
    fb.onComplete(checkAndComplete)

    lastPromise.future
  }

  val fast = Future {
    Thread.sleep(100)
    42
  }

  val slow = Future{
    Thread.sleep(200)
    43
  }

  first(fast,slow).foreach(f => println("first future value :"+ f))
  last(fast,slow).foreach(f => println("last future value :"+ f))
  Thread.sleep(1000)
  def retryUntil[A](action : () => Future[A],condition: A => Boolean): Future[A] =
    {
      action()
        .filter(condition)
        .recoverWith{
          case _ => retryUntil(action, condition)
        }
    }


  val random = new Random()
  val action = () => Future{
    Thread.sleep(100)
    val nextValue = random.nextInt()
    println("generated" + nextValue)
    nextValue
  }
  retryUntil(action,(x : Int) => x < 50).foreach(result => println("settled at"+result))
  Thread.sleep(10000)

}
