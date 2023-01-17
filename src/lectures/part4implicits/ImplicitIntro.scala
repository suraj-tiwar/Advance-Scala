package lectures.part4implicits

object ImplicitIntro extends App {
  val pair = "Daniel" -> "555"

  case class Person(name: String) {
    def greet = s"Hi, my name is $name"
  }

  implicit def stringToPerson(name: String): Person = Person(name)

  println("Suraj".greet) // compiler looks for all object, methods, values.

  // println(stringToPerson("Suraj").greet) compiler convert's above line to this with help implicit define over the function
  class A {
    def greet: Int = 2
  }

  //  implicit def fromStringToA(str : String) : A = new A
  def incr(x: Int)(implicit amount: Int) = x + amount
  implicit val defaultAmount = 10
  println(incr(5))

}
