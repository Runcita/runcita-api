package com.runcita.api.auth;

public class AuthNotFoundException extends Exception {

    AuthNotFoundException(String email) {
        super("User with email {"+email+"} is not found");
    }
}
