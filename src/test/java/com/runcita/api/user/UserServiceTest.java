package com.runcita.api.user;

import com.runcita.api.Application;
import com.runcita.api.shared.models.Auth;
import com.runcita.api.shared.models.City;
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

    private Auth auth;
    private User user;

    @BeforeEach
    void initBeforeTest() {
        auth = Auth.builder()
                .email("email@gmail.com")
                .build();

        user = User.builder()
            .firstName("firstname")
            .lastName("lastname")
            .city(City.builder()
                    .name("city")
                    .code(1)
                    .build())
            .birthday(1586653063000L)
            .sexe(false)
            .build();
    }

    @Test
    void getUserById_test() throws UserNotFoundException {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertEquals(user, userService.getUserById(user.getId()));
    }

    @Test
    void getUserById_not_found_test() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getUserById(user.getId()));
    }

    @Test
    void saveUser_test() {
        when(userRepository.save(user)).thenReturn(user);
        assertEquals(user, userService.saveUser(user));
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_test() {
        userService.deleteUser(user);
        verify(userRepository).delete(user);
    }

    @Test
    void getEmailUser_test() {
        when(userRepository.findEmailUser(user.getId())).thenReturn(auth.getEmail());
        assertEquals(auth.getEmail(), userService.getEmailUser(user));
    }
}
