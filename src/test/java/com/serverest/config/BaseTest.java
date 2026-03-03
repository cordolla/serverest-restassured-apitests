package com.serverest.config;

import com.serverest.client.CartClient;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

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

    @BeforeEach
    void limparCarrinhoAntigo() {
        CartClient.concluirCompra(userToken);
    }


    public static RequestSpecification givenWithAllure() {
        return given()
            .filter(new AllureRestAssured())
            .contentType(ContentType.JSON);
    }


}