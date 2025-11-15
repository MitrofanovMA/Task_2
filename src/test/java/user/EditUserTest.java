package user;

import httpconfig.BaseHttpClient;

import io.qameta.allure.Allure;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.equalTo;

public class EditUserTest {

    private final RequestSpecification baseRequestSpec = BaseHttpClient.getBaseRequestSpec();

    private User user;
    private UserMethods userMethods = new UserMethods(baseRequestSpec);

    private String accessToken;

    @Before
    public void setBefore() {
        Allure.step("Подготовка исходного пользователя для теста", () -> {
            user = new User(
                    "testUser" + System.currentTimeMillis() + "@yandex.ru",
                    "password123",
                    "testUser" + System.currentTimeMillis()
            );
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
    @DisplayName("Тест изменения данных пользователя с авторизацией")
    public void userEditWithAuth() {

        Allure.step("Создание пользователя перед изменением данных", () -> {
            Response responseCreateUser = userMethods.create(user);
            responseCreateUser.then().statusCode(200);
            accessToken = responseCreateUser.path("accessToken");
        });

        Allure.step("Формирование новых данных пользователя", () -> {
            user = new User(
                    "testEditUser" + System.currentTimeMillis() + "@yandex.ru",
                    "editPassword123",
                    "testEditUser" + System.currentTimeMillis()
            );
        });

        Allure.step("Изменение данных пользователя с валидным токеном", () -> {
            Response responseEditUser = userMethods.edit(user, accessToken);
            responseEditUser.then().statusCode(200)
                    .body("user.email", startsWith("testedituser"))
                    .body("user.name", startsWith("testEditUser"));
        });

        Allure.step("Получение данных пользователя и проверка изменений", () -> {
            Response responseGetUser = userMethods.get(accessToken);
            responseGetUser.then().statusCode(200)
                    .body("user.email", startsWith("testedituser"))
                    .body("user.name", startsWith("testEditUser"));
        });
    }

    @Test
    @DisplayName("Тест изменения данных пользователя без авторизации")
    public void userEditWithoutAuth() {

        Allure.step("Создание пользователя перед попыткой изменения без авторизации", () -> {
            Response responseCreateUser = userMethods.create(user);
            responseCreateUser.then().statusCode(200);
            accessToken = responseCreateUser.path("accessToken");
        });

        Allure.step("Формирование новых данных пользователя", () -> {
            user = new User(
                    "testEditUser" + System.currentTimeMillis() + "@yandex.ru",
                    "editPassword123",
                    "testEditUser" + System.currentTimeMillis()
            );
        });

        Allure.step("Попытка изменения данных пользователя с неверным токеном", () -> {
            String invalidAccessToken = "invalidAccessToken";
            Response responseEditUser = userMethods.edit(user, invalidAccessToken);

            responseEditUser.then().statusCode(401)
                    .body("message", equalTo("You should be authorised"));
        });
    }
}
