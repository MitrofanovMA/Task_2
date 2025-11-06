package Order;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class OrderCreate {
    private final RequestSpecification baseRequestSpec;

    public OrderCreate(RequestSpecification baseRequestSpec) {
        this.baseRequestSpec = baseRequestSpec;
    }

    public Response create(Order order, String accessToken) {
        return given()
                .spec(baseRequestSpec)
                .header("authorization", accessToken)
                .header("Content-Type", "application/json")
                .body(order)
                .when()
                .post("/orders")
                .then()
                .extract().response();
    }

}
