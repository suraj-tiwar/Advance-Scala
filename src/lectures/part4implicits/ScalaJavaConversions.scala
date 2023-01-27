package lectures.part4implicits
import java.{util => ju}
object ScalaJavaConversions extends App{

  import collection.JavaConverters._

  val javaset : ju.Set[Int] = new ju.HashSet[Int]()
  (1 to 5).foreach(javaset.add)
  println(javaset)

  val scalaset =  javaset.asScala
  println(scalaset)
  /*
    Iterator
    Iterable
    ju.List - scala .mutable.Buffer
    ju.Set - scala.mutable.Map
    ju.Map - scala.mutable.Map
*/

  import collection.mutable._
  val numberBuffer = ArrayBuffer[Int](1,2,3) // mutable
  val juNumbersBuffer = numberBuffer.asJava // mutable

  println(juNumbersBuffer.asScala eq numberBuffer) // return true equal object same object
  val numbers = List(1,2,3,4,5)
  val juNumbers = numbers.asJava

  val backToScala = juNumbers.asScala
  println(backToScala eq numbers)
  println(backToScala == numbers)

  class ToScala[T](value : T) {
    def asScala : T = value
  }
  implicit def asScalaOptional[T](o : ju.Optional[T]): ToScala[Option[T]] = new ToScala[Option[T]] (
    if (o.isPresent) Some(o.get) else None
  )

  val juOptional: ju.Optional[Int] = ju.Optional.of(2)
  val scalaOption = juOptional.asScala
  println(scalaOption)

}
