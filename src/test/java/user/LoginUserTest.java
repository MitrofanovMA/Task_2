package user;

import httpconfig.BaseHttpClient;

import io.qameta.allure.Allure;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest {

    private final RequestSpecification baseRequestSpec = BaseHttpClient.getBaseRequestSpec();

    private User user;
    private final UserMethods userMethods = new UserMethods(baseRequestSpec);

    private static String userEmail;
    private static String userName;
    private static String userPassword = "password123";
    private String accessToken;

    @Before
    public void setUp() {
        Allure.step("Создание тестового пользователя для логина", () -> {
            user = new User(
                    "testUser_" + System.currentTimeMillis() + "@yandex.ru",
                    userPassword,
                    "testUser_" + System.currentTimeMillis()
            );

            Response responseCreateUser = userMethods.create(user);
            responseCreateUser.then().statusCode(200);

            userEmail = responseCreateUser.path("user.email");
            userName = responseCreateUser.path("user.name");
            accessToken = responseCreateUser.path("accessToken");
        });
    }

    @After
    @DisplayName("Очистка тестовых данных")
    public void setAfter() {
        Allure.step("Удаление тестового пользователя (если был создан)", () -> {
            if (accessToken != null) {
                userMethods.delete(accessToken);
            }
        });
    }

    @Test
    @DisplayName("Тест логин под существующим пользователем")
    public void loginExistedUser() {

        Allure.step("Формирование данных существующего пользователя для логина", () -> {
            user = new User(userEmail, userPassword);
        });

        Allure.step("Отправка запроса логина и проверка успешного ответа", () -> {
            Response response = userMethods.login(user);
            response.then()
                    .statusCode(200)
                    .body("user.name", equalTo(userName));
        });
    }

    @Test
    @DisplayName("Тест логин с неверным логином")
    public void loginUserWithIncorrectLogin() {

        Allure.step("Формирование данных пользователя с неверным логином/паролем", () -> {
            user = new User(userEmail, "test");
        });

        Allure.step("Попытка логина с некорректными данными и проверка ошибки 401", () -> {
            Response response = userMethods.login(user);
            response.then()
                    .statusCode(401)
                    .body("message", equalTo("email or password are incorrect"));
        });
    }

    @Test
    @DisplayName("Тест логин с неверным паролем")
    public void loginUserWithIncorrectPassword() {

        Allure.step("Формирование данных пользователя с неверным email", () -> {
            user = new User("test@test.ru", userPassword);
        });

        Allure.step("Попытка логина с некорректными данными и проверка ошибки 401", () -> {
            Response response = userMethods.login(user);
            response.then()
                    .statusCode(401)
                    .body("message", equalTo("email or password are incorrect"));
        });
    }
}
