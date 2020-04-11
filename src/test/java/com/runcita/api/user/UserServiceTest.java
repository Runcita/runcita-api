package com.runcita.api.user;

import com.runcita.api.Application;
import com.runcita.api.shared.models.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    private final User USER = User.builder()
            .email("test@gmail.com")
            .password("12345678")
            .firstName("firstname")
            .lastName("lastname")
            .city("city")
            .birthday(new Timestamp(new Date().getTime()))
            .sexe(false)
            .build();

    @Test
    void getUserByEmail_test() {
        when(userRepository.findByEmail(USER.getEmail())).thenReturn(USER);
        assertEquals(USER, userService.getUserByEmail(USER.getEmail()));
    }

    @Test
    void save_test() {
        when(userRepository.save(USER)).thenReturn(USER);
        assertEquals(USER, userService.save(USER));
    }

    @Test
    void emailExists_with_true_value_test() {
        when(userRepository.existsByEmail(USER.getEmail())).thenReturn(true);
        assertTrue(userService.emailExists(USER.getEmail()));
    }

    @Test
    void emailExists_with_false_value_test() {
        when(userRepository.existsByEmail(USER.getEmail())).thenReturn(false);
        assertFalse(userService.emailExists(USER.getEmail()));
    }
}
