package lectures.part2afp

object CurriesPAF extends App{

  // curried Function
  val superAdder: Int => Int => Int =
    x => y => x+y
  val add3 = superAdder(3)
  println(add3(4))
  println(superAdder(3)(5)) // Curried Function it's receive multiple parameter list.

  def curriedAdder(x : Int)(y :Int) : Int = x + y // curried Method
  val add4: Int => Int  = curriedAdder(4) // we can not pass the method to the higher order function because we have to pass the parameter to function for that we use the functional values.
  // functions != Method
  def inc(x : Int) = x + 1
  List(1,2,3).map(inc) // ETA-expansion.
  // Partial function application
  val add5 = curriedAdder(5) _ // ETA-Expansion Int => Int

  // EXERCISE
  val simpleAddFunction = (x : Int, y : Int) => x + y
  def simpleAddMethod(x : Int,y : Int) = x + y
  def curriedAdderMethod(x : Int)(y : Int) = x + y

  // add7 : Int => Int = y => 7 + y
  val add71 = curriedAdderMethod(7) _
  val add72 = simpleAddFunction(7,_)
  val add73 = simpleAddMethod(7,_)
  val add74 = (x: Int) => simpleAddMethod(7,x)
  val add75 = (x : Int) => simpleAddFunction(7,x)
  val add76 = (x :Int) => curriedAdderMethod(7)(x)
  def concatenation(a : String,b : String, c : String) = a + b + c
  val insertName = concatenation("Hello, I'm ",_:String,", how are you ?") // underScore does the ETA-Expansion ( x : String => Concatenation(Hello, x , how are you)
  println(insertName("Suraj"))

  val fillInTheBlanks = concatenation("Hello, ",_:String,_:String) // (x,y) => concatenation("Hello", x, y)
  println(fillInTheBlanks("Suraj"," Scala is Awesome"))

  println(add72(4))
  println(add73(4))
  println(add71(4))

  // EXERCISE
  /*
    1. Process a list of numbers and return their string representation with different formats
        use the %4.2f, %8.6f, %14.14 with a curried formatter function
   */
  def curriedFormatter = ( format : String, values : Double) => format.format(values)
  val format42f = curriedFormatter("%4.2f",_ : Double)
  val format86f = curriedFormatter("%8.6f",_ : Double)
  val format1414f = curriedFormatter("%14.14f",_:Double)
  println(curriedFormatter("%4.2f", 3247.32494))
  println(List(5.38420,428540.342894,47329.45345).map(format42f))
  println(List(5.38420,428540.342894,47329.45345).map(format86f))
  println(List(5.38420,428540.342894,47329.45345).map(format1414f))

  /*
   2. Difference between
      -functions vs  methods
      - parameters : by- name vs )-lambda
   */
  def byName(n: => Int) = n + 1
  def byFunction(f: () => Int) = f() + 1

  def method : Int = 42 // accessor method
  def parenMethod(): Int = 42 // parenthesis method

  //calling byName and byFunction with Int
  println(byName(42))
  println(byName(method))
  println(byName(parenMethod()))
  println(byName(parenMethod))
//  println(byName(() => 45))
println(byName((() => 54 )()))






}
