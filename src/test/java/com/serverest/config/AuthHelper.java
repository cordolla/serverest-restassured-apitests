package com.serverest.config;

import com.serverest.model.LoginRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class AuthHelper {

    private static String userToken;

    public static void loginUserStoreToken(){

        given()
                .contentType(ContentType.JSON)
                .body("{\"nome\": \"Fulano QA\", \"email\": \"marcelotestesadm@qa.com\", \"password\": \"teste\", \"administrador\": \"true\"}")
                .when()
                .post("https://serverest.dev/usuarios");

        LoginRequest userLogin = new LoginRequest("marcelotestesadm@qa.com", "teste");

        Response userResponse = given()
                .contentType(ContentType.JSON)
                .body(userLogin)
                .when()
                .post("https://serverest.dev/login");

        if (userResponse.statusCode() == 200){
            userToken = userResponse.jsonPath().getString("authorization");
        } else {
            System.out.println("FALHA NO LOGIN: " + userResponse.asString());
            throw new RuntimeException("Não foi possível gerar o token de acesso.");
        }
    }

    public static String getUserToken() {
        return userToken;
    }
}