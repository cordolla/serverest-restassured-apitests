package com.serverest.utils;

import com.serverest.config.BaseTest;
import com.serverest.datafactory.CartDataFactory;
import com.serverest.datafactory.ProductDataFactory;
import com.serverest.datafactory.UserDataFactory;
import com.serverest.model.CartRequest;
import com.serverest.model.LoginRequest;
import com.serverest.model.ProductRequest;
import com.serverest.model.UserRequest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

public class TestHelpers extends BaseTest {

    public static String login(UserRequest user) {
        return given()
            .contentType(ContentType.JSON)
            .body(new LoginRequest(user.getEmail(), user.getPassword()))
            .when().post("/login")
            .then().extract().path("authorization");
    }

    public static String criarProdutoEObterId(String adminToken) {

        ProductRequest produto = ProductDataFactory.novoProdutoValido();

        return given()
            .header("Authorization", adminToken)
            .contentType("application/json")
            .body(produto)
            .when()
            .post("/produtos")
            .then()
            .statusCode(201)
            .extract()
            .path("_id");
    }

    public static String cadastrarCarrinhoEObterId(String adminToken) {

        String idProduto = criarProdutoEObterId(adminToken);

        UserRequest usuario = UserDataFactory.usuarioValidoComum();

        given().contentType("application/json").body(usuario).post("/usuarios");

        String tokenUsuario = login(usuario);

        CartRequest carrinho = CartDataFactory.carrinhoComProdutos(idProduto, 1);

        return given()
            .header("Authorization", tokenUsuario)
            .contentType("application/json")
            .body(carrinho)
            .when()
            .post("/carrinhos")
            .then()
            .statusCode(201)
            .extract()
            .path("_id");
    }
}
