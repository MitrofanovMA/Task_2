package Order;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class IngredientsGet {
    private final RequestSpecification baseRequestSpec;

    public IngredientsGet(RequestSpecification baseRequestSpec) {
        this.baseRequestSpec = baseRequestSpec;
    }

    public Response get() {
        return given()
                .spec(baseRequestSpec)
                .get("/ingredients")
                .then()
                .extract().response();
    }

}
