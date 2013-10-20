package se.ramn.gopher.server

/*
 * From the RFC:
 *
 * 0   Item is a file
 * 1   Item is a directory
 * 2   Item is a CSO phone-book server
 * 3   Error
 * 4   Item is a BinHexed Macintosh file.
 * 5   Item is DOS binary archive of some sort.
 *     Client must read until the TCP connection closes.  Beware.
 * 6   Item is a UNIX uuencoded file.
 * 7   Item is an Index-Search server.
 * 8   Item points to a text-based telnet session.
 * 9   Item is a binary file!
 *     Client must read until the TCP connection closes.  Beware.
 * +   Item is a redundant server
 * T   Item points to a text-based tn3270 session.
 * g   Item is a GIF format graphics file.
 * I   Item is some kind of image file.  Client decides how to display.
 */
object EntryTypes {
  val File = '0'
  val Directory = '1'
  val CsoPhoneBook = '2'
  val Error = '3'
  val BinHexMac = '4'
  val DosBinary = '5'
  val UnixUUEncoded = '6'
  val IndexSearchServer = '7'
  val TelnetSession = '8'
  val BinaryFile = '9'
  val RedundantServer = '+'
  val Tn3270 = 'T'
  val GifImage = 'g'
  val Image = 'I'
}
