//package assignment

import java.io._
import java.net._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.sys.ShutdownHookThread

object Server {
  var activeConnection: Map[String, Socket] = Map()
  var flag = false
  def main(args: Array[String]): Unit = {
    try{
      val ServerSocket: ServerSocket = new ServerSocket(6666)
      println("Server Started")
      while (true) {
        val request = ServerSocket.accept()
         serverClient(request)
      }
    }catch {
      case e => e.printStackTrace()
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

  def serverClient(socket : Socket) = Future
  {
    val din = new DataInputStream(socket.getInputStream)
    val dout = new DataOutputStream(socket.getOutputStream)
    dout.writeUTF("Enter Your UserName")
    val userName = din.synchronized(din.readUTF().trim)
    println("client connect : " + userName)
    val newClient = userName -> socket
    activeConnection = activeConnection + newClient
    activeUser(dout)
    sys.addShutdownHook(
     if(!socket.isClosed) dout.writeUTF("Quit")
    )
    Await.ready(serverReader(socket,din,dout,userName),Duration.Inf)
  }

  def serverReader(socket : Socket,din : DataInputStream,dout : DataOutputStream,userName : String) = Future{
    var flag = true
    while (flag) {
      if (din.available() != 0) {
        val read = din.readUTF()
        read match {
          case "Quit" => {
            leftServer(userName)
            flag = false
            socket.close()
            dout.close()
            din.close()
          }
          case "List of Active Users" => {
            activeUser(dout)
          }
          case _ => {
            val msg = userName + " :" + read
            dout.writeUTF("Enter sender Name or press enter ")
            val sender = din.readUTF()
            println(msg)
            sender match {
              case "" => activeConnection.filter(x => !x._2.isClosed) foreach ((value) => broadCast(value._2, msg))
              case sendTo => {
                broadCast(activeConnection(sendTo), msg)
                broadCast(activeConnection(userName), msg)
              }
            }
          }
        }
      }
    }
  }

  def activeUser(dout : DataOutputStream)  = {
      dout.writeUTF("List of Active Users")
      dout.writeUTF(if (activeConnection.isEmpty) "No User Active" else activeConnection.map(x => x._1).reduce((acc, curr) => acc + "," + curr))
    }

}




