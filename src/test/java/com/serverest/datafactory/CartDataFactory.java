package com.serverest.datafactory;

import com.serverest.model.CartItemRequest;
import com.serverest.model.CartRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartDataFactory {

    public static CartRequest carrinhoComProdutos(String idProduto, int quantidade) {
        CartItemRequest item = new CartItemRequest(idProduto, quantidade);
        return new CartRequest(Collections.singletonList(item));
    }

    public static CartRequest carrinhoComProdutosDuplicados(String idProduto) {
        List<CartItemRequest> lista = new ArrayList<>();
        lista.add(new CartItemRequest(idProduto, 1));
        lista.add(new CartItemRequest(idProduto, 2));
        return new CartRequest(lista);
    }

}
