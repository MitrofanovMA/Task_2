package user;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UserMethods {
    private final RequestSpecification baseRequestSpec;

    public UserMethods(RequestSpecification baseRequestSpec) {
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

    public Response get(String accessToken) {
        return given()
                .spec(baseRequestSpec)
                .header("authorization", accessToken)
                .when()
                .get("/auth/user")
                .then()
                .extract().response();
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

    public void delete(String accessToken) {
        given()
                .spec(baseRequestSpec)
                .header("authorization", accessToken)
                .when()
                .delete("/auth/user");
    }
}
