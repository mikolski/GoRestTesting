package user;

import base.TestBase;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;

public class CreateNewUserTests extends TestBase {

    private String userId;
    private final File emptyUserBody = new File("src/main/resources/jsons/emptyUserBody.json");
    private final File invalidEmailNewUserBody = new File("src/main/resources/jsons/invalidEmailNewUserBody.json");
    private final File validNewUserBody = new File("src/main/resources/jsons/validNewUserBody.json");

    @Test(priority = 1)
    public void shouldNotCreateNewUserWithoutRequiredFields() {
        Response response =

                given()
                        .spec(requestSpecification)
                        .body(emptyUserBody)
                        .when()
                        .post(BASE_URL + users)
                        .then().statusCode(422).
                        extract().response();

        JsonPath jsonPath = response.jsonPath();

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(jsonPath.get("[0].field").toString()).isEqualTo("email");
        softAssertions.assertThat(jsonPath.get("[0].message").toString()).isEqualTo("can't be blank");

        softAssertions.assertThat(jsonPath.get("[1].field").toString()).isEqualTo("name");
        softAssertions.assertThat(jsonPath.get("[1].message").toString()).isEqualTo("can't be blank");

        softAssertions.assertThat(jsonPath.get("[2].field").toString()).isEqualTo("gender");
        softAssertions.assertThat(jsonPath.get("[2].message").toString()).isEqualTo("can't be blank, can be male of female");

        softAssertions.assertThat(jsonPath.get("[3].field").toString()).isEqualTo("status");
        softAssertions.assertThat(jsonPath.get("[3].message").toString()).isEqualTo("can't be blank");
        softAssertions.assertAll();


    }

    @Test(priority = 2)
    public void shouldNotCreateNewUserWithInvalidEmail() {
        Response response =

                given()
                        .spec(requestSpecification)
                        .body(invalidEmailNewUserBody)
                        .when()
                        .post(BASE_URL + users)
                        .then().statusCode(422).
                        extract().response();

        JsonPath jsonPath = response.jsonPath();
        Assert.assertEquals(jsonPath.get("[0].field"), "email");
        Assert.assertEquals(jsonPath.get("[0].message"), "is invalid");

    }

    @Test(priority = 3)
    public void shouldNotCreateNewUserWithoutAccessToken() {
        Response response =

                given()
                        //.spec(requestSpecification)
                        .body(validNewUserBody)
                        .contentType(ContentType.JSON)
                        .when()
                        .post(BASE_URL + users)
                        .then().statusCode(401).
                        extract().response();

        JsonPath jsonPath = response.jsonPath();
        Assert.assertEquals(jsonPath.get("message"), "Authentication failed");

    }

    @Test(priority = 4)
    public void shouldCreateNewUser() {
        Response response =

                given()
                        .spec(requestSpecification)
                        .body(validNewUserBody)
                        .when()
                        .post(BASE_URL + users)
                        .then().statusCode(201).
                        extract().response();


        JsonPath jsonPath = response.jsonPath();
        userId = jsonPath.get("id").toString();

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(jsonPath.get("name").toString()).isEqualTo("John Connor");
        softAssertions.assertThat(jsonPath.get("email").toString()).isEqualTo("john_connor@email.com");
        softAssertions.assertThat(jsonPath.get("gender").toString()).isEqualTo("male");
        softAssertions.assertThat(jsonPath.get("status").toString()).isEqualTo("active");
        softAssertions.assertAll();

    }

    @Test(priority = 5)
    public void shouldGetUser() {
        Response response =

                given()
                        .spec(requestSpecification)
                        .pathParam("id", userId)
                        .when()
                        .get(BASE_URL + users + "/{id}")
                        .then().statusCode(200).
                        extract().response();

        JsonPath jsonPath = response.jsonPath();
        Assert.assertEquals(jsonPath.get("id").toString(), userId);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(jsonPath.get("name").toString()).isEqualTo("John Connor");
        softAssertions.assertThat(jsonPath.get("email").toString()).isEqualTo("john_connor@email.com");
        softAssertions.assertThat(jsonPath.get("gender").toString()).isEqualTo("male");
        softAssertions.assertThat(jsonPath.get("status").toString()).isEqualTo("active");
        softAssertions.assertAll();

    }

    @AfterClass
    public void shouldDeleteUser() {
        given()
                .spec(requestSpecification)
                .pathParam("id", userId)
                .when()
                .delete(BASE_URL + users + "/{id}")
                .then().statusCode(204);
    }
}
