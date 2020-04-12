package com.runcita.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.runcita.api.Application;
import com.runcita.api.config.security.jwt.TokenProvider;
import com.runcita.api.shared.models.Auth;
import com.runcita.api.shared.models.NewPassword;
import com.runcita.api.shared.models.User;
import com.runcita.api.user.UserService;
import org.junit.Before;
import org.junit.Test;
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

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @MockBean
    PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    private final String AUTHENTICATE_PATH = "/authenticate";
    private final String SIGNIN_PATH = "/signin";
    private final String SIGNUP_PATH = "/signup";
    private final String UPDATE_PASSWORD_PATH = "/api/users/{userId}/updatepassword";

    private User USER;
    private Auth AUTH;
    private NewPassword NEW_PASSWORD;

    @Before
    public void initBeforeTest() {
        USER = User.builder()
                .id(111L)
                .email("user@gmail.com")
                .password("12345678")
                .firstName("firstname")
                .lastName("lastname")
                .city("city")
                .birthday(1586653063000L)
                .sexe(false)
                .build();

        AUTH = Auth.builder()
                .email(USER.getEmail())
                .password(USER.getPassword())
                .build();

        NEW_PASSWORD = NewPassword.builder()
                .oldPassword(USER.getPassword())
                .newPassword("910111213")
                .build();
    }

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
                .andExpect(status().isCreated())
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

    @Test
    public void updatePassword_test() throws Exception {
        Mockito.when(userService.getUserById(USER.getId())).thenReturn(Optional.of(USER));
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(null);
        Mockito.when(passwordEncoder.encode(NEW_PASSWORD.getNewPassword())).thenReturn("password-encoder");

        mockMvc.perform(put(UPDATE_PASSWORD_PATH, USER.getId())
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(NEW_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).save(USER);
        assertEquals("password-encoder", USER.getPassword());
    }

    @Test
    public void updatePassword_with_user_not_found_test() throws Exception {
        Mockito.when(userService.getUserById(USER.getId())).thenReturn(Optional.empty());

        mockMvc.perform(put(UPDATE_PASSWORD_PATH, USER.getId())
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(NEW_PASSWORD)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User with id {"+USER.getId()+"} is not found")));

        verify(userService, times(0)).save(USER);
    }

    @Test
    public void updatePassword_with_old_password_incorrect_test() throws Exception {
        Mockito.when(userService.getUserById(USER.getId())).thenReturn(Optional.of(USER));
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenThrow(new BadCredentialsException("no"));

        mockMvc.perform(put(UPDATE_PASSWORD_PATH, USER.getId())
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(NEW_PASSWORD)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Password is incorrect")));

        verify(userService, times(0)).save(USER);
    }
}


