package com.serverest.model;

import java.util.List;

public class CartRequest {
    private List<CartItemRequest> produtos;

    public CartRequest(List<CartItemRequest> produtos) {
        this.produtos = produtos;
    }


    public CartRequest() {

    }

    public List<CartItemRequest> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<CartItemRequest> produtos) {
        this.produtos = produtos;
    }
}
