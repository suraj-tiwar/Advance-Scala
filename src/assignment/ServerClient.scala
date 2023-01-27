package assignment
import java.io.{DataInputStream, DataOutputStream}
import java.net.Socket
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object Storage {
  var activeConnection: Map[String, Socket] = Map()
}
class ServerClient(socket: Socket) {

  def runner(): Unit = Future {
    val din = new DataInputStream(socket.getInputStream)
    val dout = new DataOutputStream(socket.getOutputStream)
    dout.writeUTF("Enter Your UserName")
    val userName = din.synchronized(din.readUTF().trim)
    println("client connect : " + userName)
    val newClient = userName -> socket
    Storage.activeConnection = Storage.activeConnection + newClient // adding it to map
    activeUser(dout)
    Storage.activeConnection filter (x => x._1 != userName) foreach ((value) => broadCast(value._2, s"$userName Enter the lobby"))
    sys.addShutdownHook {
      if (!socket.isClosed) {
        dout.writeUTF("Quit")
        Thread.sleep(10)
      }
    }
    Await.ready(serverReader(socket, din, dout, userName), Duration.Inf)
  }

  def serverReader(socket: Socket, din: DataInputStream, dout: DataOutputStream, userName: String) = Future {
    var flag = true
    while (flag) {
      if (din.available() != 0) {
        val read = din.readUTF()
        read match {
          case "Quit" => {
            flag = false
            leftServer(userName)
            din.close()
            dout.close()
            socket.close()
          } // close socket and end user interaction with server and future to end it's execution.
          case "List of Active Users" => {
            activeUser(dout)
          } // Give's current list of active user
          case _ => {
            val msg = userName + " :" + read
            dout.writeUTF("Enter sender Name or press enter ")
            val sender = din.readUTF()
            println(msg)
            sender match {
              case "" => Storage.activeConnection.filter(x => !x._2.isClosed) foreach ((value) => broadCast(value._2, msg))
              case sendTo => {
                broadCast(Storage.activeConnection(sendTo), msg)
                broadCast(Storage.activeConnection(userName), msg)
              }
            } // pattern matching based on the User input to send message
          } // User want to send msg
        } // pattern matching based on input from the client
      }
    }
  }

  def leftServer(userName: String): Unit = {
    Storage.activeConnection = Storage.activeConnection - userName
    println(userName + " Left the lobby ")
    Storage.activeConnection.foreach((value => broadCast(value._2, userName + " Left the lobby ")))
  }

  def broadCast(socket: Socket, msg: String): Unit = {
    val dout = new DataOutputStream(socket.getOutputStream)
    dout.writeUTF(msg)
  }

  def activeUser(dout: DataOutputStream) = {
    dout.writeUTF("List of Active Users")
    dout.writeUTF(if (Storage.activeConnection.isEmpty) "No User Active" else Storage.activeConnection.map(x => x._1).reduce((acc, curr) => acc + "," + curr))
  }
}