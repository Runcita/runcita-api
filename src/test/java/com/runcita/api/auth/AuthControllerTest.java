package com.runcita.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.runcita.api.Application;
import com.runcita.api.config.security.jwt.TokenProvider;
import com.runcita.api.shared.models.*;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
class AuthControllerTest {

    @MockBean
    AuthService authService;

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
    private final String ME_PATH = "/auth/me";
    private final String SIGNUP_PATH = "/auth/signup";
    private final String UPDATE_PASSWORD_PATH = "/auth/update-password";
    private final String UPDATE_EMAIL_PATH = "/auth/update-email";

    private Auth auth;
    private Signin signin;

    private NewPassword newPassword;
    private NewEmail newEmail;

    @BeforeEach
    void initBeforeTest() {
        auth = Auth.builder()
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

        signin = Signin.builder()
                .email(auth.getEmail())
                .password(auth.getPassword())
                .build();

        newPassword = NewPassword.builder()
                .oldPassword(auth.getPassword())
                .newPassword("password-update")
                .build();

        newEmail = NewEmail.builder()
                .password(auth.getPassword())
                .newEmail("email-upadate@gmail.com")
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
        Mockito.when(tokenProvider.createToken(auth.getEmail())).thenReturn("Token");

        mockMvc.perform(post(SIGNIN_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(signin)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Token")));
    }

    @Test
    void signin_with_bad_credentials_test() throws Exception {
        Mockito.when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("no"));

        mockMvc.perform(post(SIGNIN_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(signin)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void recoverUserAuthentificated_test() throws Exception {
        Mockito.when(tokenProvider.getUsername(any())).thenReturn(auth.getEmail());
        Mockito.when(authService.getAuthByEmail(auth.getEmail())).thenReturn(auth);

        mockMvc.perform(get(ME_PATH))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(auth.getUser().getId()));
    }

    @Test
    void recoverUserAuthentificated_with_user_not_found_test() throws Exception {
        Mockito.when(tokenProvider.getUsername(any())).thenReturn(auth.getEmail());
        Mockito.when(authService.getAuthByEmail(auth.getEmail())).thenThrow(new AuthNotFoundException(auth.getEmail()));

        mockMvc.perform(get(ME_PATH))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User with email {"+ auth.getEmail()+"} is not found")));
    }

    @Test
    void signup_test() throws Exception {
        Mockito.when(authService.emailExists(SIGNUP_PATH)).thenReturn(false);
        Mockito.when(tokenProvider.createToken(auth.getEmail())).thenReturn("Token");

        mockMvc.perform(post(SIGNUP_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(auth)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Token")));
    }

    @Test
    void signup_with_email_already_exist_test() throws Exception {
        Mockito.when(authService.emailExists(auth.getEmail())).thenReturn(true);

        mockMvc.perform(post(SIGNUP_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(auth)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email already exist")));
    }

    @Test
    void updatePassword_test() throws Exception {
        Mockito.when(authService.getAuthByEmail(any())).thenReturn(auth);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(null);
        Mockito.when(passwordEncoder.encode(newPassword.getNewPassword())).thenReturn("password-encoder");

        mockMvc.perform(put(UPDATE_PASSWORD_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(newPassword)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(authService).saveAuth(auth);
        assertEquals("password-encoder", auth.getPassword());
    }

    @Test
    void updatePassword_with_user_not_found_test() throws Exception {
        Mockito.when(tokenProvider.getUsername(any())).thenReturn(auth.getEmail());
        Mockito.when(authService.getAuthByEmail(auth.getEmail())).thenThrow(new AuthNotFoundException(auth.getEmail()));

        mockMvc.perform(put(UPDATE_PASSWORD_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(newPassword)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User with email {"+ auth.getEmail()+"} is not found")));

        verify(authService, times(0)).saveAuth(auth);
    }

    @Test
    void updatePassword_with_old_password_incorrect_test() throws Exception {
        Mockito.when(tokenProvider.getUsername(any())).thenReturn(auth.getEmail());
        Mockito.when(authService.getAuthByEmail(auth.getEmail())).thenReturn(auth);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenThrow(new BadCredentialsException("no"));

        mockMvc.perform(put(UPDATE_PASSWORD_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(newPassword)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Password is incorrect")));

        verify(authService, times(0)).saveAuth(auth);
    }

    @Test
    public void updateEmail_test() throws Exception {
        Mockito.when(tokenProvider.getUsername(any())).thenReturn(auth.getEmail());
        Mockito.when(authService.getAuthByEmail(auth.getEmail())).thenReturn(auth);
        Mockito.when(authService.emailExists(newEmail.getNewEmail())).thenReturn(false);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(null);

        mockMvc.perform(put(UPDATE_EMAIL_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(newEmail)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(authService).saveAuth(auth);
        assertEquals(newEmail.getNewEmail(), auth.getEmail());
    }

    @Test
    public void updateEmail_with_user_not_found_test() throws Exception {
        Mockito.when(tokenProvider.getUsername(any())).thenReturn(auth.getEmail());
        Mockito.when(authService.getAuthByEmail(auth.getEmail())).thenThrow(new AuthNotFoundException(auth.getEmail()));

        mockMvc.perform(put(UPDATE_EMAIL_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(newEmail)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User with email {"+ auth.getEmail()+"} is not found")));

        verify(authService, times(0)).saveAuth(auth);
    }

    @Test
    public void updateEmail_with_old_password_incorrect_test() throws Exception {
        Mockito.when(tokenProvider.getUsername(any())).thenReturn(auth.getEmail());
        Mockito.when(authService.getAuthByEmail(auth.getEmail())).thenReturn(auth);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenThrow(new BadCredentialsException("no"));

        mockMvc.perform(put(UPDATE_EMAIL_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(newEmail)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Password is incorrect")));

        verify(authService, times(0)).saveAuth(auth);
    }

    @Test
    public void updateEmail_with_email_already_exist_test() throws Exception {
        Mockito.when(tokenProvider.getUsername(any())).thenReturn(auth.getEmail());
        Mockito.when(authService.getAuthByEmail(auth.getEmail())).thenReturn(auth);
        Mockito.when(authService.emailExists(newEmail.getNewEmail())).thenReturn(true);

        mockMvc.perform(put(UPDATE_EMAIL_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(newEmail)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email already exist")));

        verify(authService, times(0)).saveAuth(auth);
    }
}


