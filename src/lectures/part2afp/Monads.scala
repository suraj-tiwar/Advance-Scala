package lectures.part2afp

object Monads extends App{

  // our own Try monad.
  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }
  object Attempt{
    def apply[A](a : => A) : Attempt[A] = {
      try {
        Success(a)
      }
      catch {
      case e : Throwable => Fail(e)
      }
    }
  }
  case class Success[+A](values : A) extends Attempt[A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B] =
      try{
        f(values)
      } catch {
        case e : Throwable => Fail(e)
      }
  }
  case class Fail(e : Throwable) extends Attempt[Nothing] {
    def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }
  /*

   */
  val attempt = Attempt{
    throw new RuntimeException("My own monad, yes!")
  }
  println(attempt)

  /*
    EXERCISE : Implementation a Lazy[T] monad = computation will only be executed when it's needed.
    unit/apply
    flatMap

    2) Monads = unit + flatMap
       Monad =unit + map + flatten
   */
}

