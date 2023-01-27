package lectures.part5ts

object Variance extends App{

  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal
  // what is variance ?
  // "inheritance" - type substitution of generics

  class Cage[T]
  // yes - covariance
  class CCage[+T]
  val ccage : CCage[Animal] = new CCage[Cat]
  class InvariantCage[T](animal : T) // invariant
  //class CovariantCage[-T](val animal : T) // COVARIANT POSITION
  // class ContravariantCage[-T](val animal : T)
  class Kitty extends Cat
  class MyList[+A] {
    def add[B >: A](element : B) : MyList[B] = new MyList[B]
  }

  val emptyList = new MyList[Kitty] //  created MyList with Kitty
  val animal = emptyList.add(new Kitty) // if add Kitty is ok
  val moreAnimal = emptyList.add(new Cat) // now if we add the cat to MyList[Kitty] it's return and MyList[Cat] which is the nearest ancestor
  val evenMoreAnimal = moreAnimal.add(new Dog) // now if we add the Dow to MyList[Cats] it's return MyList[Animal] which is the nearest ancestor of the cats and dog
  // METHOD ARGUMENT ARE IN CONTRAVARIANT POSITION
  // return types

  class PetShop[-T]  {
    def get[S <: T](isItaPuppy: Boolean, defaultAnimal: S): S = defaultAnimal
   }

  val shop : PetShop[Animal] = new PetShop[Animal]
  class TeraNova extends Dog
  val bigFurry = shop.get(true,new TeraNova)


  // Big Rule
  /*
    method arguments are in CONTRAVARIANT position
    method types are in COVARIANT position
   */
  /**
   * 1.) Invariant , covariant, contravariant
   * Parking[T](things: List[T]) {
   *  park(vehicle: T)
   *  impount(vehicles : List[T])
   *  checkVehicles(conditions: String): List[T]
   *
   * }
   */

  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle
  class IList[T]

  class IParking[T](vehicles : List[T]) {
    def park(vehicle: T) : IParking[T] = ???
    def impound(vehicles: List[T]): IParking[T] = ???
    def checkVehicle(conditions : String): List[T] = ???

    def flatMap[S](f: T => IParking[S]): IParking[S] = ???
  }

  class CParking[+T](vehicles : List[T]) {
    def park[S >: T](vehicle: S): CParking[S] = ???
    def impound[S >: T](vehicles: List[S]): CParking[S] = ???
    def checkVehicle(conditions: String): List[T] = ???

    def flatMap[S](f: T => CParking[S]): CParking[S] = ???
  }

  class XParking[-T](vehicles: List[T]) {
    def park(vehicle: T): XParking[T] = ???
    def impound(vehicles: List[T]): XParking[T] = ???
    def checkVehicle[S <: T](conditions: String): List[S] = ???

    def flatMap[R <: T,S](f: R => XParking[S]): XParking[S] = ???
  }

  /*
      Rule of Thumb
      - use convariance = Collection of things
      - use contravariance = GROUP OF ACTIONS

   */
  class CParking2[+T](vehicles: IList[T]) {
    def park[S >: T](vehicle: S): CParking2[S] = ???

    def impound[S >: T](vehicles: IList[S]): CParking2[S] = ???

    def checkVehicle[S >: T](conditions: String): IList[S] = ???
  }

  class XParking2[-T](vehicles: IList[T]) {
    def park(vehicle: T): XParking2[T] = ???

    def impound[S <: T](vehicles: IList[S]): XParking2[S] = ???

    def checkVehicle[S <: T](conditions: String): IList[S] = ???
  }

}
