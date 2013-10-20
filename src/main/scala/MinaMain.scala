package se.ramn.gopher.server

import java.net.InetSocketAddress
import java.nio.charset.Charset
import java.nio.file.Paths

import org.apache.mina.core.session.IoSession
import org.apache.mina.transport.socket.nio.NioSocketAcceptor
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.codec.ProtocolEncoder
import org.apache.mina.filter.codec.ProtocolEncoderOutput
import org.apache.mina.filter.codec.textline.TextLineCodecFactory
import org.apache.mina.filter.codec.textline.TextLineDecoder
import org.apache.mina.filter.logging.LoggingFilter
//import org.apache.mina.core.session.IdleStatus


object GopherServer extends App with Loggable {
  if (args.isEmpty) {
    logger.error("You must supply document root as first argument")
    sys.exit()
  }
  val documentRoot = Paths.get(args(0))

  val port: Int = sys.env.get("PORT").map(_.toInt).getOrElse(7070)
  val acceptor = new NioSocketAcceptor

  acceptor.getFilterChain.addLast("logger", new LoggingFilter())
  acceptor.getFilterChain.addLast("codec",
    new ProtocolCodecFilter(
      new ProtocolEncoder {
        def dispose(session: IoSession) {
        }
        def encode(session: IoSession, message: Any, output: ProtocolEncoderOutput) {
          import org.apache.mina.core.buffer.IoBuffer
          val buffer = IoBuffer.allocate(128, false)
          buffer.setAutoExpand(true)
          message match {
            case s: String =>
              buffer.put(s.getBytes)
            case a: Array[Byte] =>
              buffer.put(a)
          }
          buffer.flip()
          output.write(buffer)
        }
      },
      new TextLineDecoder(Charset.forName("UTF-8"))))

    //new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))))

  acceptor.setHandler(new GopherHandler(documentRoot, "localhost", port))
  acceptor.bind(new InetSocketAddress(port))
  logger.info("Server listening for connections")

  sys.ShutdownHookThread {
    acceptor.unbind()
  }
}
