package com.serverest.tests;

import com.serverest.client.CartClient;
import com.serverest.client.ProductClient;
import com.serverest.client.UserClient;
import com.serverest.config.AuthHelper;
import com.serverest.config.BaseTest;
import com.serverest.datafactory.CartDataFactory;
import com.serverest.datafactory.ProductDataFactory;
import com.serverest.datafactory.UserDataFactory;
import com.serverest.model.CartRequest;
import com.serverest.model.ProductRequest;
import com.serverest.model.UserRequest;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.serverest.utils.TestHelpers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CartTest extends BaseTest {

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-001 | Cadastrar carrinho com sucesso")
    void cadastrarCarrinhoComSucesso() {

        ProductRequest produto = ProductDataFactory.novoProdutoValido();
        var responseProduto = ProductClient.cadastrarProduto(produto, userToken)
            .then()
            .statusCode(201)
            .extract();

        String idProduto = responseProduto.path("_id");
        int estoqueDisponivel = produto.getQuantidade();

        UserRequest usuario = UserDataFactory.usuarioValidoComum();
        UserClient.cadastrarUsuario(usuario);
        String tokenUsuario = AuthHelper.loginUserStoreToken(usuario.getEmail(), usuario.getPassword());

        CartRequest carrinho = CartDataFactory.carrinhoComProdutos(idProduto, estoqueDisponivel);

        CartClient.cadastrarCarrinho(carrinho, tokenUsuario)
            .then()
            .statusCode(201)
            .body("message", equalTo("Cadastro realizado com sucesso"))
            .body("_id", notNullValue());
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-002 | Validar que não é permitido ter mais de 1 carrinho por usuário")
    void validarApenasUmCarrinhoPorUsuario() {

        ProductRequest produto = ProductDataFactory.novoProdutoValido();
        String idProduto = ProductClient.cadastrarProduto(produto, userToken).path("_id");

        UserRequest usuario = UserDataFactory.usuarioValidoComum();
        UserClient.cadastrarUsuario(usuario);

        String tokenUsuario = login(usuario);

        CartRequest primeiroCarrinho = CartDataFactory.carrinhoComProdutos(idProduto, 1);

        CartClient.cadastrarCarrinho(primeiroCarrinho, tokenUsuario);

        CartRequest segundoCarrinho = CartDataFactory.carrinhoComProdutos(idProduto, 2);

        CartClient.cadastrarCarrinho(segundoCarrinho, tokenUsuario)
            .then()
            .statusCode(400)
            .body("message", equalTo("Não é permitido ter mais de 1 carrinho"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-003 | Validar que não é permitido possuir produto duplicado no carrinho")
    void validarProdutoDuplicadoNoCarrinho() {

        ProductRequest produto = ProductDataFactory.novoProdutoValido();
        String idProduto = ProductClient.cadastrarProduto(produto, userToken).path("_id");

        UserRequest usuario = UserDataFactory.usuarioValidoComum();
        UserClient.cadastrarUsuario(usuario);

        String tokenUsuario = login(usuario);

        CartRequest carrinhoDuplicado = CartDataFactory.carrinhoComProdutosDuplicados(idProduto);

        CartClient.cadastrarCarrinho(carrinhoDuplicado, tokenUsuario)
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
        UserClient.cadastrarUsuario(usuario);

        String tokenUsuario = login(usuario);

        CartRequest carrinhoFalso = CartDataFactory.carrinhoComProdutos(idInexistente, 1);

        CartClient.cadastrarCarrinho(carrinhoFalso, tokenUsuario)
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

        String idProduto = ProductClient.cadastrarProduto(produto, userToken).path("_id");

        UserRequest usuario = UserDataFactory.usuarioValidoComum();
        UserClient.cadastrarUsuario(usuario);

        String tokenUsuario = login(usuario);

        CartRequest carrinhoAcimaDoStock = CartDataFactory.carrinhoComProdutos(idProduto, 6);

        CartClient.cadastrarCarrinho(carrinhoAcimaDoStock, tokenUsuario)
            .then()
            .statusCode(400)
            .body("message", equalTo("Produto não possui quantidade suficiente"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-006 | Validar bloqueio de acesso ao carrinho sem token")
    void validarCarrinhoSemToken() {

        CartRequest carrinho = CartDataFactory.carrinhoComProdutos("qualquer_id", 1);

        CartClient.cadastrarCarrinho(carrinho, " ")
            .then()
            .statusCode(401)
            .body("message", equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-007 | Buscar carrinho por ID com sucesso")
    void buscarCarrinhoPorId() {

        ProductRequest produto = ProductDataFactory.novoProdutoValido();
        String idProduto = ProductClient.cadastrarProduto(produto, userToken).path("_id");

        CartRequest carrinho = CartDataFactory.carrinhoComProdutos(idProduto, 1);

        String idCarrinho = CartClient.cadastrarCarrinho(carrinho, userToken)
            .then()
            .statusCode(201)
            .extract()
            .path("_id");

        CartClient.buscarCarrinhoPorId(idCarrinho)
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

        CartClient.buscarCarrinhoPorId(idInexistente)
            .then()
            .statusCode(400)
            .body("message", equalTo("Carrinho não encontrado"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-009 | Concluir compra com sucesso")
    void concluirCompraComSucesso() {

        UserRequest usuario = UserDataFactory.usuarioValidoComum();
        UserClient.cadastrarUsuario(usuario);
        String tokenUsuario = login(usuario);

        ProductRequest produto = ProductDataFactory.novoProdutoValido();
        String idProduto = ProductClient.cadastrarProduto(produto, userToken).path("_id");

        CartRequest carrinho = CartDataFactory.carrinhoComProdutos(idProduto, 1);
        CartClient.cadastrarCarrinho(carrinho, tokenUsuario);

        Response response = CartClient.concluirCompra(tokenUsuario);

        response.then()
            .statusCode(200)
            .body("message", equalTo("Registro excluído com sucesso"));
    }

    @Test
    @Tag("security")
    @DisplayName("TC-CART-010 | Validar erro com token inválido")
    void validarErroTokenInvalido() {

        CartRequest carrinho = CartDataFactory.carrinhoComProdutos("id", 1);
        CartClient.cadastrarCarrinho(carrinho, "")
            .then()
            .statusCode(401)
            .body("message", equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CART-011 | Cancelar compra com sucesso (estorno de estoque)")
    void cancelarCompraComSucesso() {

        UserRequest usuario = UserDataFactory.usuarioValidoComum();
        UserClient.cadastrarUsuario(usuario);
        String tokenUsuario = login(usuario);

        ProductRequest produto = ProductDataFactory.novoProdutoValido();
        String idProduto = ProductClient.cadastrarProduto(produto, userToken).path("_id");

        CartRequest carrinho = CartDataFactory.carrinhoComProdutos(idProduto, 5);

        CartClient.cadastrarCarrinho(carrinho, tokenUsuario);

        CartClient.cancelarCompra(tokenUsuario)
            .then()
            .statusCode(200)
            .body("message", equalTo("Registro excluído com sucesso. Estoque dos produtos reabastecido"));
    }

    @Test
    @Tag("negative")
    @DisplayName("TC-CART-012 | Validar erro ao cancelar compra sem possuir carrinho")
    void validarErroAoCancelarSemCarrinho() {

        UserRequest usuario = UserDataFactory.usuarioValidoComum();
        UserClient.cadastrarUsuario(usuario);
        String tokenUsuario = login(usuario);

        CartClient.cancelarCompra(tokenUsuario)
            .then()
            .statusCode(200)
            .body("message", equalTo("Não foi encontrado carrinho para esse usuário"));
    }

    @Test
    @Tag("security")
    @DisplayName("TC-CART-013 | Validar que a API bloqueia requisições sem token")
    void validarErroTokenAusente() {

        CartClient.cancelarCompra("")
            .then()
            .statusCode(401)
            .body("message", equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
    }

    @Test
    @Tag("contract")
    @DisplayName("TC-CARTS-001 | Validar contrato da listagem de carrinhos")
    void validarContratoListaCarrinhos() {
        CartClient.listarCarrinhos()
            .then()
            .statusCode(200)
            .body(matchesJsonSchemaInClasspath("schemas/carrinhos-schema.json"));
    }
}
