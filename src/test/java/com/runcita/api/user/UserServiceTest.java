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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;
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
    private User user2;

    @BeforeEach
    void initBeforeTest() {
        auth = Auth.builder()
                .email("email@gmail.com")
                .build();

        user = User.builder()
            .id(1L)
            .firstName("firstname")
            .lastName("lastname")
            .city(City.builder()
                    .name("city")
                    .code(1)
                    .build())
            .birthday(1586653063000L)
            .sexe(false)
            .build();

        user2 = User.builder()
            .id(2L)
            .firstName("firstname2")
            .lastName("lastname2")
            .city(City.builder()
                    .name("city")
                    .code(1)
                    .build())
            .birthday(1586653063000L)
            .sexe(true)
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

    @Test
    void subscribeUser_test() {
        userService.subscribeUser(user, user2);
        verify(userRepository).subscribeUser(user.getId(), user2.getId());
    }

    @Test
    void subscriptionUserExists_with_subscription_not_exist_test() {
        when(userRepository.subscriptionUserExists(user.getId(), user2.getId())).thenReturn(false);
        assertFalse(userService.subscriptionUserExists(user, user2));
    }

    @Test
    void subscriptionUserExists_with_subscription_exist_test() {
        when(userRepository.subscriptionUserExists(user.getId(), user2.getId())).thenReturn(true);
        assertTrue(userService.subscriptionUserExists(user, user2));
    }

    @Test
    void getSubscriptionsOfUser_test() {
        when(userRepository.findSubscriptionsOfUser(user.getId())).thenReturn(List.of(user, user2));
        assertThat(userService.getSubscriptionsOfUser(user), hasItems(user, user2));
    }

    @Test
    void getSubscriptionsOfUser_with_empty_result_test() {
        when(userRepository.findSubscriptionsOfUser(user.getId())).thenReturn(Collections.emptyList());
        assertTrue(userService.getSubscriptionsOfUser(user).isEmpty());
    }

    @Test
    void getSubscribersOfUser_test() {
        when(userRepository.findSubscribersOfUser(user.getId())).thenReturn(List.of(user, user2));
        assertThat(userService.getSubscribersOfUser(user), hasItems(user, user2));
    }

    @Test
    void getSubscribersOfUser_with_empty_result_test() {
        when(userRepository.findSubscribersOfUser(user.getId())).thenReturn(Collections.emptyList());
        assertTrue(userService.getSubscribersOfUser(user).isEmpty());
    }
}
