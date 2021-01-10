package csvparser

import org.slf4j.Logger
import org.slf4j.LoggerFactory

trait Logging {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

}
