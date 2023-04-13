import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    protected static final String apiKey = "9deecb35696df72b83e7408e39993fa3";
    protected static final String baseUri = "https://api.openweathermap.org/data/2.5";


    public static RequestSpecification requestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .addQueryParam("appid", apiKey)
                .build();
    }
    public static void requestSpecificationForTest(RequestSpecification requestSpecification){
        RestAssured.requestSpecification = requestSpecification;
    }
}
