package com.serverest.tests;

import com.serverest.config.BaseTest;
import com.serverest.datafactory.CartDataFactory;
import com.serverest.datafactory.ProductDataFactory;
import com.serverest.datafactory.UserDataFactory;
import com.serverest.model.ProductRequest;
import com.serverest.model.UserRequest;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;

import static com.serverest.utils.TestHelpers.login;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductTest extends BaseTest {

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCTS-001 | Listar produtos cadastrados")
    void listarProdutosCadastrados() {
        givenWithAllure()
            .when()
            .get("/produtos")
            .then()
            .statusCode(200)
            .body("quantidade", notNullValue());
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCTS-001 | Cadastrar produto")
    void cadastrarProduto() {

        ProductRequest product = ProductDataFactory.novoProdutoValido();

        givenWithAllure()
            .header("Authorization", userToken)
            .body(product)
            .when()
            .post("/produtos")
            .then()
            .statusCode(201)
            .body("message", equalTo("Cadastro realizado com sucesso"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCTS-003 | Tentar cadastrar produto existente")
    void tentarCadastrarProdutoExistente() {

        ProductRequest product = ProductDataFactory.novoProdutoValido();

        givenWithAllure()
            .header("Authorization", userToken)
            .body(product)
            .when()
            .post("/produtos")
            .then()
            .statusCode(201);


        givenWithAllure()
            .header("Authorization", userToken)
            .body(product)
            .when()
            .post("/produtos")
            .then()
            .statusCode(400)
            .body("message", equalTo("Já existe produto com esse nome"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCTS-001 | Cadastrar produto com Token ausente, invalido ou expirado")
    void cadastrarProdutoSemToken() {

        ProductRequest product = ProductDataFactory.novoProdutoValido();

        given()
            .contentType(ContentType.JSON)
            .body(product)
            .when()
            .post("/produtos")
            .then()
            .statusCode(401)
            .body("message", equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCTS-005 | Buscar Produto Por ID")
    void buscarProdutoPorID() {

        ProductRequest product = ProductDataFactory.novoProdutoValido();

        String idProduto = givenWithAllure()
            .header("Authorization", userToken)
            .body(product)
            .when()
            .post("/produtos")
            .then()
            .statusCode(201)
            .extract()
            .path("_id");

        givenWithAllure()
            .when()
            .get("/produtos/" + idProduto)
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

        given()
            .when()
            .get("/produtos/" + idInvalido)
            .then()
            .statusCode(400)
            .body("message", equalTo("Produto não encontrado"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCT-007 | Excluir produto")
    void excluirProduto() {

        ProductRequest product = ProductDataFactory.novoProdutoValido();

        String idProduto = givenWithAllure()
            .header("Authorization", userToken)
            .body(product)
            .when()
            .post("/produtos")
            .then()
            .statusCode(201)
            .extract()
            .path("_id");


        givenWithAllure()
            .header("Authorization", userToken)
            .when()
            .delete("/produtos/" + idProduto)
            .then()
            .statusCode(200)
            .body("message", equalTo("Registro excluído com sucesso"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCT-008 | Tentar excluir produto que está em um carrinho")
    void excluirProdutoDeCarrinho(){

        UserRequest user = UserDataFactory.usuarioValidoComum();

        givenWithAllure()
            .body(user)
            .when()
            .post("/usuarios")
            .then()
            .statusCode(201);

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
            .delete("/produtos/" + idProduto)
            .then()
            .statusCode(400)
            .body("message", equalTo("Não é permitido excluir produto que faz parte de carrinho"));

    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCT-009 | tentar excluir produto sem token de acesso")
    void excluirComTokenAusente() {

        ProductRequest product = ProductDataFactory.novoProdutoValido();

        String idProduto = givenWithAllure()
            .header("Authorization", userToken)
            .body(product)
            .when()
            .post("/produtos")
            .then()
            .statusCode(201)
            .extract()
            .path("_id");

        givenWithAllure()
            .when()
            .delete("/produtos/" + idProduto)
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

        String idProduto = givenWithAllure()
            .header("Authorization", userToken)
            .body(product)
            .when()
            .post("/produtos")
            .then()
            .statusCode(201)
            .extract()
            .path("_id");

        product.setNome("Nome Editado" + RandomStringUtils.randomAlphanumeric(4));

        givenWithAllure()
            .header("Authorization", userToken)
            .body(product)
            .when()
            .put("/produtos/" + idProduto)
            .then()
            .statusCode(200)
            .body("message", equalTo("Registro alterado com sucesso"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCT-010 | Criar produto inexistente pelo PUT")
    void tentarEditarProdutoComIdValidoECadastrar() {

        String idInexistente = RandomStringUtils.randomAlphanumeric(16);

        givenWithAllure()
            .header("Authorization", userToken)
            .body(ProductDataFactory.novoProdutoValido())
            .when()
            .put("/produtos/" + idInexistente)
            .then()
            .statusCode(201)
            .body("message", equalTo("Cadastro realizado com sucesso"));

    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCT-011 | Tentar editar nome do produto com um nome ja usado")
    void tentarEditarProdutoComNomeJaUtilizado() {

        ProductRequest produtoA = ProductDataFactory.novoProdutoValido();

        String idProdutoA = givenWithAllure()
            .header("Authorization", userToken)
            .body(produtoA)
            .when()
            .post("/produtos")
            .then()
            .statusCode(201)
            .extract().path("_id");

        ProductRequest produtoB = ProductDataFactory.novoProdutoValido();

        givenWithAllure()
            .header("Authorization", userToken)
            .body(produtoB)
            .when()
            .post("/produtos")
            .then()
            .statusCode(201);

        produtoA.setNome(produtoB.getNome());

        givenWithAllure()
            .header("Authorization", userToken)
            .body(produtoA)
            .when()
            .put("/produtos/" + idProdutoA)
            .then()
            .statusCode(400)
            .body("message", equalTo("Já existe produto com esse nome"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCT-012 | Tentar editar produto com token inválido")
    void tentarEditarProdutoComTokenInvalido() {
        String idProdutoInexistente = "ID_QUALQUER";
        ProductRequest product = ProductDataFactory.novoProdutoValido();

        givenWithAllure()
            .header("Authorization", "token-completamente-errado-123")
            .body(product)
            .when()
            .put("/produtos/" + idProdutoInexistente)
            .then()
            .statusCode(401)
            .body("message", equalTo("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
    }
    @Test
    @Tag("smoke")
    @DisplayName("TC-PRODUCT-013 | Validar que usuário comum não pode editar produto")
    void validarUsuarioComumNaoPodeEditarProduto() {

        ProductRequest product = ProductDataFactory.novoProdutoValido();

        String idProduto = givenWithAllure()
            .header("Authorization", userToken)
            .body(product)
            .when()
            .post("/produtos")
            .then()
            .statusCode(201)
            .extract().path("_id");

        UserRequest userComum = UserDataFactory.usuarioValidoComum();

        givenWithAllure()
            .body(userComum)
            .when()
            .post("/usuarios")
            .then()
            .statusCode(201);

        String tokenComum = login(userComum);

        product.setNome("Tentativa de Edição por Usuário Comum");

        givenWithAllure()
            .header("Authorization", tokenComum)
            .body(product)
            .when()
            .put("/produtos/" + idProduto)
            .then()
            .statusCode(403)
            .body("message", equalTo("Rota exclusiva para administradores"));
    }
}
