package Order;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class OrderGetByUser {
    private final RequestSpecification baseRequestSpec;

    public OrderGetByUser(RequestSpecification baseRequestSpec) {
        this.baseRequestSpec = baseRequestSpec;
    }

    public Response get(String accessToken) {
        return given()
                .spec(baseRequestSpec)
                .header("authorization", accessToken)
                .when()
                .get("/orders")
                .then()
                .extract().response();
    }
}
