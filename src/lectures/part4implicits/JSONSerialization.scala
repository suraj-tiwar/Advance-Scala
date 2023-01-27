package lectures.part4implicits

import java.util.Date

object JSONSerialization extends App {

  case class User(name : String, age: Int, email: String)
  case class Post(content : String,createdAt : Date)
  case class Feed(user:  User,post : List[Post])

  /*
    1 - intermediate data types to  Int,String,List,Date
    2 - type classes for conversion to intermediate data types.
    3 - serialize to JSON

   */

  sealed trait JSONValue {
    def stringify : String
  }

  final case class JSONString(values : String) extends JSONValue {
    def stringify : String = values
  } // intermediate data types to
  final case class JSONNumber(values : Int) extends JSONValue {
    def stringify : String  = values.toString
  } // intermediate data types
  final case class JSONArray(values : List[JSONValue]) extends JSONValue {
    def stringify : String = values.map(_.stringify).mkString("[",",","]")
  } // intermediate data types
  final case class JSONObject(values : Map[String,JSONValue]) extends JSONValue {
   /*
   {
      name : "John"
      age  :  22
      friends: [ ... ]
      lastestPost : {

    */
    def stringify : String = values.map{
      case (key,value) => "\""+ key + "\":" + value.stringify
    }.mkString("{",",","}")
  } // intermediate data types

  val data = JSONObject(Map(
    "user" -> JSONString("Suraj"),
    "posts" -> JSONArray(List(
      JSONString("Scala Rocks!"),
      JSONNumber(453)
    ))
  ))

  println(data.stringify)

  trait JSONConverter[T] {
    def convert(value : T) : JSONValue
  }

  implicit class JSONOps[T](value: T) {
    def toJSON(implicit converter: JSONConverter[T]): JSONValue = converter.convert(value)
  }
  implicit object StringConverted extends JSONConverter[String] {
    def convert(value : String): JSONValue = JSONString(value)
  }

  implicit object NumberConverted extends JSONConverter[Int] {
    def convert(value: Int): JSONValue = JSONNumber(value)
  }

  implicit object UserConverter extends JSONConverter[User] {
    def convert(user : User) : JSONValue = JSONObject(Map(
      "name" -> JSONString(user.name),
      "age" -> JSONNumber(user.age),
      "email" -> JSONString(user.email)
    )
    )
  }

  implicit object PostConverter extends JSONConverter[Post]{
    def convert(post : Post): JSONValue = JSONObject(Map(
      "content" -> JSONString(post.content),
      "created" -> JSONString(post.createdAt.toString)
    ))
  }

  implicit object FeedConverter extends JSONConverter[Feed] {
    def convert(feed : Feed): JSONValue = JSONObject(Map(
      "user" -> feed.user.toJSON,//todo
      "posts" -> JSONArray(feed.post.map(_.toJSON)) // todo
    ))
  }


  val now = new Date(System.currentTimeMillis())
  val john = User("John",34,"john@rockTheJVM.com")
  val feed = Feed(john,List(
    Post("hello",now),
    Post("look at this cute puppy", now)
  ))

  println("Suraj".toJSON.stringify)
  println(john.toJSON.stringify)
  println(feed.toJSON.stringify)




}
