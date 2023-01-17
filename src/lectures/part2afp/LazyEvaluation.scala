package lectures.part2afp

object LazyEvaluation extends App{

  lazy val x : Int = {
    println("hello")
    42
  }
  // lazy DELAYS the evaluation of values whenever we use x it's values get evaluated at that time.
  println(x) // this time's it's get evaluated
  // lazy value evaluate once only when we and then store values after the evaluation
  println(x) //this time doesn't return last store value of evaluation.

  // examples of Implications.
  def sideEffectCondition : Boolean = {
    println("Boo")
    true
  }
  def simpleCondition : Boolean = false
  lazy val lazyCondition = sideEffectCondition
  println(if (simpleCondition &&  lazyCondition) "yes" else "no") // because of the false condition && make decision without evaluating the second variable thus second never get's evaluated
  println(if (simpleCondition ||  lazyCondition) "yes" else "no")
  // in conjunction with call by name
  def byNameMethod(n: => Int) : Int = {
    // CALL BY NEED
    lazy val t = n
    t + t + t + 1
  }
  def retrieveMagicValue ={
    println("Waiting")
    Thread.sleep(1000)
    42
  }

  println(byNameMethod(retrieveMagicValue))
  def lessThan30(i : Int) : Boolean = {
    println(s"$i is less than 30 ?")
    i < 30
  }
  def greaterThan20(i : Int) : Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }
  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30)
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  val lt30Lazy = numbers.withFilter(lessThan30)
  val gt20Lazy = lt30Lazy.withFilter(greaterThan20)  //withFilter it's evaluate for need rather evaluating before for given predicate.
  println(gt20Lazy)
  gt20Lazy.foreach(println)

  // for - comprehension use withFilter with guards
  for {
    a <- List(1,2,3) if a % 2== 0 // use Lazy vals
  } yield a + 1
  List(1,2,3).withFilter( _ % 2 == 0).map(_ + 1) // List[Int]

  /*
    Exercise : Implemented a lazily evaluated, Singly linked STREAM of elements
    naturals = MyStream.from(1)(x => x +1) = Stream of natural numbers (potential infinite)
    naturals.take(100).foreach(println)
    naturals.foreach(println)
    naturals.map(_ * 2)
   */

}
