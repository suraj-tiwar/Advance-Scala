package lectures.part1as

import scala.::

object DarkSugars extends App {
  // syntax sugar #1 : Methods with single param
  def singleArgMethod(arg : Int) : String = s"$arg little ducks..."
  val description = singleArgMethod {
    // we can write some code for computational it's same as code block take's last n
    42
  }
  List(1,2,3).map{
    x => x+1
  }

  // syntax sugar #2: single abstract method

  trait Action {
    def act(n :Int) : Int
  } // trait with single instance
  val anInstance = new Action{  // this creates anonymous classes which extends Action trait
    override def act(n :Int): Int = n+1
  }
  val aFunckyInstance: Action = (x:Int) => x  + 1
  val aThread = new Thread(new Runnable {   // this is java way of doing to run the thread
    override def run(): Unit = println("hello, Scala")
  })
  val aSweeterThread = new Thread(() => println("sweet, Scala"))  // syntactic sugar

  abstract class AnAbstractType {
    def implemented : Int = 32
    def f(a:Int) : Unit // unimplemented method.
  }

  val anAbstractInstance: AnAbstractType = (a : Int) => println("sweet") // Anonymous class

  // syntax sugar #3: the :: and #:: methods are special
  val prependedList = 2 :: List(3,4)
  // 2.::(List(3,4))
  // List(3,4).::(2) //compiler does it
  // scala spec : last char decides associativity of method.
  // : is right associative
  1 :: 2 :: 3 :: List(4,5)
  List(4,5).::(3).::(2).::(1)
  class MyStream[T] {
    def -->:(value : T): MyStream[T] = this
  }
  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]
  println(myStream)

  //syntax sugar #4: multi-word method naming

  class TeenGirl(name : String) {
    def `and then said`(goss : String) = println(s"$name said $goss")
  }
  val lily = new TeenGirl("lily")
  lily `and then said` "scala is functional"

  // syntax sugar #5: infix types
  class Composite[A,B]
  val composite : Composite[Int,String] = ???
  val composite1 : Int Composite String = ??? // this get converted by compiler to above code.
  class -->[A,B]
  val towards : Int --> String = ???

  // syntax sugar #6: update() is very special, much like apply()
  val anArray = Array(1,2,3)
  anArray(2) = 7 // rewritten as to anArray.update(2,7)
  // used in mutable collections
  //remember apply() and update()!

  // syntax sugar #7:
  class Mutable {
    private var internalMember = 0
    def member = internalMember // getter
    def member_=(value : Int): Unit = // setter in scala
      internalMember = value
  }
  val mutableInstance = new Mutable
  mutableInstance.member // getter
  mutableInstance.member = 10 // rewritten as mutable.member_(10)

}
