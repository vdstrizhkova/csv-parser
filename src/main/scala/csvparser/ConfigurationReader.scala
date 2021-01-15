package csvparser

import java.io.FileNotFoundException
import java.net.URL

import kantan.csv.ParseError.IOError
import kantan.csv._
import kantan.csv.generic.GenericInstances
import kantan.csv.ops._
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import scala.io.Codec

case class Country(countryId: Int, localizedName: String, alpha2: String, alpha3: String)

class ReadCountries {
  implicit val codec: Codec                                   = Codec.UTF8
  implicit val countriesHeaderDecoder: HeaderDecoder[Country] =
    HeaderDecoder.decoder("id", "name", "alpha2", "alpha3")(Country.apply)

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  def apply(language: String): Seq[Country] =
    withResource(language) { case (f, resource) =>
      resource
        .asCsvReader[Country](rfc.withHeader)
        .toSeq
        .zipWithIndex
        .flatMap { case (result, lineNumber) =>
          parse(f, lineNumber, result) { line => Country(line.countryId, line.localizedName, line.alpha2, line.alpha3) }
        }
    }

  private def withResource[A](language: String)(f: (String, URL) => A): A = {
    val file = s"countries/countries_$language.csv"
    Option(getClass.getClassLoader.getResource(file)) match {
      case Some(url) => f(file, url)
      case None      => throw new FileNotFoundException(s"Could not found file $file")
    }
  }

  private def parse[A, B](file: String, line: Int, result: ReadResult[A])(f: A => B): Option[B] = {
    result match {
      case Left(_: IOError) =>
        logger.error(s"Error opening $file")
        None
      case Left(error)      =>
        logger.error(s"Error reading $file at line $line: ${error.getMessage}")
        None
      case Right(valid)     =>
        Some(f(valid))
    }
  }

}
