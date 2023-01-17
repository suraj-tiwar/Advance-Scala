package exercises


abstract class MyStream[+A] {
  def isEmpty: Boolean

  def head: A

  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[B] // prepend operator

  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B]

  def foreach(f: A => Unit): Unit

  def map[B](f: A => B): MyStream[B]

  def flatMap[B](f: A => MyStream[B]): MyStream[B]

  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A]

  def takeAsList(n: Int): List[A]
  /*
  [1,2,3].toList([])
  [2,3].toList([1])
  [3].toList([2,1])
  [].toList([ 3 ,2, 1])
   = [1,2,3]
   */

  final def toList[B >: A](acc : List[B] = Nil) : List[B] =
    if(isEmpty) acc
    else tail.toList(head :: acc)
}

object EmptyStream extends MyStream[Nothing] {
  def isEmpty: Boolean = true

  def head:  Nothing = throw new NoSuchElementException

  def tail: MyStream[Nothing] = throw new NoSuchElementException

  def #::[B >: Nothing](element: B): MyStream[B]  = new Cons(element,this)

  def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

  def foreach(f: Nothing => Unit): Unit  = ()

  def map[B](f: Nothing => B): MyStream[B] = this

  def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this

  def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  def take(n: Int): MyStream[Nothing] = this

  def takeAsList(n: Int): List[Nothing] = Nil
}

class Cons[+A](hd : A, t1 : => MyStream[A]) extends MyStream[A]   // tail is called by call by name
{
  def isEmpty: Boolean = false

  override val head: A = hd

  override lazy val tail: MyStream[A] = t1 // made tail as lazy value.
  /*
  val s = new Cons(1, EmptyStream)
  val prepended = 1 #:: s = new Cons(1,s)

   */

  def #::[B >: A](element: B): MyStream[B] = new Cons(element,this)

  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Cons(head,tail ++ anotherStream)

  def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }
  /*
  s = new Cons(1, ?)
  mapped = s.map(_ + 1) = new Cons(2,s.tail.map(_ + 1))  Evaluation needed to be done on by need basis
   */

  def map[B](f: A => B): MyStream[B] = new Cons(f(head),tail.map(f)) // preserves lazy evaluation

  def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f) // evaluation done as we have made tail call by name value.

  def filter(predicate: A => Boolean): MyStream[A] = if(predicate(head)) new Cons(head,tail.filter(predicate)) else tail.filter(predicate)

  def take(n: Int): MyStream[A] = {
    if( n < 0) EmptyStream
    else if(n == 1) new Cons(head, EmptyStream)
    else new Cons(head,tail.take(n-1))
  }

  def takeAsList(n: Int): List[A] = toList(List())
}

object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] =
    new Cons(start,MyStream.from(generator(start))(generator))
}
object StreamPlayground extends App{
 val natural = MyStream.from(1)(_+1)
  println(natural.head)
  println(natural.tail.head)
  println(natural.tail.tail.head)
  val startFrom0 = 0 #:: natural
  println(startFrom0.head)
  startFrom0.take(10000).foreach(println)
  println(startFrom0.map(_ * 2).take(100).toList())
  println(startFrom0.flatMap(x => new Cons(x, new Cons(x + 1,EmptyStream))).take(10).toList())
  println(startFrom0.filter(x => x < 10).take(10).toList())

  // Exercise on streams
  // 1 - Stream of Fibonacci numbers infinite
  // 2 - stream of prime numbers with Eratosthenes' sieve
  // [ 2 ,3, 4, ..... ]
  // filter out all numbers divisible by 2 ,3 ,5, 7, 11, 13, 17, 19, 23.
  // [ first,

  def fibo(first : BigInt,second : BigInt) : MyStream[BigInt] = new Cons(first,fibo(second,first+second)) // infinite set of fibonacci series
  val fibonanci = fibo(0,1)
  println(fibonanci.take(100).toList())
  println(fibo(1,1).take(100).toList())

  def eratosthenes(numbers: MyStream[Int]) : MyStream[Int] =
    if(numbers.isEmpty) numbers
    else new Cons(numbers.head,eratosthenes(numbers.filter(x => x % numbers.head != 0)))

  val numberStartingFrom2 = MyStream.from(2)(x => x + 1)
  println(eratosthenes(numberStartingFrom2).take(25).toList())


}
