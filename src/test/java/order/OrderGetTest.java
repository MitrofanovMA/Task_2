package order;

import httpconfig.BaseHttpClient;
import user.User;
import user.UserMethods;

import io.qameta.allure.Allure;
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
    private OrderMethods orderCreate = new OrderMethods(baseRequestSpec);
    private IngredientsGet ingredientsGet = new IngredientsGet(baseRequestSpec);
    private UserMethods userMethods = new UserMethods(baseRequestSpec);

    private String accessToken;
    private String[] Ingredients;

    @Before
    public void setUp() {

        Allure.step("Создание тестового пользователя", () -> {
            user = new User(
                    "testUser" + System.currentTimeMillis() + "@yandex.ru",
                    "password123",
                    "testUser" + System.currentTimeMillis()
            );

            Response responseCreateUser = userMethods.create(user);
            responseCreateUser.then().statusCode(200);

            accessToken = responseCreateUser.path("accessToken");
        });

        Allure.step("Получение списка ингредиентов", () -> {
            Response responseIngredientsGet = ingredientsGet.get();
            responseIngredientsGet.then().statusCode(200);

            Ingredients = responseIngredientsGet
                    .jsonPath()
                    .getList("data._id", String.class)
                    .stream()
                    .limit(2)
                    .toArray(String[]::new);
        });

        Allure.step("Создание заказа перед тестами", () -> {
            order = new Order(Ingredients);
            Response responseCreateOrder = orderCreate.create(order, accessToken);
            responseCreateOrder.then().statusCode(200);
        });
    }

    @After
    public void tearDown() {
        Allure.step("Удаление тестового пользователя", () -> {
            if (accessToken != null) {
                userMethods.delete(accessToken);
            }
        });
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void TestGetOrderAuthUser() {

        Allure.step("Отправка запроса получения заказов с валидным токеном", () -> {
            Response responseGetOrderByUser = orderCreate.get(accessToken);
            responseGetOrderByUser.then().statusCode(200);
        });
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    public void TestGetOrderNoAuthUser() {

        Allure.step("Отправка запроса получения заказов с неверным токеном", () -> {
            Response responseGetOrderByUser = orderCreate.get("InvalidAccessToken");
            responseGetOrderByUser.then()
                    .statusCode(401)
                    .body("message", equalTo("You should be authorised"));
        });
    }
}
