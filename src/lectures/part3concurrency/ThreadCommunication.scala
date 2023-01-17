package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App{
  /*
    Producer and Consumer
    Producer -> [ ? ] -> Consumer
   */
    class SimpleContainer {
      private var value : Int = 0
      def isEmpty : Boolean = value == 0
    def set(newValue : Int) = value = newValue
      def get = {
        val result = value
        value = 0
        result
      }
    }
  def naiveProdCons() : Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[Consumer] waiting")
      while(container.isEmpty){
        println("[Consumer] actively waiting")
      }
      println("[Consumer] I have consumed" + container.get)
    })

    val producer = new Thread( () => {
      println("[producer] computing")
      Thread.sleep(500)
      val value = 42
      println("[Producer] I have Produced, after long work, the value "+ value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }
  //naiveProdCons()
  // wait and notify
  // Entering a synchronized expression on an object locks the object:  and AnyRef can have synchronized blocks.

  // wait() on an object's monitor suspends you(the thread) indefinitely  it's just release lock and wait for period
  // notify() all the sleeping thread they may continue after that it's remove lock from it

  def smartProdCons() ={
    val container = new SimpleContainer
    val consumer = new Thread(() => {
      println("[Consumer] waiting... ")
      container.synchronized{
        container.wait()
      }
      println("[Consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[Producer] Hard at work ...")
      Thread.sleep(2000)
      val value = 42
      container.synchronized{
        println("[producer] I'm producing "+ value)
        container.set(value)
        container.notify()
      }
    })
    consumer.start()
    producer.start()
  }

  //smartProdCons()
  /*
  producer -> [ ? ? ? ] -> Consumer
   */

  def prodConsLargeBuffer(): Unit = {
    val buffer : mutable.Queue[Int] = new mutable.Queue[Int]  // Shared Queue with producer and consumer doing operation on it.
    val capacity = 3 // capacity of the Queue.
    val consumer = new Thread( () => { // Consumer Thread
      val random = new Random()
      while(true) {
        buffer.synchronized{ // we synchronized which create lock on the buffer can only process and thread at a time
          if(buffer.isEmpty){
            println("[Consumer] buffer empty, waiting ...")
            buffer.wait() // this block thread and release lock from the buffer
          }
          // now there is atmost one item to be consumed.
          val x = buffer.dequeue()  // consumed value from buffer.
          println("[Consumer] consumed "+ x)

          //todo
          buffer.notify() // notify some producer thread that there is place which can filled.
        }
        Thread.sleep(random.nextInt(500))
      }
    })

    val producer = new Thread(() => {
      val random = new Random()
      var i = 0
      while(true){
        buffer.synchronized {
          if(buffer.size == capacity) {
            println("[Producer] buffer is full, waiting...")
            buffer.wait // block thread and realease
          }
          //there must be at least ONE EMPTY SPACE in the buffer
          println("[Producer] producing " + i)
          buffer.enqueue(i) // produce value to buffer.
          //todo
          buffer.notify()  // consumer thread consumed the value.
          i += 1
        }
        Thread.sleep(random.nextInt(250))
      }
    })
    consumer.start()
    producer.start()
  }
  prodConsLargeBuffer()

  /*
  Prod - Cons, level 3
  multiple producer and multiple consumer.
   */

  class Consumer(id: Int, buffer : mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      while (true) {
        buffer.synchronized { // we synchronized which create lock on the buffer can only process and thread at a time
          while (buffer.isEmpty) {
            println(s"[Consumer $id] buffer empty, waiting ...")
            buffer.wait() // this block thread and release lock from the buffer
          }
          // now there is at most one item to be consumed.
          val x = buffer.dequeue() // consumed value from buffer.
          println(s"[Consumer $id] consumed " + x)

          //todo
          buffer.notify() // notify some producer thread that there is place which can filled.
        }
        Thread.sleep(random.nextInt(250))
      }
    }
  }

  class Producer(id: Int, buffer : mutable.Queue[Int],capacity : Int) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      var i = 0
      while (true) {
        buffer.synchronized {
          while(buffer.size == capacity) {
            println(s"[Producer $id] buffer is full, waiting...")
            buffer.wait // block thread and realease
          }
          //there must be at least ONE EMPTY SPACE in the buffer
          println(s"[Producer $id] producing " + i)
          buffer.enqueue(i) // produce value to buffer.
          //todo
          buffer.notify() // consumer thread consumed the value.
          i += 1
        }
        Thread.sleep(random.nextInt(500))
      }
    }

  }

 def multiProdCons(nConsumers: Int, nProducers :Int): Unit = {
   val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
   val capacity = 20
   (1 to nConsumers).foreach( i => new Consumer(i,buffer).start())
   (1 to nProducers).foreach( i => new Producer(i, buffer, capacity).start())
 }
 // multiProdCons(3,6)

  /*
   Exercise
   1) think of an example where notifyAll acts in a different way than notify ?
   2) create a deadlock
   3) create a livelock
   */

  //notifyall

  def testNotifyAll(): Unit = {
    val bell = new Object

    (1 to 10).foreach( i => new Thread(() => {
      bell.synchronized{
        println(s"[thread $i ] waiting")
        bell.wait()
        println(s"[Thread $i ] hooray!")
      }
    }).start())

    new Thread(() => {
      Thread.sleep(1000)
      bell.synchronized{
        println(s"[Accouncer] Rock n Roll")
        bell.notify()
      }
    }).start()
  }
  //testNotifyAll()


  // 2 - deadlock
  case class Friend(name : String) {
    def bow(other : Friend) ={
      this.synchronized{
        println(s"$name is bowing to $other")
        other.rise(this)
        println(s"$this: my friend $other is risen")
      }
    }
    def rise(other : Friend) = {
      this.synchronized{
        println(s"$this: I am rising to my friend $other")
      }
    }

    var side = "right"
    def switchSide() : Unit = {
      if(side == "right") side = "left"
      else side = "right"
    }
    def pass(other : Friend) = {
      while(this.side == other.side) {
        println(s"$this, oh, but please, $other, feel free to pass")
        switchSide()
        Thread.sleep(1000)
      }
    }
  }

  val sam = Friend("Sam")
  val pierre = Friend("Pierre")

//  new Thread(() => sam.bow(pierre)).start() // sam's lock   |  pierre lock
//  new Thread(() => pierre.bow(sam)).start() // pierre lock  |  sam's lock

 // new Thread(() => sam.pass(pierre)).start()
  //new Thread(() => pierre.pass(sam)).start()

}
