package com.mavs.userservice.controller;

import com.mavs.userservice.UserServiceApplication;
import com.mavs.userservice.model.User;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {UserServiceApplication.class}, webEnvironment = WebEnvironment.DEFINED_PORT)
public class UserControllerTest {

    private static final String API_URL = "http://localhost:8080/api/v1/users";
    private static final Integer USER_ID = 1;
    private static final String USER_BY_ID_URL = API_URL + "/id/" + USER_ID;

    @Test
    public void whenFindAllUsers_thenOK() {
        Response response = RestAssured.get(API_URL);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void whenFindById_thenOK() {
        User randomUser = createRandomUser();
        createUserAsUri(randomUser);

        Response responseUser = RestAssured.get(USER_BY_ID_URL);
        User user = responseUser.getBody().jsonPath().getObject("", User.class);
        assertThat(user).isNotNull();
    }

    @Test
    public void whenFindByName_thenOK() {
        User randomUser = createRandomUser();
        createUserAsUri(randomUser);

        Response responseUser = RestAssured.get(API_URL + "/name/" + randomUser.getName());
        User user = responseUser.getBody().jsonPath().getObject("", User.class);
        assertThat(user).isNotNull();
    }

    @Test
    public void whenFindByNotExistId_thenNotFound() {
        Response responseUser = RestAssured.get(API_URL + "/id/-1");
        assertThat(responseUser.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void whenSaveNewUser_thenOk() {
        User randomUser = createRandomUser();

        Response response = RestAssured.given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(randomUser)
                .post(API_URL);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    public void whenSaveNewUserWithNullName_thenBadRequest() {
        User randomUser = createRandomUser();
        randomUser.setName(null);

        Response response = RestAssured.given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(randomUser)
                .post(API_URL);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenUpdateUser_thenOk() {
        cleanUp();
        User randomUser = createRandomUser();
        createUserAsUri(randomUser);
        User savedUser = RestAssured.get(USER_BY_ID_URL).jsonPath().getObject("", User.class);

        randomUser.setId(USER_ID);
        randomUser.setName(randomAlphabetic(10));

        RestAssured.given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(randomUser)
                .put(API_URL);

        User updatedUser = RestAssured.get(USER_BY_ID_URL).jsonPath().getObject("", User.class);

        assertThat(updatedUser).isNotEqualTo(savedUser);
        assertThat(updatedUser.getId()).isEqualTo(savedUser.getId());
        assertThat(updatedUser.getName()).isNotEqualTo(savedUser.getName());
    }

    @Test
    public void whenDeleteUser_thenOk() {
        User randomUser = createRandomUser();
        createUserAsUri(randomUser);
        User savedUser = RestAssured.get(USER_BY_ID_URL).jsonPath().getObject("", User.class);

        assertThat(savedUser).isNotNull();

        RestAssured.delete(API_URL + "/" + USER_ID);
        Response response = RestAssured.get(USER_BY_ID_URL);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private String createUserAsUri(User user) {
        Response response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(user)
                .post(API_URL);
        return API_URL + "/id/" + response.jsonPath().get("id");
    }

    private User createRandomUser() {
        return User.builder()
                .email(randomAlphabetic(10))
                .name(randomAlphabetic(15))
                .phone(randomAlphabetic(8))
                .build();

    }

    private void cleanUp() {
        Response response = RestAssured.get(USER_BY_ID_URL);
        if (response.getStatusCode() == HttpStatus.OK.value()) {
            RestAssured.delete(API_URL + "/" + USER_ID);
        }
    }
}