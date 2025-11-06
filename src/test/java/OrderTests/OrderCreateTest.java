package OrderTests;

import HttpConfig.BaseHttpClient;
import Order.Order;
import Order.OrderCreate;
import Order.IngredientsGet;
import User.User;
import User.UserCreate;
import User.UserDelete;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderCreateTest {
    private final RequestSpecification baseRequestSpec = BaseHttpClient.getBaseRequestSpec();

    private Order order;
    private User user;
    private OrderCreate orderCreate = new OrderCreate(baseRequestSpec);
    private IngredientsGet ingredientsGet = new IngredientsGet(baseRequestSpec);
    private UserCreate userCreate = new UserCreate(baseRequestSpec);
    private UserDelete userDelete = new UserDelete(baseRequestSpec);

    private String accessToken;
    private String[] Ingredients;

    @Before
    public void setUp() {
        user = new User("testUser" + System.currentTimeMillis() + "@yandex.ru",
                "password123", "testUser" + System.currentTimeMillis());
        Response responseCreateUser = userCreate.create(user);
        responseCreateUser.then().statusCode(200);

        this.accessToken = responseCreateUser.path("accessToken");

        Response responseIngredientsGet = ingredientsGet.get();
        responseIngredientsGet.then().statusCode(200);
        this.Ingredients = responseIngredientsGet
                .jsonPath()                            // парсим JSON
                .getList("data._id", String.class)
                .stream()
                .limit(2)
                .toArray(String[]::new);
    }

    @After
    public void tearDown() {
        if (accessToken != null){
            userDelete.delete(accessToken);
        }
    }


    @Test
    @DisplayName("Тест создания заказа c авторизации")
    public void TestOrderCreateWithAuth() {
        order = new Order(Ingredients);

        Response responseCreateOrder = orderCreate.create(order, accessToken);
        responseCreateOrder.then().statusCode(200)
                .body("order.number", notNullValue());

    }

    @Test
    @DisplayName("Тест создания заказа без авторизации")
    public void TestOrderCreateWithoutAuth() {
        order = new Order(Ingredients);

        String InvalidAccessToken = ("invalidAccessToken");
        Response responseCreateOrder = orderCreate.create(order, InvalidAccessToken);
        responseCreateOrder.then().statusCode(200);

    }


    @Test
    @DisplayName("Тест создания заказа c ингредиентов")
    public void TestOrderCreateWithIngredients() {
        order = new Order(Ingredients);

        Response responseCreateOrder = orderCreate.create(order, accessToken);
        responseCreateOrder.then().statusCode(200)
                .body("order.number", notNullValue());

    }

    @Test
    @DisplayName("Тест создания заказа без ингредиентов")
    public void TestOrderCreateWithoutIngredients() {
        order = new Order(null);

        Response responseCreateOrder = orderCreate.create(order, accessToken);
        responseCreateOrder.then().statusCode(400)
                .body("message", equalTo("Ingredient ids must be provided"));

    }

    @Test
    @DisplayName("Тест создания заказа с неверным хешем ингредиентов")
    public void TestOrderCreateWithInvalidHash() {
        order = new Order(new String[]{"dd"});

        Response responseCreateOrder = orderCreate.create(order, accessToken);
        responseCreateOrder.then().statusCode(500);

    }
}