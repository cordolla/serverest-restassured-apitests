package com.serverest.datafactory;

import com.github.javafaker.Faker;
import com.serverest.model.UserRequest;

public class UserDataFactory {

    private static final Faker faker = new Faker();

    public static UserRequest usuarioValidoAdmin() {
        return new UserRequest(
                faker.name().fullName(),
                faker.internet().emailAddress(),
                "teste123",
                "true"
        );
    }
    public static UserRequest usuarioValidoComum() {
        return new UserRequest(
                faker.name().fullName(),
                faker.internet().emailAddress(),
                "teste123",
                "false"
        );
    }
}
