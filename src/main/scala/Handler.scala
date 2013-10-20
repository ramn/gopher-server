package se.ramn.gopher.server

import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files
import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.IoSession


class GopherHandler(documentRoot: Path, host: String, port: Int) extends IoHandlerAdapter with Loggable {
  val CrLf = "\r\n"
  val messageTermination = '.' + CrLf
  val documentRootContent = fetchContent(documentRoot)

  override def messageReceived(session: IoSession, message: Any) {
    message match {
      case "" =>
        session.write(formatResponse(documentRootContent))
      case selector: String =>
        logger.debug(s"Got selector: $selector")

        val selectedPath = Paths.get(selector)
        if (isWithinDocRoot(selectedPath)) {
          val absolutePath = documentRoot.resolve(selectedPath).normalize
          if (!isWithinDocRoot(absolutePath)) {
            session.write("404")
          } else {
            logger.debug(absolutePath.toString)
            if (Files.isDirectory(absolutePath)) {
              session.write(formatResponse(fetchContent(absolutePath)))
            } else {
              import java.nio.charset.MalformedInputException
              try {
                val fileContent = io.Source.fromFile(absolutePath.toFile, "UTF8").mkString
                session.write(fileContent)
              } catch {
                case e: MalformedInputException =>
                  logger.warn(s"MalformedInputException for selector $selector, sending as bytes instead")
                  val inputStream = Files.newInputStream(absolutePath)
                  var data = Seq.empty[Byte]
                  while (inputStream.available > 0) {
                    data = data :+ inputStream.read.asInstanceOf[Byte]
                  }
                  session.write(data.toArray)
              }
            }
          }
        } else {
          // Outside of Document Root, can't process
          session.write("404")
        }
      case _ =>
        logger.warn(s"Got unknown selector: $message")
    }
    session.close(false)
  }

  override def exceptionCaught(session: IoSession, cause: Throwable) {
    cause.printStackTrace
  }

  protected def isWithinDocRoot(path: Path): Boolean =
    path.startsWith(documentRoot) || !path.isAbsolute

  protected def formatResponse(entries: Iterable[FileSystemEntry]): String = {
    val response = entries
          .map(_.toListingFormat(host, port))
          .mkString
    response + messageTermination
  }

  def fetchContent(documentRoot: Path): Iterable[FileSystemEntry] = {
    import collection.JavaConverters._
    Files.newDirectoryStream(documentRoot)
      .asScala
      .map { path =>
        val builder = if (Files.isDirectory(path)) {
          DirEntry
        } else {
          FileEntry
        }
        builder.apply(path.getFileName.toString, path.toString)
      }
  }
}
