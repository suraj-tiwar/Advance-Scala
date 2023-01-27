package lectures.part5ts

object PathDependentTypes extends App{

  class Outer{
    class Inner
    object InnerObject
    type InnerType

    def print(i : Inner) = println(i)
    def printGeneral(i : Outer#Inner) = println(i)
  }

  def aMethod : Int = {
    class HelperClass
    type HelperType  = String
    2
  }

  // per - instance
  val o = new Outer
  val inner = new o.Inner  // o.Inner is a TYPE

  val oo = new Outer
  // val otherInner : oo.Inner = new o.Inner // this does not complier because it's instance has been changed
  o.print(inner)
  //oo.print(inner)
  // path-dependent types
  o.printGeneral(inner)
  oo.printGeneral(inner)
  /*
      Exercise
      DB keyed by Int and String, but maybe others
   */
  trait ItemLike {
    type Key
  }
  trait Item[K] extends ItemLike
  {
    type Key = K
  }
  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???

  get[IntItem](42) // ok
  get[StringItem]("home") // ok
  //get[IntItem]("Scala") // not ok





}
