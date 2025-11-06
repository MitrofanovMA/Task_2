package UserTests;


import HttpConfig.BaseHttpClient;
import User.User;
import User.UserCreate;
import User.UserDelete;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CreateUserTest {
    private final RequestSpecification baseRequestSpec = BaseHttpClient.getBaseRequestSpec();

    private User user;
    private UserCreate userCreate = new UserCreate(baseRequestSpec);
    private UserDelete userDelete = new UserDelete(baseRequestSpec);
    private String accessToken;

    @Before
    public void setUp(){
        user = new User("testUser" + System.currentTimeMillis() + "@yandex.ru",
                "password123", "testUser" + System.currentTimeMillis());
    }

    @After
    @DisplayName("Очистка тестовых данных")
    public void tearDown(){
        if (accessToken != null){
            userDelete.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Тест создания уникального пользователя")
    public void userCreateUniq() {

        Response responseCreateUser = userCreate.create(user);
        responseCreateUser.then().statusCode(200);
        //Get access token from new User
        this.accessToken = responseCreateUser.path("accessToken");
    }
    @Test
    @DisplayName("Тест создания  пользователя, который уже зарегистрирован")
    public void userCreateNotUniq() {

        Response responseCreateUser = userCreate.create(user);

        String userEmail = responseCreateUser.path("user.name");
        responseCreateUser.then().statusCode(200);

        //Get email from response body
        String email = responseCreateUser.path("user.email");

        //Get access token from new User
        this.accessToken = responseCreateUser.path("accessToken");

        // Create user with exist email
        user = new User(email, "password123", "testUser_" + System.currentTimeMillis());
        Response errorResponse = userCreate.create(user);
        errorResponse.then().statusCode(403);

    }

    @Test
    @DisplayName("Тест создания пользователя без email")
    public void userCreateWithoutRequairedFieldsEmail() {
        user = new User(null,"password123", "testUser_" + System.currentTimeMillis());

        Response responseCreateUser = userCreate.create(user);
        responseCreateUser.then().statusCode(403);
        //Get access token from new User
        this.accessToken = responseCreateUser.path("accessToken");
    }
    @Test
    @DisplayName("Тест создания пользователя без password")
    public void userCreateWithoutRequairedFieldsPassword() {
        user = new User("testUser_" + System.currentTimeMillis() + "@yandex.ru", null, "testUser_" + System.currentTimeMillis());

        Response responseCreateUser = userCreate.create(user);
        responseCreateUser.then().statusCode(403);
        //Get access token from new User
        this.accessToken = responseCreateUser.path("accessToken");
    }
    @Test
    @DisplayName("Тест создания пользователя без name")
    public void userCreateWithoutRequairedFieldsName() {
        user = new User("testUser_" + System.currentTimeMillis() + "@yandex.ru","password123", null);

        Response responseCreateUser = userCreate.create(user);
        responseCreateUser.then().statusCode(403);
        //Get access token from new User
        this.accessToken = responseCreateUser.path("accessToken");
    }




}
