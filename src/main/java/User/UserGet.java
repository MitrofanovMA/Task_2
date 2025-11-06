package User;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UserGet {
    private final RequestSpecification baseRequestSpec;

    public UserGet(RequestSpecification baseRequestSpec) {
        this.baseRequestSpec = baseRequestSpec;
    }

    public Response get(String accessToken) {
        return given()
                .spec(baseRequestSpec)
                .header("authorization", accessToken)
                .when()
                .get("/auth/user")
                .then()
                .extract().response();
    }
}
