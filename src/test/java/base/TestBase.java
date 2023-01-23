package base;

import helpers.Configuration;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeMethod;

public class TestBase {
    public static final String BASE_URL = Configuration.getBaseUrl();

    public String users = "/public/v2/users";

    public RequestSpecification requestSpecification;

    @BeforeMethod
    public void testSetup() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.addQueryParam("access-token", Configuration.getToken());
        requestSpecBuilder.setContentType(ContentType.JSON);
        requestSpecification = requestSpecBuilder.build();
    }
}
