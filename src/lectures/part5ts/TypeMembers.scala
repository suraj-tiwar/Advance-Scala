package lectures.part5ts

object TypeMembers extends App{



  class Animal
  class Dog extends Animal
  class Cat extends Animal
  class AnimalCollection{
    type AnimalType // Abstract type member
    type BoundedAnimal <: Animal // upper bound by animal
    type SuperBoundedAnimal >: Dog <: Animal // lower bounded by Dog and Upper bound by animal
    type AnimalC = Cat // this just alias for type Cat

  }
  val ac = new AnimalCollection
  val dog : ac.AnimalType = ???
  ///val  cat : ac.BoundedAnimal = new Cat

  val pup : ac.SuperBoundedAnimal = new Dog
  val cat : ac.AnimalC = new Cat

  type CatAlias = Cat
  val anotherCat : CatAlias = new Cat

  trait MyList {
    type T
    def add(element : T) : MyList
  }

  class NonEmptyList(value : Int) extends MyList {
    override type T  = Int
    def add(element: Int): MyList = ???
  }
  /*
  Exercise - enforce a type to be applicable to SOME TYPES only
   */
  trait MList {
    type A
    def head : A
    def tail : MList
  }

  trait ApplicableToNumbers {
    type A <: Number
  }

//  class CustomList(hd: String, tl: CustomList) extends MList with ApplicableToNumbers {
//    type A = String
//    def head = hd
//    def tail = tl
//  }

  class  IntList(hd: Int, tl: IntList) extends MList {
    type A = Int
    def head = hd
    def tail = tl
  }
  println("Implemented")


}
