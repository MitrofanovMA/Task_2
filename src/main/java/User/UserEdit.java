package User;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UserEdit {
    private final RequestSpecification baseRequestSpec;

    public UserEdit(RequestSpecification baseRequestSpec) {
        this.baseRequestSpec = baseRequestSpec;
    }

    public Response edit(User user, String accessToken) {
        return given()
                .spec(baseRequestSpec)
                .header("Content-Type", "application/json")
                .header("authorization", accessToken)
                .body(user)
                .when()
                .patch("/auth/user")
                .then()
                .extract().response();
    }
}
