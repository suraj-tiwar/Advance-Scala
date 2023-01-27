package lectures.part4implicits

import scala.concurrent.Future

object MagnentPattern extends App {
  // method
  // 1 - No more type eraser problem.
  class P2PRequest
  class P2PResponse
  class Serializer[T]
  trait Actor {
    def receive(statusCode : Int) : Int
    def receive(request : P2PRequest) : Int
    def receive(response : P2PResponse) : Int
    def receive[T : Serializer](message : T) : Int
    def receive[T : Serializer](message : T , statusCode : Int): Int
    def receive(future : Future[P2PRequest]): Int
    //def receive(future : Future[P2PRequest]): Int
    // lots of overloads
  }

  /*
    1 - type erasure
    2 - lifting doesn't work for all overloads
        val receiveFV = receive _ // ?!
    3 - code duplication
    4 - type inferrence and default args

        actor.receive(?!)
   */

  trait MessageMagnet[Result] {
    def apply() : Result
  }

  def receive[R](magnet : MessageMagnet[R]) : R = magnet()

  implicit class FromP2PRequest(request : P2PRequest) extends MessageMagnet[Int] {
    def apply() : Int = {
      // login to handling a P2PRequest
      println("Handling P2P request")
      42
    }
  }

  implicit class FromP2PResponse(request: P2PResponse) extends MessageMagnet[Int] {
    def apply(): Int = {
      // login to handling a P2PResponse
      println("Handling P2P response")
      24
    }
  }
  println(receive(new P2PResponse))
  println(receive(new P2PRequest))

  // 1 - no more type erasure problems!
  implicit class FromResponseFuture(future : Future[P2PResponse]) extends MessageMagnet[Int] {
    override def apply() : Int = 2
  }

  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int] {
    override def apply(): Int = 3
  }

}
