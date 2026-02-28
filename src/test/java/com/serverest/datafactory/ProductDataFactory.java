package com.serverest.datafactory;

import com.github.javafaker.Faker;
import com.serverest.model.ProductRequest;

public class ProductDataFactory {

    private static final Faker faker = new Faker();

    public static ProductRequest novoProdutoValido(){
        return new ProductRequest(
                faker.commerce().productName() + " " + faker.number().digits(5),
                faker.number().numberBetween(10, 1000),
                faker.commerce().material(),
                faker.number().numberBetween(1, 100)
        );
    }
}
