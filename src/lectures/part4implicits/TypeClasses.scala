package lectures.part4implicits

object TypeClasses extends App {

  trait HTMLWritable
  {
   def toHtml : String
  }

  case class User(name : String, age : Int , email : String) extends HTMLWritable{
    override def toHtml: String = s"<div>$name($age yo) <a href = $email/> </div>"
  }

  User("John",32,"joh@rocktheJvm.com").toHtml

  trait Equal[T] {
    def apply(a : T , b : T): Boolean
  }

  object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }
  object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
  }

  /// part 2

//  object HTMLSerializer {
//    def serialize[T]
//  }
}
