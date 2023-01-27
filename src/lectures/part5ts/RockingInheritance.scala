package lectures.part5ts

object RockingInheritance extends App{
  trait Writer[T] {
    def write(value : T): Unit
  }
  trait Closeable{
    def close(status : Int): Unit
  }
  trait GenericStream[T] {
    def foreach(f : T => Unit): Unit
  }

  def processStream[T](stream : GenericStream[T] with Writer[T] with Closeable): Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  // diamond problem

  trait Animal{ def name : String}
  trait Lion extends Animal {
    override def name: String = "lion"}
  trait Tiger extends Animal {
    override def name: String = "tiger"}
  class Mutant extends Lion with Tiger

  val m = new Mutant
  println(m.name)
  /*
    Last Override get's picked
   */
  // the super problem + type linearization

  trait Cold
  {
    def print : Unit = println("Cold")
  }

  trait Red extends Cold {
    override def print(): Unit = {
      println("Red")
      super.print
    }
  }
  trait Blue extends Cold {
    override def print(): Unit = {
      println("Blue")
      super.print
    }
  }
    class Green {
      def print() : Unit = {
        println("Green")
      }
    }

  class White extends Green with Blue with Red {
    override def print(): Unit = {
      println("Walter White")
      super.print()
    }
  }
  val white = new White
  white.print()



}
