package exercises

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object AsynNonBlocking {
  def blockingFunction(x: Int): Int = {
    Thread.sleep(10000)
    x + 42
  }
  blockingFunction(5)
  val meaningOfLife = 42
  // asynchronous blocking call

  def asyncBlockingFunction(x : Int): Future[Int] = Future {
    Thread.sleep(10000)
    x + 42
  }



  asyncBlockingFunction(5)
  val anotherMeaningOfLife = 43



  // asynchronous, NON - blocking

//  def createSimpelActor() = Behavior.reciv
//  def main(args : Array[String]) : Unit = {

//  }

}
