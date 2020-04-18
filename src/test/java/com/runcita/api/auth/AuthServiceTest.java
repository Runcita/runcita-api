package com.runcita.api.auth;

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    AuthService authService;

    @MockBean
    AuthRepository authRepository;

    private Auth auth;

    @BeforeEach
    void initBeforeTest() {
        auth = Auth.builder()
                .id(111L)
                .email("user@gmail.com")
                .password("12345678")
                .user(User.builder()
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
    void getAuthByEmail_test() throws AuthNotFoundException {
        when(authRepository.findByEmail(auth.getEmail())).thenReturn(Optional.of(auth));
        assertEquals(auth, authService.getAuthByEmail(auth.getEmail()));
    }

    @Test
    void getAuthByEmail_not_found_test() {
        when(authRepository.findByEmail(auth.getEmail())).thenReturn(Optional.empty());
        Assertions.assertThrows(AuthNotFoundException.class, () -> authService.getAuthByEmail(auth.getEmail()));
    }

    @Test
    void saveAuth_test() {
        when(authRepository.save(auth)).thenReturn(auth);
        assertEquals(auth, authService.saveAuth(auth));
        verify(authRepository).save(auth);
    }

    @Test
    void emailExists_with_true_value_test() {
        when(authRepository.existsByEmail(auth.getEmail())).thenReturn(true);
        assertTrue(authService.emailExists(auth.getEmail()));
    }

    @Test
    void emailExists_with_false_value_test() {
        when(authRepository.existsByEmail(auth.getEmail())).thenReturn(false);
        assertFalse(authService.emailExists(auth.getEmail()));
    }
}
