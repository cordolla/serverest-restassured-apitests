package com.serverest.config;

import com.serverest.model.LoginRequest;
import com.serverest.model.UserRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthHelper {

    private static String userToken;

    public static String loginUserStoreToken(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);

        Response response = given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
            .when()
            .post("/login");

        if (response.getStatusCode() != 200) {
            System.out.println("Admin não encontrado, criando usuário semeador...");
            setupInitialAdmin(email, password);

            response = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/login");
        }

        userToken = response.path("authorization");
        return userToken;
    }

    private static void setupInitialAdmin(String email, String password) {
        UserRequest admin = new UserRequest("Admin Semeador", email, password, "true");
        given()
            .contentType(ContentType.JSON)
            .body(admin)
            .when()
            .post("/usuarios");
    }

    public static String getUserToken() {
        return userToken;
    }
}