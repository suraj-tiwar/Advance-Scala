package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App{

  //JVM threads
  /*
      interface Runnable {
        public void run()
      }
   */
  val aThread = new Thread(new Runnable { // Runnable interface   this is first thread.
    override def run(): Unit = println("Running in parallel")
  })
//  aThread.start() // gives the signal to the JVM to start a JVM thread.
//  // create a JVM thread => OS thread
//  aThread.join() // blocks until aThread finishes running.
//
//  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))   // second thread
// // blocks until aThread finishes running.
//  val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("Goodbye"))) // Third thread
//  threadHello.start()
//  threadGoodbye.start()
//  // different runs produce different results!

  val pool = Executors.newFixedThreadPool(10) // creating 10 thread
  pool.execute(() => println("Something in the thread pool"))   // Assigning task to pool which has 10 thread

//  pool.execute(() => {  // Assigning task to pool which has 10 thread
//    Thread.sleep(1000)
//    println("done after 1 second")
//  })
//
//  pool.execute(() => {  // Assigning task to pool which has 10 thread
//    Thread.sleep(1000)
//    println("almost done")
//    Thread.sleep(1000)
//    println("done after 2 seconds")
//    })
  pool.shutdown() // will allow previously submitted task to execute before termination and not accepting new task.
  //pool.execute(() => println("should not appear")) // RejectedExecutionException
 // pool.shutdownNow() // method prevents waiting task to starting up and attempts to stop currently executed tasks.
  println(pool.isShutdown)
//
//  def runInParallel = {
//    var x = 0
//    val thread1 = new Thread( () => {
//      x = 1
//    })
//    val thread2 = new Thread(() => {
//      x = 2
//    })
//    thread1.start()
//    thread2.start()
//    println(x)
//  }

//  for(_ <- 1 to 100) runInParallel
  // race condition
  class BankAccount(var amount : Int) {
    override def toString: String = "" + amount
  }
//  def buy(account : BankAccount, thing : String, price : Int) ={
//    account.amount -= price
//  }
//
//  for( _ <- 1 to 1000){
//    val account = new BankAccount(50000)
//    val thread1 = new Thread(() => buySafe(account,"shoes",3000))
//    val thread2 = new Thread(() => buySafe(account,"iPhone12", 4000))
//
//    thread1.start()
//    thread2.start()
//    Thread.sleep(10)
//    if(account.amount != 43000) println("AHA: "+ account.amount) // Race condition to change state of object and not interacting with each other.
//  }
//  // to deal with these race condition.
//
//  def buySafe(account : BankAccount,thing : String,price : Int): Unit = {
//    account.synchronized {
//      // no two threads can evaluate this at the same time.
//      account.amount -= price
//      println("I've bought" + thing)
//      println("my account is now " + account)
//    }
//  }

  def inceptionThread(maxThreads : Int , i : Int): Thread = new Thread( () => { // creates new thread
    if(i < maxThreads) {
      val newThread = inceptionThread(maxThreads, i + 1)
      newThread.start() // it's start new Thread which was created by the inceptionThread.
      newThread.join() // it's wait for the Thread to finish.
    } //  This new Thread on this thread and pass that thread to the inceptionThread(newThread)
    println(s"hello from thread $i") // Current thread index display for concurrent
  })
  inceptionThread(50,1).start()

  /*
  2
   */
  var x = 0
  var threads = ( 1 to 100).map(_ => new Thread(() => x += 1)) // This creates 100 threads
  threads.foreach(_.start()) // This start all 100 threads.
  /*
    1) what is biggest value possible for x? 100 // case when all the Thread start one after another
    2) what is the smallest value possible for x ? 1
   */
  println(x)
  /*
   3. sleep fallacy
   */
  var message = ""
  val awesomeThread = new Thread( () => {
    Thread.sleep(1000)
    message = "Scala is awesome"
  })
  message = "Scala sucks"
  awesomeThread.start()
  awesomeThread.join()
  println(message)


}
