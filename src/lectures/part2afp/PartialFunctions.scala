package lectures.part2afp

object PartialFunctions extends App {
  val aFunction = (x :Int) => x + 1  // Function1[Int,Int] === Int => Int

  val aFussyFunction = ( x : Int) =>
    if(x == 1) 42
    else if (x == 2) 56
    else if (x == 3) 9999
    else throw new FunctionNotApplicableException
    class FunctionNotApplicableException extends RuntimeException
    val aNiceFussyFunction = (x : Int) => x match {
      case 1 => 42
      case 2 => 56
      case 5 => 999
    }
  //println(aFussyFunction(5))
  println(aNiceFussyFunction(2))
 // println(aNiceFussyFunction(6))
  val aPartialFunction : PartialFunction[Int,Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 96
  }
  println(aPartialFunction(2))
  // PF utilities
  println(aPartialFunction.isDefinedAt(67)) // isDefinedAt(Int) return boolean  we can check for the valid function implementation.
  // lift
  val lifted = aPartialFunction.lift // return Option[Int] Some(Int) or None(Int)
  println(lifted(2))
  println(lifted(96))
  val pfChain = aPartialFunction.orElse[Int,Int] {
    case 45 => 67
  }
  println(pfChain(45))
  println(pfChain(2))

  // pf extends normal functions
  val aTotalFunction : Int => Int = {
    case 1 => 99
  } // as we declared with normal functions we can pass the partial function literals in TotalFunction definition as Partial Function is subType normal Function
  // HOF accept partial Function as well // HOF accept function and return function
  val aMappedList = List(1,2,3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }
  println(aMappedList)
  /*
   Note:
   Partial Function can only have one parameter type
   */

  /**
   * Exercises
   * 1 - Construct a PF instance (anonymous class)
   * 2 - dumb chatbot as a PF
   */
  val aManualFussyFunction = new PartialFunction[Int,Int] {
    override def apply(v1: Int): Int = v1 match {
      case 1 => 10
      case 2 => 20
      case 3 => 30
    }

    override def isDefinedAt(x: Int): Boolean =
      x == 2 || x == 3 || x ==4
  } // classic way of declaring the anonymous function and overriding the key implementation of apply method and isDefined Method in Partial Function Traits.
 println(aManualFussyFunction(2))
  val chatBot : PartialFunction[String,String] = {
    case "hello" => "Hi, name is HAL90000"
    case "goodbye" => "Once you start talking to me, there is no return, human world"
    case "call mom" => "Unable to find your phone without your credit card"
  }
  scala.io.Source.stdin.getLines().map(chatBot).foreach(println)


}
