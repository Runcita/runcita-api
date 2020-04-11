package com.runcita.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.runcita.api.Application;
import com.runcita.api.config.security.jwt.TokenProvider;
import com.runcita.api.shared.models.Auth;
import com.runcita.api.shared.models.User;
import com.runcita.api.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
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
public class AuthControllerTest {

    @MockBean
    UserService userService;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    AuthenticationManager authenticationManager;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    private final String AUTHENTICATE_PATH = "/authenticate";
    private final String SIGNIN_PATH = "/signin";
    private final String SIGNUP_PATH = "/signup";

    private final User USER = User.builder()
            .email("test@gmail.com")
            .password("12345678")
            .firstName("firstname")
            .lastName("lastname")
            .city("city")
            .birthday(LocalDateTime.now())
            .sexe(false)
            .build();

    private final Auth AUTH = Auth.builder()
            .email(USER.getEmail())
            .password(USER.getPassword())
            .build();

    @Test
    public void authenticate_test() throws Exception {
        mockMvc.perform(get(AUTHENTICATE_PATH))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void signin_test() throws Exception {
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(null);

        Mockito.when(tokenProvider.createToken(USER.getEmail())).thenReturn("Token");

        mockMvc.perform(post(SIGNIN_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(AUTH)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Token")));
    }

    @Test
    public void signin_with_bad_credentials_test() throws Exception {
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenThrow(new BadCredentialsException("no"));

        mockMvc.perform(post(SIGNIN_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(AUTH)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void signup_test() throws Exception {
        Mockito.when(userService.emailExists(SIGNUP_PATH)).thenReturn(false);

        Mockito.when(tokenProvider.createToken(USER.getEmail())).thenReturn("Token");

        mockMvc.perform(post(SIGNUP_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Token")));
    }

    @Test
    public void signup_with_email_already_exist_test() throws Exception {
        Mockito.when(userService.emailExists(USER.getEmail())).thenReturn(true);

        mockMvc.perform(post(SIGNUP_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(USER)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email already exist")));
    }
}


