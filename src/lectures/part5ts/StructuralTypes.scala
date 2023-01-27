package lectures.part5ts

object StructuralTypes extends App{

  type JavaCloseable = java.io.Closeable
  class HipsterCloseable {
    def close(): Unit = println("yeah yeah I'm closing ")
  }
}
