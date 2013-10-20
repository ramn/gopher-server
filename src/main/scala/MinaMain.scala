package se.ramn.gopher.server

import java.net.InetSocketAddress
import java.nio.charset.Charset

import org.apache.mina.transport.socket.nio.NioSocketAcceptor
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.codec.textline.TextLineCodecFactory
import org.apache.mina.filter.logging.LoggingFilter
//import org.apache.mina.core.session.IdleStatus


object GopherServer extends App {
  val port: Int = sys.env.get("PORT").map(_.toInt).getOrElse(7070)
  val acceptor = new NioSocketAcceptor

  acceptor.getFilterChain.addLast("logger", new LoggingFilter())
  acceptor.getFilterChain.addLast("codec",
    new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))))

  acceptor.setHandler(new GopherHandler)
  acceptor.bind(new InetSocketAddress(port))
}
