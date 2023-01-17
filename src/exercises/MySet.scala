package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean){
  /*
    EXERCISE - Implement a functional set
   */
  def apply(elem : A) : Boolean = contains(elem)
  def contains(elem : A) : Boolean
  def +(elem : A) : MySet[A]
  def ++(anotherSet : MySet[A]) : MySet[A]
  def -(elem : A) : MySet[A]
  def &(anotherSet: MySet[A]): MySet[A] // Intersection
  def --(anotherSet: MySet[A]): MySet[A] // Difference

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]) : MySet[B]
  def filter(predicate: A => Boolean): MySet[A]
  def forEach(f : A => Unit) : Unit
  def unary_! : MySet[A]
  /*
   EXERCISE
    - removing an element.
    - interaction with another set
    - difference with another set
   */
}
class EmptySet[A] extends MySet[A]{
  def contains(elem: A): Boolean = false

  def +(elem: A): MySet[A] = new NonEmtySet[A](elem,this)

  def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  def -(elem : A) : MySet[A] = this

  def &(anotherSet: MySet[A]): MySet[A] = this // intersection

  def --(anotherSet: MySet[A]): MySet[A] = this // difference

  def map[B](f: A => B): MySet[B] = new EmptySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

  def filter(predicate: A => Boolean): MySet[A] = this

  def forEach(f: A => Unit): Unit =()

  def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)
}
// all elements of type A which satisfy a property.
// { x in A | property(x) }
class PropertyBasedSet[A](property : A => Boolean) extends MySet[A] {
  def contains(elem: A): Boolean = property(elem)
  // { x in A | property(x) } + element = { x in A | property(x) || x == element }
  def +(elem: A): MySet[A] = new PropertyBasedSet[A](x => property(x) || x == elem)

  def ++(anotherSet: MySet[A]): MySet[A] = new PropertyBasedSet[A](x => property(x) || anotherSet(x))

  def -(elem: A): MySet[A] = filter(x => x != elem)

  def &(anotherSet: MySet[A]): MySet[A]  = filter(anotherSet)

  def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  def map[B](f: A => B): MySet[B] = politelyFail

  def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail

  def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x)) // both condition holds true

  def forEach(f: A => Unit): Unit = politelyFail

  def unary_! : MySet[A]  = new PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new IllegalArgumentException("Really deep rabit hole!")

}
class NonEmtySet[A]( head : A ,tail : MySet[A]) extends MySet[A] {
  def contains(elem: A): Boolean = elem == this.head || tail.contains(elem)

  def +(elem: A): MySet[A] = { // prepend the elem to the list
    if(this contains elem) this
    else
      new NonEmtySet[A](elem,this)
  }

  def ++(anotherSet: MySet[A]): MySet[A] = {
    tail ++ anotherSet + head // tail.++(anotherSet)+head
  }
  /*
    [ 1,2,3] ++ [4,5]
    [ 2,3 ] ++ [4,5] + 1
    [3] ++ [4,5] + 2
    [] ++ [4,5] + 3 // [] ++ [4,5]  return's anotherList
    [4 , 5] + 3 = [ 4 ,5, 3]
    [4,5,3] + 2 = [ 4, 5,3,2]
    [4,5,3,2] + 1 = [4,5,3,2,1]
   */
  def -(elem : A) : MySet[A] = {
    if(head == elem)  tail
    else tail - elem + head
  }

  def &(anotherSet : MySet[A]) = filter(anotherSet)

  def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))

  def map[B](f: A => B): MySet[B] = tail.map(f)+f(head) // tail map f + f(head)

  def flatMap[B](f: A => MySet[B]): MySet[B] =tail.flatMap(f) ++ f(head) // recursion

  def filter(predicate: A => Boolean): MySet[A]  =
    {
      val filteredTail = tail filter predicate
      if(predicate(head)) filteredTail + head  // filter when called with seq function is check that we have anotherList(head) == anotherList.contain(head) this return true
      else filteredTail
    } // recursion

  def forEach(f: A => Unit): Unit = {
    tail.forEach(f)
    f(head)
  }
}
object MySet {

  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)

    buildSet(values.toList, new EmptySet[A])
  }
}
object MySetPlayground extends App{
  val s = MySet(1,2,3,4)  // implementation of the collection as function as
  //s - 3 forEach println
  //s.forEach(println)
  s + 5 ++ MySet(-1,-2) + 3 flatMap (x => MySet(x,x * 10)) filter (s - 3) forEach println
  val negative = !s // all the naturals numbers not equal to 1,2,3,4
  println(negative(2))
  println(negative(5))
  val negativeEven = negative.filter(x => x % 2 == 0)
  println(negativeEven(5))

  val negativeEven5 = negative + 5
  println(negativeEven5(5))
  println(negativeEven)
  println(s)

}