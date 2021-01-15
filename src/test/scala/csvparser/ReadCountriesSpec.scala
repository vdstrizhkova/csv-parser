package csvparser

import org.mockito.MockitoSugar
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ReadCountriesSpec extends AnyFlatSpec with Matchers with MockitoSugar {

  private val readCountries = new ReadCountries

  it should "read countries" in {
    val countries = readCountries.apply("test")
    countries shouldBe Seq(
      Country(4, "Afghanistan", "af", "afg"),
      Country(710, "Afrique du Sud", "za", "zaf"),
      Country(248, "Îles Åland", "ax", "ala")
    )
  }
}
