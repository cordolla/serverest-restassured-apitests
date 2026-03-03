package com.serverest.client;

import com.serverest.config.BaseTest;
import com.serverest.model.ProductRequest;
import io.restassured.response.Response;

public class ProductClient extends BaseTest {

    private static final String ENDPOINT_PRODUTOS = "/produtos";

    public static Response listarProdutos() {
        return givenWithAllure()
            .when()
            .get(ENDPOINT_PRODUTOS);
    }

    public static Response cadastrarProduto(ProductRequest product, String token) {
        return givenWithAllure()
            .header("Authorization", token)
            .body(product)
            .when()
            .post(ENDPOINT_PRODUTOS);
    }

    public static Response buscarProdutoPorId(String id) {
        return givenWithAllure()
            .when()
            .get(ENDPOINT_PRODUTOS + "/" + id);
    }

    public static Response editarProduto(String id, ProductRequest product, String token) {
        return givenWithAllure()
            .header("Authorization", token)
            .body(product)
            .when()
            .put(ENDPOINT_PRODUTOS + "/" + id);
    }

    public static Response excluirProduto(String id, String token) {
        return givenWithAllure()
            .header("Authorization", token)
            .when()
            .delete(ENDPOINT_PRODUTOS + "/" + id);
    }
}
