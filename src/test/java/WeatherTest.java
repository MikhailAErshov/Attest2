import io.restassured.response.ValidatableResponse;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WeatherTest extends BaseTest {

    @BeforeAll
    public static void setUpRequest() {
        requestSpecificationForTest(requestSpecification());
    }

    @Test
    public void shouldGetCoordinatesWhenOneValidParameterIsCityName() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "Berlin");

        ValidatableResponse response = given()
                .queryParams(parameters)
                .when()
                .get("weather")
                .then().log().all()
                .body("coord.lon", notNullValue())
                .body("coord.lat", notNullValue())
                .body("coord.lon", equalTo(13.4105F))
                .body("coord.lat", equalTo(52.5244F));
    }

    @Test
    public void shouldGetWeatherWhenInvalidParameterIsCityName() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "Qwaqwaqwaqwaqwa");

        ValidatableResponse response = given()
                .queryParams(parameters)
                .when()
                .get("weather")
                .then().log().all()
                .statusCode(404)
                .body("message", containsString("city not found"));
    }

    @Test
    //Примечание: может неправильно понимаю, что при вводе двух и более параметров (валидный и невалидные)
    //поиск выдаст ошибку, но такого не происходит, ему достаточно одного валидного параметра в поле City Name
    //на остальные поля приложение не обращает внимание (пробовал со всеми параметрами, в тесте указан один из вариантов)
    public void shouldGetWeatherWhenOneValidParameterByCityNameAndSomeInvalidParameters() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "Madrid");
        parameters.put("id", "88888888"); //невалидный параметр
        parameters.put("lat", "888888888"); //невалидный параметр
        parameters.put("lon", "888888888"); //невалидный параметр
        parameters.put("zip", "88888888"); //невалидный параметр

        ValidatableResponse response = given()
                .queryParams(parameters)
                .when()
                .get("weather")
                .then().log().all()
                .statusCode(404)
                .body("message", containsString("city not found"));
    }

    @Test
    public void shouldGetCityNameWhenValidParameterIsCoordinate() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("lat", "48.8534");
        parameters.put("lon", "2.3488");

        ValidatableResponse response = given()
                .queryParams(parameters)
                .when()
                .get("weather")
                .then().log().all()
                .statusCode(200)
                .body("$", hasKey("coord"))
                .body("name", containsString("Paris"));
    }

    @Test
    public void shouldGetWeaterWhenInvalidParameterIsCoordinate() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("lat", "960");
        parameters.put("lon", "480");

        ValidatableResponse response = given()
                .queryParams(parameters)
                .when()
                .get("weather")
                .then().log().all()
                .statusCode(400)
                .body("message", containsString("wrong latitude"));
    }

    @Test
    public void shouldGetWeatherWhenValidParameterIsCityId() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", "5128581");

        ValidatableResponse response = given()
                .queryParams(parameters)
                .when()
                .get("weather")
                .then().log().all()
                .statusCode(200)
                .body("name", equalTo("New York"))
                .body("id", equalTo(5128581));
    }

    @Test
    public void shouldGetWeatherWhenInvalidParameterIsCityId() {
        Map<String, String> parameters = new HashMap<>();
        String invalidParameterIsCityId = "hdfjks85758fhs";
        parameters.put("id", invalidParameterIsCityId);

        ValidatableResponse response = given()
                .queryParams(parameters)
                .when()
                .get("weather")
                .then().log().all()
                .statusCode(400)
                .body("message", containsString(invalidParameterIsCityId + " is not a city ID"));
    }

    @Test
    public void shouldGetWeatherWhenValidParameterZipCode() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("zip", "02109");

        ValidatableResponse response = given()
                .queryParams(parameters)
                .when()
                .get("weather")
                .then().log().all()
                .body("$", hasKey("weather"))
                .body("name", equalTo("Boston"));
    }

    @Test
    public void shouldGetWeatherWhenNoParameters() {
        ValidatableResponse response = given()
                .when()
                .get("weather")
                .then().log().all()
                .statusCode(400)
                .body("message", containsString("Nothing to geocode"));
    }

    //Тест падает, потому что несмотря на то, что указаны все параметры для поиска где конкретно указан город, его id,
    //и его координаты - поиск осуществляется только беря параметр City Name, игнорируя остальные и находит
    //первый попавший город который находится в США, а не в Италии.
    @Test
    public void shouldGetWeatherWhenAllParameters() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "Rome");
        parameters.put("id", "3169070");
        parameters.put("lat", "41.8947");
        parameters.put("lon", "12.4839");
        parameters.put("zip", "00199");
        parameters.put("units", "imperial");
        parameters.put("lang", "en");
        parameters.put("mode", "json");

        String response = given()
                .queryParams(parameters)
                .when()
                .get("weather")
                .then().log().all()
                .extract()
                .path("sys.country");

        assertThat(response).isEqualTo("IT");
    }

    @Test
    public void shouldGetWeatherWithTempClass() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "Boston");
        parameters.put("lat", "42.3584");
        parameters.put("lon", "-71.0598");

        WeatherTempmain weatherTempmain = given()
                .queryParams(parameters)
                .when()
                .get("weather")
                .then().log().all()
                .extract().as(WeatherTempmain.class);

        assertThat(weatherTempmain.getCoord().getLat()).isEqualTo(WeatherTestValue.LAT);
        assertThat(weatherTempmain.getCoord().getLon()).isEqualTo(WeatherTestValue.LON);
        assertThat(weatherTempmain.getName()).isEqualTo(WeatherTestValue.NAME);
        assertThat(weatherTempmain.getSys().getCountry()).isEqualTo(WeatherTestValue.COUNTRY);
    }

    @Test
    public void shouldGetWeatherOnRussianLanguageOnlyDescriptionParameterPositiveTest() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "Rome");
        parameters.put("units", "metric");
        parameters.put("lang", "ru");

        String extractDescription = given()
                .queryParams(parameters)
                .when()
                .get("weather")
                .then().log().all()
                .extract()
                .path("weather[0].description");

        String regex = "[а-яёА-ЯЁ]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(extractDescription);

        assertTrue(m.find());
    }

    //Тест не проходит, потому что в запросе указан русский язык, а приходит в ответе только один параметр на русском
    //языке (description) как в тесте выше. Другой параметр приходит Rain - не переведенный. PS: также параметр country
    //приходит US, хотя должен прийти США.
    @Test
    public void shouldGetWeatherOnRussianLanguageWhenSomeParameters() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "Rome");
        parameters.put("units", "metric");
        parameters.put("lang", "ru");

        String extractMain = given()
                .queryParams(parameters)
                .when()
                .get("weather")
                .then().log().all()
                .extract()
                .path("weather[0].main");

        String regex = "[а-яёА-ЯЁ]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(extractMain);

        assertTrue(m.find());
    }

    @Test
    public void shouldGetWeatherInXMLformat() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("zip", "02109");
        parameters.put("mode", "xml");

        String responseFormat = given()
                .queryParams(parameters)
                .when()
                .get("weather")
                .contentType().split(";")[0];

        assertThat(responseFormat).isEqualTo("application/xml");
    }
}
