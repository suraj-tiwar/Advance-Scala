package exercises
import java.io.{DataInputStream, DataOutputStream}
import java.net.Socket
import java.util.Scanner
object Client3 extends App{
  try {
    val s = new Socket("localhost", 6666)
    val dout = new DataOutputStream(s.getOutputStream)
    val din = new DataInputStream(s.getInputStream)
    val sc = new Scanner(System.in)
    var flag = true
    val writerThread = new Thread(() => { // write to server.
      while (flag) {
        val message = sc.nextLine()
        message match {
          case "Quit" => {
           dout.writeUTF("Quit")
            flag = false
          }
          case _ => dout.writeUTF(message)
        }
      }
    })
    val readerThread = new Thread(() => {
      while (flag) {
        val msg = din.readUTF()
        msg match {
          case "Stop" => flag = false
          case _ => println(msg)
        }
      }
    }) // Reading from the server
    writerThread.start()
    readerThread.start()
    dout.flush()
    Thread.sleep(5000)
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      flag = false
      dout.writeUTF("Quit")
      din.close()
      dout.close()
      s.close()
    }))
  } catch {
    case e: Exception => e.printStackTrace
  }
}
