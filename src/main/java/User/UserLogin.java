package User;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UserLogin {
    private final RequestSpecification baseRequestSpec;

    public UserLogin(RequestSpecification baseRequestSpec) {
        this.baseRequestSpec = baseRequestSpec;
    }

    public Response login(User user) {
        return given()
                .spec(baseRequestSpec)
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .post("/auth/login")
                .then()
                .extract().response();
    }
}
