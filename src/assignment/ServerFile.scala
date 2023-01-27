//package assignment

import assignment.ServerClient

import java.io._
import java.net._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.sys.ShutdownHookThread

object ServerFile {
  def main(args: Array[String]): Unit = {
    try{
      val ServerSocket: ServerSocket = new ServerSocket(6666)   //creating server socket.
      println("Server Started")
      while (true) {
        val request = ServerSocket.accept() // serverSocket is listening
        val client = new ServerClient(request)
        client.runner()
      }
    }catch {
      case e => println(e)
    }
  }

}








