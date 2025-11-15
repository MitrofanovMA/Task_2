package user;

import httpconfig.BaseHttpClient;

import io.qameta.allure.Allure;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CreateUserTest {

    private final RequestSpecification baseRequestSpec = BaseHttpClient.getBaseRequestSpec();

    private User user;
    private UserMethods userMethods = new UserMethods(baseRequestSpec);
    private String accessToken;

    @Before
    public void setUp() {
        Allure.step("Подготовка тестовых данных пользователя", () -> {
            user = new User(
                    "testUser" + System.currentTimeMillis() + "@yandex.ru",
                    "password123",
                    "testUser" + System.currentTimeMillis()
            );
        });
    }

    @After
    @DisplayName("Очистка тестовых данных")
    public void tearDown() {
        Allure.step("Удаление тестового пользователя (если был создан)", () -> {
            if (accessToken != null) {
                userMethods.delete(accessToken);
            }
        });
    }

    @Test
    @DisplayName("Тест создания уникального пользователя")
    public void userMethodsUniq() {

        Allure.step("Создание уникального пользователя", () -> {
            Response responseCreateUser = userMethods.create(user);
            responseCreateUser.then().statusCode(200);

            // Get access token from new User
            accessToken = responseCreateUser.path("accessToken");
        });
    }

    @Test
    @DisplayName("Тест создания пользователя, который уже зарегистрирован")
    public void userMethodsNotUniq() {

        Allure.step("Создание пользователя впервые", () -> {
            Response responseCreateUser = userMethods.create(user);
            responseCreateUser.then().statusCode(200);

            // Get email from response body
            String email = responseCreateUser.path("user.email");

            // Get access token from new User
            accessToken = responseCreateUser.path("accessToken");

            // Переопределяем пользователя с тем же email, но другим именем
            user = new User(email, "password123", "testUser_" + System.currentTimeMillis());
        });

        Allure.step("Попытка создать пользователя с уже существующим email", () -> {
            Response errorResponse = userMethods.create(user);
            errorResponse.then().statusCode(403);
        });
    }

    @Test
    @DisplayName("Тест создания пользователя без email")
    public void userMethodsWithoutRequairedFieldsEmail() {

        Allure.step("Формирование пользователя без email", () -> {
            user = new User(null, "password123", "testUser_" + System.currentTimeMillis());
        });

        Allure.step("Попытка создания пользователя без email", () -> {
            Response responseCreateUser = userMethods.create(user);
            responseCreateUser.then().statusCode(403);

            // Для негативного сценария токен, скорее всего, не вернётся, но читаем как в оригинальном коде
            accessToken = responseCreateUser.path("accessToken");
        });
    }

    @Test
    @DisplayName("Тест создания пользователя без password")
    public void userMethodsWithoutRequairedFieldsPassword() {

        Allure.step("Формирование пользователя без password", () -> {
            user = new User(
                    "testUser_" + System.currentTimeMillis() + "@yandex.ru",
                    null,
                    "testUser_" + System.currentTimeMillis()
            );
        });

        Allure.step("Попытка создания пользователя без password", () -> {
            Response responseCreateUser = userMethods.create(user);
            responseCreateUser.then().statusCode(403);

            accessToken = responseCreateUser.path("accessToken");
        });
    }

    @Test
    @DisplayName("Тест создания пользователя без name")
    public void userMethodsWithoutRequairedFieldsName() {

        Allure.step("Формирование пользователя без name", () -> {
            user = new User(
                    "testUser_" + System.currentTimeMillis() + "@yandex.ru",
                    "password123",
                    null
            );
        });

        Allure.step("Попытка создания пользователя без name", () -> {
            Response responseCreateUser = userMethods.create(user);
            responseCreateUser.then().statusCode(403);

            accessToken = responseCreateUser.path("accessToken");
        });
    }
}
