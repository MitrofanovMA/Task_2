package User;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UserCreate {
    private final RequestSpecification baseRequestSpec;

    public UserCreate(RequestSpecification baseRequestSpec) {
        this.baseRequestSpec = baseRequestSpec;
    }

    public Response create(User user) {
        return given()
                .spec(baseRequestSpec)
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .post("/auth/register")
                .then()
                .extract().response();
    }
}
