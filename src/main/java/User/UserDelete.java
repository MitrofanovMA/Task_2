package User;

import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UserDelete {
    private final RequestSpecification baseRequestSpec;

    public UserDelete(RequestSpecification baseRequestSpec) {
        this.baseRequestSpec = baseRequestSpec;
    }

    public void delete(String accessToken) {
        given()
                .spec(baseRequestSpec)
                .header("authorization", accessToken)
                .when()
                .delete("/auth/user");
    }
}
