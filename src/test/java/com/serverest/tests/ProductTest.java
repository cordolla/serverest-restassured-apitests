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
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductTest extends BaseTest {

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCTS-001 | Listar produtos cadastrados")
    void listarProdutosCadastrados() {

        ProductClient.listarProdutos()
            .then()
            .statusCode(200);
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCTS-001 | Cadastrar produto")
    void cadastrarProduto() {

        ProductRequest product = ProductDataFactory.novoProdutoValido();

        ProductClient.cadastrarProduto(product, userToken)
            .then()
            .statusCode(201)
            .body("message", equalTo("Cadastro realizado com sucesso"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCTS-003 | Tentar cadastrar produto existente")
    void tentarCadastrarProdutoExistente() {

        ProductRequest product = ProductDataFactory.novoProdutoValido();

        ProductClient.cadastrarProduto(product, userToken)
            .then()
            .statusCode(201);

        ProductClient.cadastrarProduto(product, userToken)
            .then()
            .statusCode(400)
            .body("message", equalTo("Já existe produto com esse nome"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCTS-001 | Cadastrar produto com Token ausente, invalido ou expirado")
    void cadastrarProdutoSemToken() {

        ProductRequest product = ProductDataFactory.novoProdutoValido();

        ProductClient.cadastrarProduto(product, " ")
            .then()
            .statusCode(401)
            .body("message", equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCTS-005 | Buscar Produto Por ID")
    void buscarProdutoPorID() {

        ProductRequest product = ProductDataFactory.novoProdutoValido();

        String idProduto = ProductClient.cadastrarProduto(product, userToken).path("_id");

        ProductClient.buscarProdutoPorId(idProduto)
            .then()
            .statusCode(200)
            .body("_id", equalTo(idProduto))
            .body("nome", equalTo(product.getNome()));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCTS-006 | Buscar e não encontrar produto por ID")
    void buscarProdutoPorIdNaoEncontrar() {
        String idInvalido = "9999999999999999";

        ProductClient.buscarProdutoPorId(idInvalido)
            .then()
            .statusCode(400)
            .body("message", equalTo("Produto não encontrado"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCT-007 | Excluir produto")
    void excluirProduto() {

        ProductRequest product = ProductDataFactory.novoProdutoValido();

        String idProduto = ProductClient.cadastrarProduto(product, userToken).path("_id");

        ProductClient.excluirProduto(idProduto, userToken)
            .then()
            .statusCode(200)
            .body("message", equalTo("Registro excluído com sucesso"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCT-008 | Tentar excluir produto que está em um carrinho")
    void excluirProdutoDeCarrinho(){

        UserRequest user = UserDataFactory.usuarioValidoComum();
        UserClient.cadastrarUsuario(user)
            .then()
            .statusCode(201);

        ProductRequest product = ProductDataFactory.novoProdutoValido();
        String idProduto = ProductClient.cadastrarProduto(product, userToken).path("_id");

        String tokenUsuario = login(user);

        CartRequest cart = CartDataFactory.carrinhoComProdutos(idProduto, 2);
        CartClient.cadastrarCarrinho(cart, tokenUsuario)
            .then()
            .statusCode(201);

        ProductClient.excluirProduto(idProduto, userToken)
            .then()
            .statusCode(400)
            .body("message", equalTo("Não é permitido excluir produto que faz parte de carrinho"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCT-009 | tentar excluir produto sem token de acesso")
    void excluirComTokenAusente() {

        ProductRequest product = ProductDataFactory.novoProdutoValido();
        String idProduct = ProductClient.cadastrarProduto(product, userToken).path("_id");

        ProductClient.excluirProduto(idProduct, " ")
            .then()
            .statusCode(401)
            .log().all()
            .body("message", equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCT-010 | Editar Nome do Produto")
    void editarProduto() {

        ProductRequest product = ProductDataFactory.novoProdutoValido();
        String idProduct = ProductClient.cadastrarProduto(product, userToken).path("_id");

        String novoNome = "Produto Editado " + RandomStringUtils.randomAlphanumeric(4);
        product.setNome(novoNome);

        Response response = ProductClient.editarProduto(idProduct, product, userToken);

        response.then()
            .statusCode(200)
            .body("message", equalTo("Registro alterado com sucesso"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCT-010 | Criar produto inexistente pelo PUT")
    void tentarEditarProdutoComIdValidoECadastrar() {

        String idInexistente = RandomStringUtils.randomAlphanumeric(16);
        ProductRequest product = ProductDataFactory.novoProdutoValido();

        ProductClient.editarProduto(idInexistente, product, userToken)
            .then()
            .statusCode(201)
            .body("message", equalTo("Cadastro realizado com sucesso"));

    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCT-011 | Tentar editar nome do produto com um nome ja usado")
    void tentarEditarProdutoComNomeJaUtilizado() {

        ProductRequest produtoA = ProductDataFactory.novoProdutoValido();
        String idProdutoA = ProductClient.cadastrarProduto(produtoA, userToken).path("_id");

        ProductRequest produtoB = ProductDataFactory.novoProdutoValido();
        ProductClient.cadastrarProduto(produtoB, userToken);

        produtoA.setNome(produtoB.getNome());

        ProductClient.editarProduto(idProdutoA, produtoB, userToken)
            .then()
            .statusCode(400)
            .body("message", equalTo("Já existe produto com esse nome"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCT-012 | Tentar editar produto com token inválido")
    void tentarEditarProdutoComTokenInvalido() {

        String idProdutoInexistente = "ID_QUALQUER";

        ProductClient.excluirProduto(idProdutoInexistente, " ")
            .then()
            .statusCode(401)
            .body("message", equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
    }
    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCT-013 | Validar que usuário comum não pode editar produto")
    void validarUsuarioComumNaoPodeEditarProduto() {

        ProductRequest product = ProductDataFactory.novoProdutoValido();
        String idProduto = ProductClient.cadastrarProduto(product, userToken).path("_id");

        UserRequest user = UserDataFactory.usuarioValidoComum();
        UserClient.cadastrarUsuario(user);

        String tokenComum = login(user);

        product.setNome("Tentativa de Edição por Usuário Comum");

        ProductClient.excluirProduto(idProduto, tokenComum)
            .then()
            .statusCode(403)
            .body("message", equalTo("Rota exclusiva para administradores"));
    }
}
