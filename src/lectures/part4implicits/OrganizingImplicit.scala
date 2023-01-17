package lectures.part4implicits

object OrganizingImplicit extends App {


  println(List(1,2,3,4,5).sorted) // This ordering traits in scala predef function takes the execution
  implicit def reverseOrdering : Ordering[Int] = Ordering.fromLessThan(_ > _)
  println(List(1,2,3,4,5).sorted) // This takes implicit define ordering[Int]

  /*
  Implicits (used as implicit as parameters)
    - val/var
    - object
    - accessor method = defs with no parentheses
   */
  // Exercise
  case class Person(name : String, age : Int)

  val persons = List(
    Person("Steve",30),
    Person("Amy",22),
    Person("John",66)
  )

  /*
    Implicit Scope
     - normal scope = Local Scope
     - Imported Scope
     - Companions of all types involved in method signature
   */
  object AlphabeticNameOrdering {
    implicit val alphabeticOrdering : Ordering[Person] = Ordering.fromLessThan((a,b) => a.name.compareTo(b.name) < 0)
  }

  object AgeOrdering {
    implicit val ageOrdering: Ordering[Person] = Ordering.fromLessThan((a,b) => a.age < b.age)
  }

  import AgeOrdering._
  println(persons.sorted)

  case class Purchase(nUnits : Int,unitPrice : Double)
  object Purchase
  {
    implicit val totalPrice : Ordering[Purchase] = Ordering.fromLessThan((a,b) => a.nUnits * a.unitPrice < b.nUnits * b.unitPrice)
  }

  object UnitCount {
    implicit val unitCount : Ordering[Purchase] = Ordering.fromLessThan((a,b) => a.nUnits < b.nUnits)
  }

  object UnitPrice {
    implicit val unitPrice : Ordering[Purchase] = Ordering.fromLessThan((a,b) => a.unitPrice < b.unitPrice)
  }

  val purchases = List(
    Purchase(4,45),
    Purchase(5,45),
    Purchase(6,45),
  )

  println(purchases.sorted)

}
