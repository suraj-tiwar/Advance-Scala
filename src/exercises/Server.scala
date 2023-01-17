package exercises
import java.io._
import java.net._
import scala.+:
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Server {
  var activeConnection: Map[String, Socket] = Map()
  def main(args: Array[String]): Unit = {
    try{
      val ServerSocket: ServerSocket = new ServerSocket(6666)
      println("Server Started")
      while (true) {
        val request = ServerSocket.accept()
        ServerClient(request).start()
      }

    }catch {
      case e => e.printStackTrace()
    }
  }

  case class ServerClient(Socket: Socket) extends Thread {

    override def run(): Unit = {
      try {
        val din = new DataInputStream(Socket.getInputStream)
        val dout = new DataOutputStream(Socket.getOutputStream)
        dout.writeUTF("Enter Your UserName")
        val userName = din.synchronized(din.readUTF().trim)
        println("client connect : " + userName)
        val newClient = userName -> Socket
        activeConnection = activeConnection + newClient

        def activeUser() = {
          dout.writeUTF("List of Active Users")
          dout.writeUTF(if (activeConnection.isEmpty) "No User Active" else activeConnection.map(x => x._1).reduce((acc, curr) => acc + "," + curr))
        }

        println(userName + " Enter the lobby")
        activeConnection.filter(_._1 != userName).filter(!_._2.isClosed).foreach(value => broadCast(value._2, userName + " Enter the lobby"))
        activeUser()
        var flag = true
        val readerThread = new Thread(() => {
          while (flag) {
            if (din.available() != 0) {
              val read = din.readUTF()
              read match {
                case "Quit" => {
                  leftServer(userName)
                  flag = false
                }
                case "List of Active Users" => {
                  activeUser()
                }
                case _ => {
                  val msg = userName + " :" + read
                  dout.writeUTF("Enter sender Name or press enter ")
                  val sender = din.readUTF()
                  println(msg)
                  sender match {
                    case "" => activeConnection.filter(x => !x._2.isClosed)foreach((value) => broadCast(value._2, msg))
                    case sendTo => {
                      broadCast(activeConnection(sendTo), msg)
                      broadCast(activeConnection(userName), msg)
                    }
                  }
                }
              }
            }
          }
        }) // Reads MSG from the chat Client
        readerThread.start()
        Runtime.getRuntime.addShutdownHook(new Thread(() => {
          if(!Socket.isClosed) {
            dout.writeUTF("Server crashed")
            dout.writeUTF("Quit")
            leftServer(userName)
            println("Exiting the program")
            flag = false
            Socket.close()
            dout.close()
            din.close()
          }
        }))
      } catch {
        case e => println("Somebody crush with error")
      }

    }
  }
  def broadCast(socket : Socket,msg : String): Unit = {
    val dout = new DataOutputStream(socket.getOutputStream)
    dout.writeUTF(msg)
  }
  def leftServer(userName : String): Unit = {
    activeConnection = activeConnection - userName
    println(userName + " Left the lobby ")
    activeConnection.foreach((value => broadCast(value._2, userName + " Left the lobby ")))
  }

}




