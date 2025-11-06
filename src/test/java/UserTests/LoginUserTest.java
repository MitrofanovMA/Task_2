package UserTests;

import HttpConfig.BaseHttpClient;
import User.User;
import User.UserLogin;
import User.UserCreate;
import User.UserDelete;
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
    private final UserLogin userLogin = new UserLogin(baseRequestSpec);
    private final UserCreate userCreate = new UserCreate(baseRequestSpec);
    private UserDelete userDelete = new UserDelete(baseRequestSpec);

    private static String userEmail;
    private static String userName;
    private static String userPassword = "password123";
    private String accessToken;

    @Before
    public void setUp(){
        user = new User(
                "testUser_" + System.currentTimeMillis() + "@yandex.ru",
                userPassword, "testUser_" + System.currentTimeMillis()
        );
        Response responseCreateUser = userCreate.create(user);
        responseCreateUser.then().statusCode(200);
        userEmail = responseCreateUser.path("user.email");
        userName = responseCreateUser.path("user.name");
        this.accessToken = responseCreateUser.path("accessToken");

    }

    @After
    @DisplayName("Очистка тестовых данных")
    public void setAfter(){
        if (accessToken != null){
            userDelete.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Тест логин под существующим пользователем")
    public void loginExistedUser() {
        user = new User(userEmail, userPassword);

        Response response = userLogin.login(user);

        response.then().statusCode(200).body("user.name", equalTo(userName));
    }

    @Test
    @DisplayName("Тест логин с неверным логином")
    public void loginUserWithIncorrectLogin() {
        user = new User(userEmail, "test");

        Response response = userLogin.login(user);

        response.then().statusCode(401).body("message", equalTo("email or password are incorrect"));
    }
    @Test
    @DisplayName("Тест логин с неверным паролем")
    public void loginUserWithIncorrectPassword() {
        user = new User("test@test.ru", userPassword);

        Response response = userLogin.login(user);

        response.then().statusCode(401).body("message", equalTo("email or password are incorrect"));
    }



}

