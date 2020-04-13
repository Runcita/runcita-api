package com.runcita.api.user;

public class UserNotFoundException extends Exception {

    UserNotFoundException(Long userId) {
        super("User with id {"+userId+"} is not found");
    }
}
