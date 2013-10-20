package se.ramn.gopher.server

import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files
import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.IoSession
import java.nio.charset.MalformedInputException


class GopherHandler(documentRoot: Path, host: String, port: Int) extends IoHandlerAdapter with Loggable {
  val CrLf = "\r\n"
  val messageTermination = '.' + CrLf
  def documentRootContent = fetchContent(documentRoot)
  val NotFound = "404 - Not Found"

  override def messageReceived(session: IoSession, message: Any) {
    message match {
      case "" =>
        session.write(formatResponse(documentRootContent))
      case selector: String =>
        session.write(selectorHandler(selector, session))
      case _ =>
        logger.warn(s"Got unknown selector: $message")
    }
    session.close(false)
  }

  protected def selectorHandler(selector: String, session: IoSession) = {
    logger.debug(s"Got selector: $selector")
    val selectedPath = Paths.get(selector)
    if (isWithinDocRoot(selectedPath))
      fetchContentForSelectedPath(selectedPath, session)
    else
      NotFound
  }

  protected def fetchContentForSelectedPath(selectedPath: Path, session: IoSession) = {
    val absolutePath = documentRoot.resolve(selectedPath).normalize
    // Check the path again after resolving and normalizing
    if (!isWithinDocRoot(absolutePath)) {
      NotFound
    } else {
      if (Files.isDirectory(absolutePath)) {
        formatResponse(fetchContent(absolutePath))
      } else {
        serveFile(absolutePath)
      }
    }
  }

  protected def serveFile(absolutePath: Path) =
    try {
      val source = io.Source.fromFile(absolutePath.toFile, Config.charset.name)
      val fileContent = source.mkString
      fileContent
    } catch {
      case e: MalformedInputException =>
        logger.warn(s"MalformedInputException for selector ${absolutePath.toString}, sending as bytes instead")
        val inputStream = Files.newInputStream(absolutePath)
        var data = Seq.empty[Byte]
        while (inputStream.available > 0) {
          data = data :+ inputStream.read.asInstanceOf[Byte]
        }
        data.toArray
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
