package com.serverest.config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;

public abstract class BaseTest {

    protected static String userToken;

    @BeforeAll
    public static void setupBase() {
        RestAssured.baseURI = ConfigurationManager.getProperty("base.uri");
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        userToken = AuthHelper.loginUserStoreToken(
            ConfigurationManager.getProperty("admin.email"),
            ConfigurationManager.getProperty("admin.password")
        );
    }

    public static RequestSpecification givenWithAllure() {
        RequestSpecification spec = given()
            .filter(new AllureRestAssured())
            .contentType(ContentType.JSON);

        if ("true".equalsIgnoreCase(ConfigurationManager.getProperty("log.all"))) {
            spec.log().all();
        }

        return spec;
    }
}