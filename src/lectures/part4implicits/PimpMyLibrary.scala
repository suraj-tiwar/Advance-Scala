package lectures.part4implicits

object PimpMyLibrary extends App {
  //2.isPrime
  implicit class RichInt(value : Int) {
    def isEven : Boolean = value % 2 == 0
    def sqrt : Double = Math.sqrt(value)

    def times(function: () => Unit) : Unit = {
      def timeAux(n :Int): Unit =
        {
          if( n <= 1) function
          else
            {
              function()
              timeAux(n-1)
            }
        }
        timeAux(value)
    }

    def *[T](list : List[T]): List[T] = {
      def concatenate(n : Int):List[T] =
        if(n <= 0) List()
        else concatenate(n-1) ++ list
      concatenate(value)
    }
  }
  // type enrichment = pimping
  42.isEven
  import scala.concurrent.duration._
  2.second
  // compiler doesn't do multiple implicit searches.

  /*
  Enrich the string class
  - asInt
  - encrypt
   John -> Lnjp

   Keep enriching the Int class
    - times(function)
    3.times(() => ...)
   */
  implicit class RichString(string : String) {
    def asInt: Int = Integer.valueOf(string) // java.lang.Integer -> Int
    def encrypt(cypherDistance : Int) : String = string.map(c => ( c + cypherDistance).asInstanceOf[Char])
  }
  println("3".asInt + 4)
  println("John".encrypt(2))

  println(3.times(() => println("Scala, Rocks")))
  println(4 * List(1,2))
  implicit def stringToInt(string : String): Int = Integer.valueOf(string)

  println("6" / 2)

  implicit def intToBoolean(i : Int) : Boolean = i == 1
  val aConditionValue = if(3) "OK" else "Something wrong"
  println(aConditionValue)
}
