package com.mavs.userservice.controller;

import com.mavs.common.dto.UserDto;
import com.mavs.userservice.model.User;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Ignore
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${local.server.port}")
    private int port;

    private static final Integer USER_ID = 1;
    private String apiUrl;
    private String userApiUrl;
    private String userByIdUrl;
    private String registrationUrl;

    @Before
    public void setup() {
        apiUrl = "http://localhost:" + port;
        userApiUrl = apiUrl + "/api/v1/users";
        userByIdUrl = apiUrl + "/id/" + USER_ID;
        registrationUrl = apiUrl + "/auth/registration";
    }

    @Test
    public void whenFindAllUsers_thenOK() throws Exception {
        ResultActions perform = mockMvc.perform(get(""));
        Response response = RestAssured.get(userApiUrl);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @Ignore
    public void whenFindById_thenOK() {
        UserDto randomUser = createRandomUser();
        createUserAsUri(randomUser);

        Response responseUser = RestAssured.get(userByIdUrl);
        User user = responseUser.getBody().jsonPath().getObject("", User.class);
        assertThat(user).isNotNull();
    }

    @Test
    @Ignore
    public void whenFindByName_thenOK() {
        UserDto randomUser = createRandomUser();
        createUserAsUri(randomUser);

        Response responseUser = RestAssured.get(userApiUrl + "/username/" + randomUser.getUsername());
        User user = responseUser.getBody().jsonPath().getObject("", User.class);
        assertThat(user).isNotNull();
    }

    @Test
    public void whenFindByNotExistId_thenNotFound() {
        Response responseUser = RestAssured.get(userApiUrl + "/id/-1");
        assertThat(responseUser.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Ignore
    public void whenSaveNewUser_thenOk() {
        UserDto randomUser = createRandomUser();

        Response response = RestAssured.given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(randomUser)
                .post(registrationUrl);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @Ignore
    public void whenUpdateUser_thenOk() {
        cleanUp();
        UserDto randomUser = createRandomUser();
        createUserAsUri(randomUser);
        User savedUser = RestAssured.get(userByIdUrl).jsonPath().getObject("", User.class);

        randomUser.setUsername(randomAlphabetic(10));

        RestAssured.given()
                .contentType(APPLICATION_JSON_VALUE)
                .body(randomUser)
                .put(userApiUrl);

        User updatedUser = RestAssured.get(userByIdUrl).jsonPath().getObject("", User.class);

        assertThat(updatedUser).isNotEqualTo(savedUser);
        assertThat(updatedUser.getId()).isEqualTo(savedUser.getId());
        assertThat(updatedUser.getUsername()).isNotEqualTo(savedUser.getUsername());
    }

    private String createUserAsUri(UserDto user) {
        Response response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(user)
                .post(registrationUrl);
        return userApiUrl + "/" + response.jsonPath().get("id");
    }

    private UserDto createRandomUser() {
        return UserDto.builder()
                .email(randomAlphabetic(10) + "@gmail.com")
                .username(randomAlphabetic(15))
                .build();
    }

    private void cleanUp() {
        Response response = RestAssured.get(userByIdUrl);
        if (response.getStatusCode() == HttpStatus.OK.value()) {
            RestAssured.delete(userApiUrl + "/" + USER_ID);
        }
    }
}