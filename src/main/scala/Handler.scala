package se.ramn.gopher.server

import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.IoSession


class GopherHandler extends IoHandlerAdapter with Loggable {
  val CrLf = "\r\n"

  override def messageReceived(session: IoSession, message: Any) {
    message match {
      case "" =>
        session.write("1Some dir	/some_dir	localhost	7070" + CrLf)
        session.write("0My File	/my_file	localhost	7070" + CrLf)
        session.write("1Another dir	/another_dir	localhost	7070" + CrLf)
        session.write(".")
      case "/my_file" =>
        session.write("this is a text file")
      case _ =>
        logger.warn(s"got unknown request: $message")
    }
    session.close(false)
  }

  override def exceptionCaught(session: IoSession, cause: Throwable) {
    cause.printStackTrace
  }
}
