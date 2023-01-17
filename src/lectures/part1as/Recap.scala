package lectures.part1as

import scala.annotation.tailrec

object Recap extends App{
  val aCondition: Boolean = false
  val aConditionedVal = if (aCondition) 42 else 65
  // instruction vs expression
  // code block
  // compiler infers type for us
  val aCodeBlock =
    {
      if(aCondition) 54
      56
    }
    // Unit  -  void
  val theUnit = println("hello, Scala")
  def aFunction(x:Int) : Int = x+1
  // Recursion : stack and tail
  @tailrec
  def fact(n : Int, acc : Int) :  Int = {
    if( n <= 0) acc
    else fact(n-1,acc * n)
  }

  // object-oriented Programming
  class Animal
  class Dog extends Animal
  val aDog : Animal = new Dog  // subtyping polymorphism

  trait Carnivore {
    def eat(a : Animal) : Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("Cruch"+ a)
  }

  // method notation
  val aCroc = new Crocodile
  aCroc eat aDog // inflix notation
  1.+(2) // operator are rewritten as a method.

  // anonymous classes - compiler created anonymous class which extends traits Carnivore and we have to overwrite def eat method
  val aCarnivore = new Carnivore {
    override def eat(a: Animal): Unit = println("Roar")
  }
  // generics
  abstract class MyList[+A]
  // singleton and companions
  object Mylist

  // case class
  case class Person(name : String,age : Int)  //  it's help in apply method and equal toString implementation of the object serialization

  // exceptions and try/catch/finally
  val throwsException = throw new RuntimeException // Nothing
  val aPotentialFailure = try {
    throw new RuntimeException
  }catch {
    case e : Exception => "I caught an exception"
  }finally {
    println("some logs")
  }
  // packing and imports

  // functional programming.
  val incrementer = new Function1[Int,Int]{
    override def apply(v1:Int): Int = v1 +1
  }
  incrementer(1)
  val anonymousIncrementer = (x : Int) => x+1
  List(1,2,3).map(anonymousIncrementer)
  // map,filter,flatMap

  //for comprehension
  val pairs = for {
    num <- List(1,2,3)
    char <- List('a','b','c')
  } yield num + " "+char

  // scala collections : Seqs,Arrays, Lists, Vectors,Maps,Tuples
  val aMap = Map{
    "Daniel" ->789
    "jess" -> 555
  }
  // "Collection" : Options ,try
  val anOption = Some(2)
  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "Second"
    case 3 => "third"
    case _ => x +"th"
  }

  val bob = Person("Bob",22)
  val greeting = bob match {
    case Person(n,_) => s"Hi, my name is $n"
  }
// all the patterns.
}
