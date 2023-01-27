//package assignment

import java.io.{DataInputStream, DataOutputStream}
import java.net.Socket
import java.util.Scanner
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, duration}
import scala.util.{Failure, Success, Try}

object ClientFile extends App{
  try {
    val s = new Socket("localhost", 6666) // client connection to server
    val dout = new DataOutputStream(s.getOutputStream)
    val din = new DataInputStream(s.getInputStream)
    val sc = new Scanner(System.in)
    var flag = true
    val writerThread = Future {
      while (flag) {
        val message = sc.nextLine()
        message match {
          case "Quit" => {
            flag = false
          }
          case _ => dout.writeUTF(message)
        } // pattern matching based on the user input.
      }
      true
     }
     // Write console message to the server

    val readerThread = Future {
      while (flag) {
        if (din.available() != 0) {
          val msg = din.readUTF().trim
          msg match {
            case "Quit" => {
              flag = false
              println("Server Stopped")
              System.exit(0)
            }
            case _ => println(msg)
          } // pattern matching based on the server input
        }
      }
      true
    }
    // Reading from the server
    dout.flush() // flushed the outputStream
    sys.addShutdownHook(
      if(!s.isClosed) {
        dout.writeUTF("Quit")
        }
    )
    val res = for {
      r <- readerThread
      w <- writerThread
    } yield  r ||  w // Future[List[Future]]
    Await.result(res,Duration.Inf) // waiting for both Future finish to exit the program.
  } catch {
    case  e => println(e)
  }
}


