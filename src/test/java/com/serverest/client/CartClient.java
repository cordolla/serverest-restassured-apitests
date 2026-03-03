package com.serverest.client;

import com.serverest.config.BaseTest;
import com.serverest.model.CartRequest;
import io.restassured.response.Response;

public class CartClient extends BaseTest {

    private static final String ENDPOINT_CARRINHOS = "/carrinhos";

    public static Response listarCarrinhos() {
        return givenWithAllure()
            .when()
            .get(ENDPOINT_CARRINHOS);
    }

    public static Response cadastrarCarrinho(CartRequest cart, String token) {
        return givenWithAllure()
            .header("Authorization", token)
            .body(cart)
            .when()
            .post(ENDPOINT_CARRINHOS);
    }

    public static Response buscarCarrinhoPorId(String id) {
        return givenWithAllure()
            .when()
            .get(ENDPOINT_CARRINHOS + "/" + id);
    }

    public static Response concluirCompra(String token) {
        return givenWithAllure()
            .header("Authorization", token)
            .when()
            .delete(ENDPOINT_CARRINHOS + "/concluir-compra");
    }

    public static Response cancelarCompra(String token) {
        return givenWithAllure()
            .header("Authorization", token)
            .when()
            .delete(ENDPOINT_CARRINHOS + "/cancelar-compra");
    }
}