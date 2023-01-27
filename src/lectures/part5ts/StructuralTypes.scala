package lectures.part5ts

object StructuralTypes extends App{

  type JavaCloseable = java.io.Closeable //
  class HipsterCloseable {
    def close(): Unit = println("yeah yeah I'm closing ")

    def closeSilently(): Unit = println("Hippster closing the gates")
  }

  type UnifiedCloseable = {
    def close(): Unit
  }// STRUCTURAL TYPE
  def closeQuietly(unifiedCloseable: UnifiedCloseable): Unit = unifiedCloseable.close() //

  closeQuietly(new JavaCloseable {
    override def close(): Unit = ???
  })
  closeQuietly(new HipsterCloseable)

  // Type REFINEMENTS
  type AdvanceCloseable = JavaCloseable {
    def closeSilently(): Unit // adding new method to java.io.closeable package
  }

  class AdvancedJavaCloseable extends JavaCloseable {
    override def close() : Unit = println("Java closes")
    def closeSilently(): Unit = println("Java closes silently")
  }
  def closeShh(advCloseable : AdvanceCloseable):Unit = advCloseable.closeSilently()

  closeShh(new AdvancedJavaCloseable)
  def altClose(closeable : { def close() : Unit}): Unit = closeable.close()



}
