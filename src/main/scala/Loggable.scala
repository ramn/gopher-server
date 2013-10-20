package se.ramn.gopher.server

import org.slf4j.Logger
import org.slf4j.LoggerFactory


trait Loggable {
  lazy final val logger = LoggerFactory.getLogger(getClass)
}
