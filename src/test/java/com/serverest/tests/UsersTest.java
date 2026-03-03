package com.serverest.tests;

import com.serverest.client.CartClient;
import com.serverest.client.ProductClient;
import com.serverest.client.UserClient;
import com.serverest.config.BaseTest;
import com.serverest.datafactory.CartDataFactory;
import com.serverest.datafactory.ProductDataFactory;
import com.serverest.datafactory.UserDataFactory;

import com.serverest.model.CartRequest;
import com.serverest.model.ProductRequest;
import com.serverest.model.UserRequest;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;

import static com.serverest.utils.TestHelpers.login;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersTest extends BaseTest {

    @Test
    @Tag("smoke")
    @DisplayName("TC-USERS-001 | Listar Usuarios")
    void listarUsuarios() {

        UserClient.listarUsuarios()
            .then()
            .statusCode(200);
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-USERS-002 | Cadastrar Usuario")
    void cadastrarUsuario() {

        UserRequest user = UserDataFactory.usuarioValidoComum();

        UserClient.cadastrarUsuario(user)
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

        UserClient.cadastrarUsuario(user)
            .then()
            .statusCode(201);

        UserClient.cadastrarUsuario(user)
            .then()
            .statusCode(400)
            .body("message", equalTo("Este email já está sendo usado"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-USERS-004 | Buscar usuário por ID")
    void buscarUsuarioPorID() {

        UserRequest user = UserDataFactory.usuarioValidoComum();
        String idValido = UserClient.cadastrarUsuario(user).path("_id");

        UserClient.buscarUsuarioPorId(idValido)
            .then()
            .statusCode(200)
            .body("_id", equalTo(idValido))
            .body("nome", equalTo(user.getNome()));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-USERS-004 | Buscar usuário por ID inexistente")
    void buscarUsuarioPorIDNaoEcontrar() {

        String idInexistente = RandomStringUtils.randomAlphanumeric(16);

        UserClient.buscarUsuarioPorId(idInexistente)
            .then()
            .statusCode(400)
            .body("message", equalTo("Usuário não encontrado"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-USERS-005 | Excluir Usuario")
    void excluirUsuarioPorId() {

        UserRequest user = UserDataFactory.usuarioValidoComum();
        String idValido = UserClient.cadastrarUsuario(user).path("_id");

        UserClient.excluirUsuario(idValido)
            .then()
            .statusCode(200)
            .body("message", equalTo("Registro excluído com sucesso"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-USERS-006 | Tentar excluir usuário com carrinho cadastrado")
    void tentarExcluirUsuarioComCarrinhoCadastrado() {

        UserRequest user = UserDataFactory.usuarioValidoComum();
        String idValidoUsuario = UserClient.cadastrarUsuario(user).path("_id");

        ProductRequest product = ProductDataFactory.novoProdutoValido();
        String idValidoProduto = ProductClient.cadastrarProduto(product, userToken).path("_id");

        String tokenUsuario = login(user);
        CartRequest cart = CartDataFactory.carrinhoComProdutos(idValidoProduto, 2);

        CartClient.cadastrarCarrinho(cart, tokenUsuario)
            .then()
            .statusCode(201);

        Response response = UserClient.excluirUsuario(idValidoUsuario);

        response.then()
            .statusCode(400)
            .body("message", equalTo("Não é permitido excluir usuário com carrinho cadastrado"))
            .body("idCarrinho", notNullValue());
    }

    @Test
    @Tag("smoke")
    @DisplayName("CT-USERS-007 | Editar Usuario")
    void editarUsuario() {

        UserRequest user = UserDataFactory.usuarioValidoComum();
        String idValido = UserClient.cadastrarUsuario(user).path("_id");

        user.setNome("Nome Editado " + RandomStringUtils.randomAlphanumeric(4));

        UserClient.editarUsuario(idValido, user)
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

        UserClient.editarUsuario(idInexistente, user)
            .then()
            .statusCode(201)
            .body("message", equalTo("Cadastro realizado com sucesso"))
            .body("_id", notNullValue());
    }

    @Test
    @Tag("smoke")
    @DisplayName("CT-USERS-008 | Tentar modificar usuario com email ja cadastrado")
    void deveTentarModificarDadosDaContaComEmailUtilizado() {

        UserRequest userA = UserDataFactory.usuarioValidoComum();
        UserClient.cadastrarUsuario(userA)
            .then()
            .statusCode(201);

        UserRequest userB = UserDataFactory.usuarioValidoComum();
        String idUsuarioB = UserClient.cadastrarUsuario(userB)
            .then()
            .statusCode(201)
            .extract().path("_id");

        userB.setEmail(userA.getEmail());

        Response response = UserClient.editarUsuario(idUsuarioB, userB);

        response.then()
            .statusCode(400)
            .body("message", equalTo("Este email já está sendo usado"));
    }

    @Test
    @Tag("contract")
    @DisplayName("CT-USERS-001 | Validar contrato da listagem de usuários")
    void validarContratoListaUsuarios() {
        UserClient.listarUsuarios()
            .then()
            .statusCode(200)
            .body(matchesJsonSchemaInClasspath("schemas/usuarios-lista-schema.json"));
    }
}
