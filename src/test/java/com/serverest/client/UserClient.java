package com.serverest.client;

import com.serverest.config.BaseTest;
import com.serverest.model.UserRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class UserClient extends BaseTest {

    public static Response cadastrarUsuario(UserRequest user) {
        return givenWithAllure()
            .contentType(ContentType.JSON)
            .body(user)
            .when()
            .post("/usuarios");
    }

    public static Response listarUsuarios() {
        return givenWithAllure()
            .when()
            .get("/usuarios");
    }

    public static Response buscarUsuarioPorId(String id) {
        return givenWithAllure()
            .when()
            .get("/usuarios/" + id);
    }

    public static Response editarUsuario(String id, UserRequest user) {
        return givenWithAllure()
            .contentType(ContentType.JSON)
            .body(user)
            .when()
            .put("/usuarios/" + id);
    }

    public static Response excluirUsuario(String id) {
        return givenWithAllure()
            .when()
            .delete("/usuarios/" + id);
    }
}
