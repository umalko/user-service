//package com.mavs.userservice.controller;
//
//import com.mavs.userservice.model.User;
//import io.restassured.RestAssured;
//import io.restassured.response.Response;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
//import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//
//
//@RunWith(SpringRunner.class)
//@ActiveProfiles("test")
//@SpringBootTest
//@AutoConfigureMockMvc
//public class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @LocalServerPort
//    private int port;
//
//    private static final Integer USER_ID = 1;
//    private String apiUrl;
//    private String userByIdUrl;
//
//    @Before
//    public void setup() {
//        apiUrl = "http://localhost:" + port + "/api/v1/users";
//        userByIdUrl = apiUrl + "/id/" + USER_ID;
//    }
//
//    @Test
//    public void whenFindAllUsers_thenOK() throws Exception {
//        ResultActions perform = mockMvc.perform(get(""));
//        Response response = RestAssured.get(apiUrl);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
//    }
//
//    @Test
//    public void whenFindById_thenOK() {
//        User randomUser = createRandomUser();
//        createUserAsUri(randomUser);
//
//        Response responseUser = RestAssured.get(userByIdUrl);
//        User user = responseUser.getBody().jsonPath().getObject("", User.class);
//        assertThat(user).isNotNull();
//    }
//
//    @Test
//    public void whenFindByName_thenOK() {
//        User randomUser = createRandomUser();
//        createUserAsUri(randomUser);
//
//        Response responseUser = RestAssured.get(apiUrl + "/username/" + randomUser.getName());
//        User user = responseUser.getBody().jsonPath().getObject("", User.class);
//        assertThat(user).isNotNull();
//    }
//
//    @Test
//    public void whenFindByNotExistId_thenNotFound() {
//        Response responseUser = RestAssured.get(apiUrl + "/id/-1");
//        assertThat(responseUser.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
//    }
//
//    @Test
//    public void whenSaveNewUser_thenOk() {
//        User randomUser = createRandomUser();
//
//        Response response = RestAssured.given()
//                .contentType(APPLICATION_JSON_VALUE)
//                .body(randomUser)
//                .post(apiUrl);
//
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
//    }
//
//    @Test
//    public void whenUpdateUser_thenOk() {
////        cleanUp();
////        User randomUser = createRandomUser();
////        createUserAsUri(randomUser);
////        User savedUser = RestAssured.get(userByIdUrl).jsonPath().getObject("", User.class);
////
////        randomUser.setId(USER_ID);
////        randomUser.setName(randomAlphabetic(10));
////
////        RestAssured.given()
////                .contentType(APPLICATION_JSON_VALUE)
////                .body(randomUser)
////                .put(apiUrl);
////
////        User updatedUser = RestAssured.get(userByIdUrl).jsonPath().getObject("", User.class);
////
////        assertThat(updatedUser).isNotEqualTo(savedUser);
////        assertThat(updatedUser.getId()).isEqualTo(savedUser.getId());
////        assertThat(updatedUser.getName()).isNotEqualTo(savedUser.getName());
//    }
//
//    @Test
//    public void whenDeleteUser_thenOk() {
////        User randoteponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
//    }
//
//    private String createUserAsUri(User user) {
//        Response response = RestAssured.given()
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .body(user)
//                .post(apiUrl);
//        return apiUrl + "/id/" + response.jsonPath().get("id");
//    }
//
//    private User createRandomUser() {
//        return User.builder()
//                .email(randomAlphabetic(10))
//                .name(randomAlphabetic(15))
//                .phone(randomAlphabetic(8))
//                .build();
//
//    }
//
//    private void cleanUp() {
//        Response response = RestAssured.get(userByIdUrl);
//        if (response.getStatusCode() == HttpStatus.OK.value()) {
//            RestAssured.delete(apiUrl + "/" + USER_ID);
//        }
//    }
//}