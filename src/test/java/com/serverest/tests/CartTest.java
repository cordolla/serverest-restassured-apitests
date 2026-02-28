package com.serverest.tests;

import com.serverest.config.BaseTest;
import com.serverest.datafactory.CartDataFactory;
import com.serverest.datafactory.ProductDataFactory;
import com.serverest.datafactory.UserDataFactory;
import com.serverest.model.CartRequest;
import com.serverest.model.ProductRequest;
import com.serverest.model.UserRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.serverest.utils.TestHelpers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CartTest extends BaseTest {

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-001 | Cadastrar carrinho com sucesso")
    void cadastrarCarrinhoComSucesso() {

        ProductRequest produto = ProductDataFactory.novoProdutoValido();

        String idProduto = givenWithAllure()
            .header("Authorization", userToken)
            .body(produto)
            .when()
            .post("/produtos")
            .then()
            .statusCode(201)
            .extract().path("_id");


        UserRequest usuario = UserDataFactory.usuarioValidoComum();

        givenWithAllure()
            .body(usuario)
            .when()
            .post("/usuarios")
            .then()
            .statusCode(201);

        String tokenUsuario = login(usuario);

        CartRequest carrinho = CartDataFactory.carrinhoComProdutos(idProduto, 5);

        givenWithAllure()
            .header("Authorization", tokenUsuario)
            .body(carrinho)
            .when()
            .post("/carrinhos")
            .then()
            .statusCode(201)
            .body("message", equalTo("Cadastro realizado com sucesso"))
            .body("_id", notNullValue());
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-002 | Validar que não é permitido ter mais de 1 carrinho por usuário")
    void validarApenasUmCarrinhoPorUsuario() {

        String idProduto1 = criarProdutoEObterId(userToken);
        String idProduto2 = criarProdutoEObterId(userToken);

        UserRequest usuario = UserDataFactory.usuarioValidoComum();

        givenWithAllure()
            .body(usuario)
            .post("/usuarios")
            .then()
            .statusCode(201);

        String tokenUsuario = login(usuario);

        CartRequest primeiroCarrinho = CartDataFactory.carrinhoComProdutos(idProduto1, 1);

        givenWithAllure()
            .header("Authorization", tokenUsuario)
            .body(primeiroCarrinho)
            .when()
            .post("/carrinhos")
            .then()
            .statusCode(201);

        CartRequest segundoCarrinho = CartDataFactory.carrinhoComProdutos(idProduto2, 1);
        givenWithAllure()
            .header("Authorization", tokenUsuario)
            .body(segundoCarrinho)
            .when()
            .post("/carrinhos")
            .then()
            .statusCode(400)
            .body("message", equalTo("Não é permitido ter mais de 1 carrinho"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-003 | Validar que não é permitido possuir produto duplicado no carrinho")
    void validarProdutoDuplicadoNoCarrinho() {

        String idProduto = criarProdutoEObterId(userToken);

        UserRequest usuario = UserDataFactory.usuarioValidoComum();

        givenWithAllure()
            .body(usuario)
            .post("/usuarios")
            .then().statusCode(201);

        String tokenUsuario = login(usuario);

        CartRequest carrinhoDuplicado = CartDataFactory.carrinhoComProdutosDuplicados(idProduto);

        givenWithAllure()
            .header("Authorization", tokenUsuario)
            .body(carrinhoDuplicado)
            .when()
            .post("/carrinhos")
            .then()
            .statusCode(400)
            .body("message", equalTo("Não é permitido possuir produto duplicado"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-004 | Validar erro ao cadastrar carrinho com produto inexistente")
    void validarProdutoInexistenteNoCarrinho() {

        String idInexistente = "ID_INVENTADO_123";

        UserRequest usuario = UserDataFactory.usuarioValidoComum();

        givenWithAllure()
            .body(usuario)
            .post("/usuarios")
            .then().statusCode(201);

        String tokenUsuario = login(usuario);

        CartRequest carrinhoFalso = CartDataFactory.carrinhoComProdutos(idInexistente, 1);

        givenWithAllure()
            .header("Authorization", tokenUsuario)
            .body(carrinhoFalso)
            .when()
            .post("/carrinhos")
            .then()
            .statusCode(400)
            .body("message", equalTo("Produto não encontrado"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-005 | Validar erro ao cadastrar carrinho com quantidade superior ao stock")
    void validarStockInsuficienteNoCarrinho() {

        ProductRequest produto = ProductDataFactory.novoProdutoValido();
        produto.setQuantidade(5);

        String idProduto = givenWithAllure()
            .header("Authorization", userToken)
            .body(produto)
            .when()
            .post("/produtos")
            .then()
            .statusCode(201)
            .extract().path("_id");

        UserRequest usuario = UserDataFactory.usuarioValidoComum();

        givenWithAllure()
            .body(usuario)
            .post("/usuarios")
            .then()
            .statusCode(201);

        String tokenUsuario = login(usuario);

        CartRequest carrinhoAcimaDoStock = CartDataFactory.carrinhoComProdutos(idProduto, 6);

        givenWithAllure()
            .header("Authorization", tokenUsuario)
            .body(carrinhoAcimaDoStock)
            .when()
            .post("/carrinhos")
            .then()
            .statusCode(400)
            .body("message", equalTo("Produto não possui quantidade suficiente"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-006 | Validar bloqueio de acesso ao carrinho sem token")
    void validarCarrinhoSemToken() {

        CartRequest carrinho = CartDataFactory.carrinhoComProdutos("qualquer_id", 1);

        givenWithAllure()
            .header("Authorization", "")
            .body(carrinho)
            .when()
            .post("/carrinhos")
            .then()
            .statusCode(401)
            .body("message", equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-007 | Buscar carrinho por ID com sucesso")
    void buscarCarrinhoPorId() {

        String idCarrinho = cadastrarCarrinhoEObterId(userToken);

        givenWithAllure()
            .pathParam("_id", idCarrinho)
            .when()
            .get("/carrinhos/{_id}")
            .then()
            .statusCode(200)
            .body("_id", equalTo(idCarrinho))
            .body("idUsuario", notNullValue())
            .body("produtos", notNullValue());
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-008 | Validar erro ao buscar carrinho com ID inexistente")
    void validarBuscaCarrinhoInexistente() {

        String idInexistente = RandomStringUtils.randomAlphanumeric(16);

        givenWithAllure()
            .pathParam("_id", idInexistente)
            .when()
            .get("/carrinhos/{_id}")
            .then()
            .statusCode(400)
            .body("message", equalTo("Carrinho não encontrado"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-009 | Concluir compra com sucesso")
    void concluirCompraComSucesso() {

        UserRequest usuario = UserDataFactory.usuarioValidoComum();

        givenWithAllure()
            .body(usuario)
            .post("/usuarios");

        String tokenUsuario = login(usuario);

        String idProduto = criarProdutoEObterId(userToken);

        CartRequest carrinho = CartDataFactory.carrinhoComProdutos(idProduto, 1);

        givenWithAllure()
            .header("Authorization", tokenUsuario)
            .body(carrinho)
            .post("/carrinhos");

        givenWithAllure()
            .header("Authorization", tokenUsuario)
            .when()
            .delete("/carrinhos/concluir-compra")
            .then()
            .statusCode(200)
            .body("message", equalTo("Registro excluído com sucesso"));

        givenWithAllure()
            .header("Authorization", tokenUsuario)
            .when()
            .get("/carrinhos")
            .then()
            .statusCode(200)
            .body("quantidade", notNullValue());
    }

    @Test
    @Tag("security")
    @DisplayName("TC-CART-010 | Validar erro com token inválido")
    void validarErroTokenInvalido() {

        givenWithAllure()
            .header("Authorization", "")
            .body(CartDataFactory.carrinhoComProdutos("id", 1))
            .when()
            .post("/carrinhos")
            .then()
            .statusCode(401)
            .body("message", equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-011 | Cancelar compra com sucesso (estorno de estoque)")
    void cancelarCompraComSucesso() {

        UserRequest usuario = UserDataFactory.usuarioValidoComum();

        givenWithAllure()
            .body(usuario)
            .post("/usuarios");

        String tokenUsuario = login(usuario);

        String idProduto = criarProdutoEObterId(userToken);

        CartRequest carrinho = CartDataFactory.carrinhoComProdutos(idProduto, 5);

        givenWithAllure()
            .header("Authorization", tokenUsuario)
            .body(carrinho)
            .post("/carrinhos");

        givenWithAllure()
            .header("Authorization", tokenUsuario)
            .when()
            .delete("/carrinhos/cancelar-compra")
            .then()
            .statusCode(200)
            .body("message", equalTo("Registro excluído com sucesso. Estoque dos produtos reabastecido"));
    }

    @Test
    @Tag("negative")
    @DisplayName("TC-CART-012 | Validar erro ao cancelar compra sem possuir carrinho")
    void validarErroAoCancelarSemCarrinho() {

        UserRequest usuario = UserDataFactory.usuarioValidoComum();

        givenWithAllure()
            .body(usuario)
            .post("/usuarios");

        String tokenUsuario = login(usuario);

        givenWithAllure()
            .header("Authorization", tokenUsuario)
            .when()
            .delete("/carrinhos/cancelar-compra")
            .then()
            .statusCode(200)
            .body("message", equalTo("Não foi encontrado carrinho para esse usuário"));
    }

    @Test
    @Tag("security")
    @DisplayName("TC-CART-013 | Validar que a API bloqueia requisições sem token")
    void validarErroTokenAusente() {

        givenWithAllure()
            .header("Authorization", "")
            .when()
            .post("/carrinhos")
            .then()
            .statusCode(401)
            .body("message", equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
    }
}
