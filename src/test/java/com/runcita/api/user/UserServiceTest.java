package com.runcita.api.user;

import com.runcita.api.Application;
import com.runcita.api.shared.models.City;
import com.runcita.api.shared.models.Profile;
import com.runcita.api.shared.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    private User USER;

    @BeforeEach
    void initBeforeTest() {
        this.USER = User.builder()
                .id(111L)
                .email("user@gmail.com")
                .password("12345678")
                .profile(Profile.builder()
                        .firstName("firstname")
                        .lastName("lastname")
                        .city(City.builder()
                                .name("city")
                                .code(1)
                                .build())
                        .birthday(1586653063000L)
                        .sexe(false)
                        .build())
                .build();
    }

    @Test
    void getUserById_test() throws UserNotFoundException {
        when(userRepository.findById(USER.getId(), 2)).thenReturn(Optional.of(USER));
        assertEquals(USER, userService.getUserById(USER.getId()));
    }

    @Test
    void getUserById_not_found_test() {
        when(userRepository.findById(USER.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getUserById(USER.getId()));
    }

    @Test
    void getUserByEmail_test() {
        when(userRepository.findByEmail(USER.getEmail())).thenReturn(Optional.of(USER));
        assertEquals(USER, userService.getUserByEmail(USER.getEmail()).get());
    }

    @Test
    void getUserByEmail_not_found_test() {
        when(userRepository.findByEmail(USER.getEmail())).thenReturn(Optional.empty());
        assertTrue(userService.getUserByEmail(USER.getEmail()).isEmpty());
    }

    @Test
    void saveUser_test() {
        when(userRepository.save(USER)).thenReturn(USER);
        assertEquals(USER, userService.saveUser(USER));
        verify(userRepository).save(USER);
    }

    @Test
    void deleteUser_test() {
        userService.deleteUser(USER);
        verify(userRepository).delete(USER);
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
