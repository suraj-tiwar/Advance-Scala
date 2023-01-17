package lectures.part3concurrency
import java.util.concurrent.atomic.AtomicReference
import scala.collection.parallel.ForkJoinTaskSupport
import scala.collection.parallel.immutable.ParVector
import scala.concurrent.forkjoin.ForkJoinPool

object ParallelUtils extends App{
  // 1 - Parallel collections - Operation on them are handle by multiple thread at same time.
  val parList = List(1, 2, 3).par // parallel version of list
  val aParVector = ParVector[Int](1,2,3)
  /*

  Seq
  Vector
  Array
  Map - Hash,Trie
  Set-Hash, Trie
   */
  def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }
  val list = (1 to 100).toList
  val serialTime = measure {
    list.map(_ + 1)
  }
  val parallelTime = measure {
    list.par.map(_ + 1)
  }
  println("Serial time for computing list: " +serialTime )
  println("parallel time for computing list: " +parallelTime)
  /*
    Map - reduce model // divide and conquer
    - split the elements into chunks - splitter
    - operation
    - recombine - Combiner

    fold,reduce with non-associative operators
   */
  println(List(1,2,3).reduce(_ - _))
  println(List(1,2,3).par.reduce(_ - _))
  var sum = 0
  List(1,2,3).par.foreach(sum += _)
  println(sum) // race condition

  // configuring
  aParVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))


  // 2 - atomic ops and references // not partial implementation( Either Fully thread is implemented or Thread is not implemented at all)
  val atomic = new AtomicReference[Int](2) // This does creates AtomicReference
  val currentValue = atomic.get() // Thread - safe read.
  atomic.set(4) // Thread - safe write
  atomic.getAndSet(5) // Thread - safe read - write.
  atomic.compareAndSet(38,56) // This first compare value with ref and seen it's shallow equality or not then set and value updateValue
  atomic.updateAndGet(_ + 1)
  atomic.getAndUpdate(_ + 1)

  atomic.accumulateAndGet(12, _ + _ )
}
