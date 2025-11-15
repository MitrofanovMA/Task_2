package order;

import httpconfig.BaseHttpClient;
import io.qameta.allure.Allure;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserMethods;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderCreateTest {

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

        Allure.step("Создание нового пользователя", () -> {
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

            List<String> ids = responseIngredientsGet
                    .jsonPath()
                    .getList("data._id", String.class);

            Ingredients = ids.stream()
                    .limit(2)
                    .toArray(String[]::new);
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
    @DisplayName("Тест создания заказа c авторизацией")
    public void TestOrderCreateWithAuth() {

        Allure.step("Формирование заказа", () -> {
            order = new Order(Ingredients);
        });

        Allure.step("Создание заказа с валидным токеном", () -> {
            Response responseCreateOrder = orderCreate.create(order, accessToken);
            responseCreateOrder.then().statusCode(200)
                    .body("order.number", notNullValue());
        });
    }

    @Test
    @DisplayName("Тест создания заказа без авторизации")
    public void TestOrderCreateWithoutAuth() {

        Allure.step("Формирование заказа", () -> {
            order = new Order(Ingredients);
        });

        Allure.step("Создание заказа с невалидным токеном", () -> {
            Response responseCreateOrder = orderCreate.create(order, "invalidAccessToken");
            responseCreateOrder.then().statusCode(200);
        });
    }

    @Test
    @DisplayName("Тест создания заказа с ингредиентами")
    public void TestOrderCreateWithIngredients() {

        Allure.step("Формирование заказа", () -> {
            order = new Order(Ingredients);
        });

        Allure.step("Создание заказа", () -> {
            Response responseCreateOrder = orderCreate.create(order, accessToken);
            responseCreateOrder.then().statusCode(200)
                    .body("order.number", notNullValue());
        });
    }

    @Test
    @DisplayName("Тест создания заказа без ингредиентов")
    public void TestOrderCreateWithoutIngredients() {

        Allure.step("Формирование заказа без ингредиентов", () -> {
            order = new Order(null);
        });

        Allure.step("Создание заказа — ожидаем ошибку 400", () -> {
            Response responseCreateOrder = orderCreate.create(order, accessToken);
            responseCreateOrder.then().statusCode(400)
                    .body("message", equalTo("Ingredient ids must be provided"));
        });
    }

    @Test
    @DisplayName("Тест создания заказа с неверным хешем ингредиентов")
    public void TestOrderCreateWithInvalidHash() {

        Allure.step("Формирование заказа с неправильным хешем", () -> {
            order = new Order(new String[]{"dd"});
        });

        Allure.step("Создание заказа — ожидаем ошибку 500", () -> {
            Response responseCreateOrder = orderCreate.create(order, accessToken);
            responseCreateOrder.then().statusCode(500);
        });
    }
}
