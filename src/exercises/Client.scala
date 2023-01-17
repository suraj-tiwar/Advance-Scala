package exercises

import java.io.{DataInputStream, DataOutputStream}
import java.net.Socket
import java.util.Scanner

object Client extends App{
  try {
    val s = new Socket("localhost", 6666)
    val dout = new DataOutputStream(s.getOutputStream)
    val din = new DataInputStream(s.getInputStream)
    val sc = new Scanner(System.in)
    var flag = true
    val writerThread = new Thread(() => {
      while (flag) {
        val message = sc.nextLine()
        message match {
          case "Quit" => {
            dout.writeUTF("Quit")
            flag = false
            din.close()
            dout.close()
            s.close()
          }
          case _ => dout.writeUTF(message)
        }
      }
     }) // Write console message to the server

    val readerThread = new Thread ( () =>
    {
      while (flag) {
        if (din.available() != 0) println(din.readUTF)
      }
    }) // Reading from the server
    writerThread.start()
    readerThread.start()
    dout.flush()
    Thread.sleep(5000)
//    dout.close()
//    din.close()
//    s.close()
  } catch {
    case e: Exception =>
      System.out.println(e)
  }
}


