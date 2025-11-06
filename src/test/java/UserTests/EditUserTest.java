package UserTests;

import HttpConfig.BaseHttpClient;
import User.User;
import User.UserCreate;
import User.UserEdit;
import User.UserGet;
import User.UserDelete;
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
    private UserCreate userCreate = new UserCreate(baseRequestSpec);
    private UserEdit userEdit = new UserEdit(baseRequestSpec);
    private UserGet userGet = new UserGet(baseRequestSpec);
    private UserDelete userDelete = new UserDelete(baseRequestSpec);

    private String accessToken;

    @Before
    public void setBefore(){
        user = new User("testUser" + System.currentTimeMillis() + "@yandex.ru",
                "password123", "testUser" + System.currentTimeMillis());
    }

    @After
    @DisplayName("Очистка тестовых данных")
    public void setAfter(){
      if (accessToken != null){
          userDelete.delete(accessToken);
      }
    }

    @Test
    @DisplayName("Тест изменнение данных пользователя с авторизацией")
    public void userEditWithAuth() {

        Response responseCreateUser = userCreate.create(user);
        responseCreateUser.then().statusCode(200);
        //Get access token from new User
        this.accessToken = responseCreateUser.path("accessToken");


        user = new User("testEditUser" + System.currentTimeMillis() + "@yandex.ru",
                "editPassword123", "testEditUser" + System.currentTimeMillis());
        Response responseEditUser = userEdit.edit(user, accessToken);
        responseEditUser.then().statusCode(200)
                .body("user.email", startsWith("testedituser"))
                .body("user.name", startsWith("testEditUser"));

        Response responseGetUser = userGet.get(accessToken);
        responseGetUser.then().statusCode(200)
                .body("user.email", startsWith("testedituser"))
                .body("user.name", startsWith("testEditUser"));

    }

    @Test
    @DisplayName("Тест изменнение данных пользователя без авторизацией")
    public void userEditWithoutAuth() {

        Response responseCreateUser = userCreate.create(user);
        responseCreateUser.then().statusCode(200);
        this.accessToken = responseCreateUser.path("accessToken");

        //Invalid access token for new User
        String accessToken = ("invalidAccessToken");

        user = new User("testEditUser" + System.currentTimeMillis() + "@yandex.ru",
                "editPassword123", "testEditUser" + System.currentTimeMillis());
        Response responseEditUser = userEdit.edit(user, accessToken);

        responseEditUser.then().statusCode(401)
                .body("message", equalTo("You should be authorised"));

    }


}
