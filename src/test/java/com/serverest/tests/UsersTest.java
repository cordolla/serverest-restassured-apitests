package com.serverest.tests;

import com.serverest.config.BaseTest;
import com.serverest.datafactory.CartDataFactory;
import com.serverest.datafactory.ProductDataFactory;
import com.serverest.datafactory.UserDataFactory;

import com.serverest.model.UserRequest;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;

import static com.serverest.utils.TestHelpers.login;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersTest extends BaseTest {

    @Test
    @Tag("smoke")
    @DisplayName("TC-USERS-001 | Listar Usuarios")
    void listarUsuarios() {
        givenWithAllure()
                .when()
                .get("/usuarios")
                .then()
                .statusCode(200);
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-USERS-002 | Cadastrar Usuario")
    void cadastrarUsuario() {

        UserRequest user = UserDataFactory.usuarioValidoComum();

        givenWithAllure()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(201)
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .body("_id", notNullValue());
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-USERS-003 | Cadastrar Usuario com Email ja cadastrado")
    void naoDeveCadastrarUsuarioComEmailDuplicado() {

        UserRequest user = UserDataFactory.usuarioValidoComum();

        givenWithAllure()
                .body(user)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(201);

        givenWithAllure()
                .body(user)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(400)
                .body("message", equalTo("Este email já está sendo usado"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-USERS-004 | Buscar usuário por ID")
    void buscarUsuarioPorID() {

        UserRequest user = UserDataFactory.usuarioValidoComum();

        String idValido = givenWithAllure()
                .body(user)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(201)
                .extract()
                .path("_id");

        givenWithAllure()
                .when()
                .get("/usuarios/" + idValido)
                .then()
                .statusCode(200)
                .body("_id", equalTo(idValido))
                .body("nome", equalTo(user.getNome()));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-USERS-004 | Buscar usuário por ID")
    void buscarUsuarioPorIDNaoEcontrar() {

        String idInexistente = RandomStringUtils.randomAlphanumeric(16);

        givenWithAllure()
                .when()
                .get("/usuarios/" + idInexistente)
                .then()
                .statusCode(400)
                .body("message", equalTo("Usuário não encontrado"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-USERS-005 | Excluir Usuario")
    void excluirUsuarioPorId() {

        UserRequest user = UserDataFactory.usuarioValidoComum();

        String idValido = givenWithAllure()
                .body(user)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(201)
                .extract()
                .path("_id");


        givenWithAllure()
                .contentType(ContentType.JSON)
                .when()
                .delete("/usuarios/" + idValido)
                .then()
                .statusCode(200)
                .body("message", equalTo("Registro excluído com sucesso"));

    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-USERS-006 | Tentar excluir usuário com carrinho cadastrado")
    void tentarExcluirUsuarioComCarrinhoCadastrado() {

        UserRequest user = UserDataFactory.usuarioValidoComum();

        String idUsuario = givenWithAllure()
                .body(user)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(201)
                .extract()
                .path("_id");

        String idProduto = givenWithAllure()
                .header("Authorization", userToken)
                .body(ProductDataFactory.novoProdutoValido())
                .when()
                .post("/produtos")
                .then()
                .statusCode(201)
                .extract()
                .path("_id");

        String tokenUsuario = login(user);

        givenWithAllure()
                .header("Authorization", tokenUsuario)
                .body(CartDataFactory.carrinhoComProdutos(idProduto, 2))
                .when()
                .post("/carrinhos")
                .then()
                .statusCode(201);

        givenWithAllure()
                .header("Authorization", userToken)
                .when()
                .delete("/usuarios/" + idUsuario)
                .then()
                .statusCode(400)
                .body("message", equalTo("Não é permitido excluir usuário com carrinho cadastrado"))
                .body("idCarrinho", notNullValue());
    }

    @Test
    @Tag("smoke")
    @DisplayName("CT-USERS-007 | Editar Usuario")
    void editarUsuario() {

        UserRequest user = UserDataFactory.usuarioValidoComum();

        String idValido = givenWithAllure()
                .body(user)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(201)
                .extract()
                .path("_id");

        user.setNome("Nome Editado " + RandomStringUtils.randomAlphanumeric(4));

        givenWithAllure()
                .body(user)
                .when()
                .put("/usuarios/" + idValido)
                .then()
                .statusCode(200)
                .body("message", equalTo("Registro alterado com sucesso"));

    }

    @Test
    @Tag("smoke")
    @DisplayName("CT-USERS-008 | Criar usuário inexistente via PUT")
    void deveCriarUsuarioViaPutQuandoIdNaoExistir() {
        String idInexistente = RandomStringUtils.randomAlphanumeric(16);

        UserRequest user = UserDataFactory.usuarioValidoComum();

        givenWithAllure()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .put("/usuarios/" + idInexistente)
                .then()
                .statusCode(201)
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .body("_id", notNullValue());
    }

    @Test
    @Tag("smoke")
    @DisplayName("CT-USERS-008 | Tentar modificar usuario com email ja cadastrado")
    void deveTentarModificarDadosDaContaComEmailUtilizado() {

        UserRequest user = UserDataFactory.usuarioValidoComum();

        UserRequest userEmailUsado = UserDataFactory.usuarioValidoComum();

        givenWithAllure()
                .body(user)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(201);

        String idValido = givenWithAllure()
                .body(userEmailUsado)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(201)
                .extract()
                .path("_id");

        givenWithAllure()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .put("/usuarios/" + idValido)
                .then()
                .statusCode(400)
                .body("message", equalTo("Este email já está sendo usado"));
    }
}
