package OrderTests;

import HttpConfig.BaseHttpClient;
import Order.OrderGetByUser;
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



public class OrderGetTest {
    private final RequestSpecification baseRequestSpec = BaseHttpClient.getBaseRequestSpec();

    private Order order;
    private User user;
    private OrderCreate orderCreate = new OrderCreate(baseRequestSpec);
    private IngredientsGet ingredientsGet = new IngredientsGet(baseRequestSpec);
    private UserCreate userCreate = new UserCreate(baseRequestSpec);
    private UserDelete userDelete = new UserDelete(baseRequestSpec);
    private OrderGetByUser orderGetByUser = new OrderGetByUser(baseRequestSpec);

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

        order = new Order(Ingredients);
        Response responseCreateOrder = orderCreate.create(order, accessToken);
        responseCreateOrder.then().statusCode(200);
    }

    @After
    public void tearDown() {
        if (accessToken != null){
            userDelete.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя – авторизованный пользователь")
    public void TestGetOrderAuthUser(){
        Response responseGetOrderByUser = orderGetByUser.get(accessToken);
        responseGetOrderByUser.then().statusCode(200);
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя – неавторизованный пользователь")
    public void TestGetOrderNoAuthUser(){
        Response responseGetOrderByUser = orderGetByUser.get("InvalidAccessToken");
        responseGetOrderByUser.then().statusCode(401)
                 .body("message", equalTo("You should be authorised"));
    }


}
