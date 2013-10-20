package se.ramn.gopher.server


sealed trait FileSystemEntry {
  val viewName: String
  val selector: String
  val listingType: Char

  def toListingFormat(host: String, port: Int): String = {
    val tab = '\t'
    val crlf = "\r\n"
    val fields = List(listingType + viewName, selector, host, port)
    fields.mkString("", tab.toString, crlf)
  }
}

case class FileEntry(viewName: String, selector: String) extends FileSystemEntry {
  val listingType: Char = EntryTypes.File
}

case class DirEntry(viewName: String, selector: String) extends FileSystemEntry {
  val listingType: Char = EntryTypes.Directory
}
