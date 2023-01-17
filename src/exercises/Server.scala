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
      val din = new DataInputStream(Socket.getInputStream)
      val dout = new DataOutputStream(Socket.getOutputStream)
      dout.writeUTF("Enter Your UserName")
      val userName = din.synchronized(din.readUTF())
      println("client connect : " + userName)
      val newClient = userName -> Socket
      activeConnection = activeConnection + newClient
      def activeUser()= {
        dout.writeUTF("List of Active Users")
        dout.writeUTF(if (activeConnection.isEmpty) "No User Active" else activeConnection.map(x => x._1).reduce((acc, curr) => acc + "," + curr))
      }
      println(userName+ " Enter the lobby")
      activeConnection.filter(_._1 != userName).foreach(value => broadCast(value._2,userName + " Enter the lobby"))
      activeUser()
      var flag = true
      val readerThread = new Thread(() => {
        while (flag) {
          if (din.available() != 0) {
            val read  = din.readUTF()
            read match {
              case "Quit" => {
                activeConnection = activeConnection - userName
                println(userName + " Left the lobby ")
                activeConnection.foreach((value => broadCast(value._2,userName + " Left the lobby ")))
                flag = false
              }
              case "List of Active Users" =>{
                activeUser()
                }
              case _ => {
                val msg = userName+" :"+ read
                dout.writeUTF("Enter sender Name or press enter ")
                val sender = din.readUTF()
                println(msg)
                sender match {
                  case "" => activeConnection.foreach((value) => broadCast(value._2, msg))
                  case sendTo => {
                    broadCast(activeConnection(sendTo), msg)
                    broadCast(activeConnection(userName),msg)
                  }
                }
              }
            }
          }
        }
      })
      readerThread.start()
    }
  }
  def broadCast(socket : Socket,msg : String): Unit = {
    val dout = new DataOutputStream(socket.getOutputStream)
    dout.writeUTF(msg)
  }
}




