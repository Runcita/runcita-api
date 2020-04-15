package com.runcita.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.runcita.api.Application;
import com.runcita.api.config.security.jwt.TokenProvider;
import com.runcita.api.shared.models.Auth;
import com.runcita.api.shared.models.City;
import com.runcita.api.shared.models.Profile;
import com.runcita.api.shared.models.User;
import com.runcita.api.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;

import static org.mockito.ArgumentMatchers.any;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @MockBean
    UserService userService;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    private final String AUTHENTICATE_PATH = "/auth/authenticate";
    private final String SIGNIN_PATH = "/auth/signin";
    private final String SIGNUP_PATH = "/auth/signup";

    private User USER;
    private Auth AUTH;

    @BeforeEach
    void initBeforeTest() {
        USER = User.builder()
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

        AUTH = Auth.builder()
                .email(USER.getEmail())
                .password(USER.getPassword())
                .build();
    }

    @Test
    void authenticate_test() throws Exception {
        mockMvc.perform(get(AUTHENTICATE_PATH))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void signin_test() throws Exception {
        Mockito.when(authenticationManager.authenticate(any())).thenReturn(null);
        Mockito.when(tokenProvider.createToken(USER.getEmail())).thenReturn("Token");

        mockMvc.perform(post(SIGNIN_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(AUTH)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Token")));
    }

    @Test
    void signin_with_bad_credentials_test() throws Exception {
        Mockito.when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("no"));

        mockMvc.perform(post(SIGNIN_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(AUTH)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void signup_test() throws Exception {
        Mockito.when(userService.emailExists(SIGNUP_PATH)).thenReturn(false);
        Mockito.when(tokenProvider.createToken(USER.getEmail())).thenReturn("Token");

        mockMvc.perform(post(SIGNUP_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(USER)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Token")));
    }

    @Test
    void signup_with_email_already_exist_test() throws Exception {
        Mockito.when(userService.emailExists(USER.getEmail())).thenReturn(true);

        mockMvc.perform(post(SIGNUP_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(USER)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email already exist")));
    }
}


