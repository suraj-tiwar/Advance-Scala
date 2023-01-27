package exercises

import lectures.part4implicits.TypeClasses.User

object EqualityPlayground extends App {
  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }

  object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  implicit object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
  }

  object Equal {
    def apply[T](a : T, b : T)(implicit equalizer: Equal[T]): Boolean = equalizer(a,b)
  }
  val john = User("John",32,"john@rockthjvm.com")
  val anotherJohn = User("John",45,"anotherJohn@rtjvm.com")
  println(Equal(john,anotherJohn))

  // AD-HOC polymorphism

  /*
    Exercise
   */

  implicit class TypeSafeEqual[T](value : T) {
    def === (other : T)(implicit equalizer: Equal[T]): Boolean = equalizer.apply(value,other)
    def !==( other : T)(implicit equalizer : Equal[T]): Boolean = ! equalizer.apply(value,other)
  }

  println(john === anotherJohn)
  /*
    john.===(anotherJohn)
    new TypeSafeEqual[User](john).===(anotherJoin)
    new TypeSafeEqual[User](john).===(anotherJoin)(NameEquality)
   */


}
