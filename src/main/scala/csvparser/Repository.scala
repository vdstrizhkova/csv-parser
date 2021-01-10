package csvparser

import com.google.common.collect.HashBasedTable
import org.slf4j.LoggerFactory
import scala.jdk.CollectionConverters._

class CountryRepository(readCountries: ReadCountries) {

  def load(language: String): Unit = {
    val countries = readCountries(language)
    InMemoryConfiguration.load(Language(language), countries)
  }

  def getAll(language: String): Seq[Country] =
    InMemoryConfiguration.getAll(Language(language))

  def getById(language: String, countryId: Int): Option[Country] =
    InMemoryConfiguration.getById(Language(language), countryId)

  def getByAlpha2(language: String, alpha2: String): Option[Country] =
    InMemoryConfiguration.getByAlpha2(Language(language), Alpha2(alpha2.toLowerCase()))

  def getByAlpha3(language: String, alpha3: String): Option[Country] =
    InMemoryConfiguration.getByAlpha3(Language(language), Alpha3(alpha3.toLowerCase()))

}

private object InMemoryConfiguration {
  private val logger = LoggerFactory.getLogger(getClass)

  private val countriesPerIdAndLanguage: HashBasedTable[Int, Language, Country]        = HashBasedTable.create()
  private val countriesPerAlpha2AndLanguage: HashBasedTable[Alpha2, Language, Country] = HashBasedTable.create()
  private val countriesPerAlpha3AndLanguage: HashBasedTable[Alpha3, Language, Country] = HashBasedTable.create()

  def load(language: Language, countries: Seq[Country]): Unit = {
    if (
      countriesPerIdAndLanguage.containsColumn(language) || countriesPerAlpha2AndLanguage.containsColumn(
        language
      ) || countriesPerAlpha3AndLanguage.containsColumn(language)
    )
      logger.error("Reloading the configuration after it has been previously initialized")

    if (countries.isEmpty) logger.error("Trying to load an empty configuration into memory")

    countries.foreach { c =>
      countriesPerIdAndLanguage.put(c.countryId, language, c)
      countriesPerAlpha2AndLanguage.put(Alpha2(c.alpha2), language, c)
      countriesPerAlpha3AndLanguage.put(Alpha3(c.alpha3), language, c)
    }
  }

  def getAll(language: Language): Seq[Country] = {
    countriesPerIdAndLanguage.column(language).asScala.values.toSeq
  }

  def getById(language: Language, id: Int): Option[Country] = {
    Option(countriesPerIdAndLanguage.row(id))
      .map(_.asScala.toMap)
      .filter(_.nonEmpty)
      .flatMap(_.get(language))
  }

  def getByAlpha2(language: Language, alpha2: Alpha2): Option[Country] = {
    Option(countriesPerAlpha2AndLanguage.row(alpha2))
      .map(_.asScala.toMap)
      .filter(_.nonEmpty)
      .flatMap(_.get(language))
  }

  def getByAlpha3(language: Language, alpha3: Alpha3): Option[Country] = {
    Option(countriesPerAlpha3AndLanguage.row(alpha3))
      .map(_.asScala.toMap)
      .filter(_.nonEmpty)
      .flatMap(_.get(language))
  }

}
