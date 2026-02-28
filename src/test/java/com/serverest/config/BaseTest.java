package com.serverest.config;

import com.serverest.model.LoginRequest;
import com.serverest.model.UserRequest;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;

public abstract class BaseTest {
    protected static final String BASE_URL = "https://serverest.dev";
    protected static String userToken;

    @BeforeAll
    static void setupBase() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        AuthHelper.loginUserStoreToken();

        userToken = AuthHelper.getUserToken();
    }

    public static RequestSpecification givenWithAllure() {
        return given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .log().all();
    }

}
