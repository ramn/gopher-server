package se.ramn.gopher.server

import java.nio.charset.Charset
import org.apache.mina.core.session.IoSession
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.filter.codec.ProtocolEncoder
import org.apache.mina.filter.codec.ProtocolEncoderOutput


class ByteEncoder extends ProtocolEncoder {
  val stringEncoder = Config.charset.newEncoder

  def dispose(session: IoSession) {
  }

  def encode(session: IoSession, message: Any, output: ProtocolEncoderOutput) {
    val buffer = IoBuffer.allocate(1024, false)
    buffer.setAutoExpand(true)
    message match {
      case s: String => buffer.putString(s, stringEncoder)
      case a: Array[Byte] => buffer.put(a)
    }
    buffer.flip()
    output.write(buffer)
  }
}
