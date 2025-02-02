package com.runcita.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.runcita.api.Application;
import com.runcita.api.config.security.jwt.TokenProvider;
import com.runcita.api.shared.models.Auth;
import com.runcita.api.shared.models.City;
import com.runcita.api.shared.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
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
class UserControllerTest {

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

    private final String DELETE_USER_PATH = "/api/users/{userId}";
    private final String RECOVER_USER_PATH = "/api/users/{userId}";
    private final String UPDATE_USER_PATH = "/api/users/{userId}";
    private final String FOLLOW_USER_PATH = "/api/users//{userId}/subscriptions/{otherUserId}";
    private final String RECOVER_SUBSCRIPTIONS_USER_PATH = "/api/users/{userId}/subscriptions";
    private final String RECOVER_SUBSCRBIERS_USER_PATH = "/api/users/{userId}/subscribers";

    private Auth auth;
    private User user;
    private User user2;

    @BeforeEach
    void initBeforeTest() {
        auth = Auth.builder()
                .email("email@gmail.com")
                .build();

        user = User.builder()
                .id(12L)
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
                .id(22L)
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
    public void deleteUser_test() throws Exception {
        Mockito.when(userService.getUserById(user.getId())).thenReturn(user);
        Mockito.when(tokenProvider.getUsername(any())).thenReturn(auth.getEmail());
        Mockito.when(userService.getEmailUser(user)).thenReturn(auth.getEmail());

        mockMvc.perform(delete(DELETE_USER_PATH, user.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).deleteUser(user);
    }

    @Test
    public void deleteUser_with_user_not_found_test() throws Exception {
        Mockito.when(userService.getUserById(user.getId())).thenThrow(new UserNotFoundException(user.getId()));

        mockMvc.perform(delete(DELETE_USER_PATH, user.getId()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User with id {"+ user.getId()+"} is not found")));

        verify(userService, times(0)).deleteUser(user);
    }

    @Test
    public void deleteUser_with_user_not_authorize_test() throws Exception {
        Mockito.when(userService.getUserById(user.getId())).thenReturn(user);
        Mockito.when(tokenProvider.getUsername(any())).thenReturn("email other user");
        Mockito.when(userService.getEmailUser(user)).thenReturn(auth.getEmail());

        mockMvc.perform(delete(DELETE_USER_PATH, user.getId()))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(userService, times(0)).deleteUser(user);
    }

    @Test
    public void recoverUser_test() throws Exception {
        Mockito.when(userService.getUserById(user.getId())).thenReturn(user);

        mockMvc.perform(get(RECOVER_USER_PATH, user.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist());
    }

    @Test
    public void recoverUser_with_user_not_found_test() throws Exception {
        Mockito.when(userService.getUserById(user.getId())).thenThrow(new UserNotFoundException(user.getId()));

        mockMvc.perform(get(RECOVER_USER_PATH, user.getId()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User with id {"+ user.getId()+"} is not found")));
    }

    @Test
    public void updateUser_test() throws Exception {
        user.setFirstName("firstname-update");

        Mockito.when(userService.getUserById(user.getId())).thenReturn(user);
        Mockito.when(tokenProvider.getUsername(any())).thenReturn(auth.getEmail());
        Mockito.when(userService.getEmailUser(user)).thenReturn(auth.getEmail());

        mockMvc.perform(put(UPDATE_USER_PATH, user.getId())
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()));

        verify(userService).saveUser(user);
    }

    @Test
    public void updateUser_with_user_not_found_test() throws Exception {
        Mockito.when(userService.getUserById(user.getId())).thenThrow(new UserNotFoundException(user.getId()));

        mockMvc.perform(put(UPDATE_USER_PATH, user.getId())
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User with id {"+ user.getId()+"} is not found")));

        verify(userService, times(0)).saveUser(user);
    }

    @Test
    public void updateUser_with_user_not_authorize_test() throws Exception {
        Mockito.when(userService.getUserById(user.getId())).thenReturn(user);
        Mockito.when(tokenProvider.getUsername(any())).thenReturn("email other user");
        Mockito.when(userService.getEmailUser(user)).thenReturn(auth.getEmail());

        mockMvc.perform(put(UPDATE_USER_PATH, user.getId())
                .contentType(APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(userService, times(0)).saveUser(user);
    }

    @Test
    public void followUser_test() throws Exception {
        Mockito.when(userService.getUserById(user.getId())).thenReturn(user);
        Mockito.when(userService.getUserById(user2.getId())).thenReturn(user2);
        Mockito.when(tokenProvider.getUsername(any())).thenReturn(auth.getEmail());
        Mockito.when(userService.getEmailUser(user)).thenReturn(auth.getEmail());
        Mockito.when(userService.subscriptionUserExists(user, user2)).thenReturn(false);

        mockMvc.perform(post(FOLLOW_USER_PATH, user.getId(), user2.getId()))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(userService).subscribeUser(user, user2);
    }

    @Test
    public void followUser_with_user_not_found_test() throws Exception {
        Mockito.when(userService.getUserById(user.getId())).thenThrow(new UserNotFoundException(user.getId()));

        mockMvc.perform(post(FOLLOW_USER_PATH, user.getId(), user2.getId()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User with id {"+ user.getId()+"} is not found")));

        verify(userService, times(0)).subscribeUser(user, user2);
    }

    @Test
    public void followUser_with_other_user_not_found_test() throws Exception {
        Mockito.when(userService.getUserById(user2.getId())).thenThrow(new UserNotFoundException(user2.getId()));

        mockMvc.perform(post(FOLLOW_USER_PATH, user.getId(), user2.getId()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User with id {"+ user2.getId()+"} is not found")));

        verify(userService, times(0)).subscribeUser(user, user2);
    }

    @Test
    public void followUser_with_user_not_authorize_test() throws Exception {
        Mockito.when(userService.getUserById(user.getId())).thenReturn(user);
        Mockito.when(userService.getUserById(user2.getId())).thenReturn(user2);
        Mockito.when(tokenProvider.getUsername(any())).thenReturn("email other user");
        Mockito.when(userService.getEmailUser(user)).thenReturn(auth.getEmail());

        mockMvc.perform(post(FOLLOW_USER_PATH, user.getId(), user2.getId()))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(userService, times(0)).subscribeUser(user, user2);
    }

    @Test
    public void followUser_with_subscription_already_exist_test() throws Exception {
        Mockito.when(userService.getUserById(user.getId())).thenReturn(user);
        Mockito.when(userService.getUserById(user2.getId())).thenReturn(user2);
        Mockito.when(tokenProvider.getUsername(any())).thenReturn(auth.getEmail());
        Mockito.when(userService.getEmailUser(user)).thenReturn(auth.getEmail());
        Mockito.when(userService.subscriptionUserExists(user, user2)).thenReturn(true);

        mockMvc.perform(post(FOLLOW_USER_PATH, user.getId(), user2.getId()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Subscription already exist")));

        verify(userService, times(0)).subscribeUser(user, user2);
    }

    @Test
    public void recoverSubscriptionOfUser_test() throws Exception {
        Mockito.when(userService.getUserById(user.getId())).thenReturn(user);
        Mockito.when(userService.getSubscriptionsOfUser(user)).thenReturn(List.of(user2));

        mockMvc.perform(get(RECOVER_SUBSCRIPTIONS_USER_PATH, user.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(user2.getId()));
    }

    @Test
    public void recoverSubscriptionOfUser_with_user_not_found_test() throws Exception {
        Mockito.when(userService.getUserById(user.getId())).thenThrow(new UserNotFoundException(user.getId()));

        mockMvc.perform(get(RECOVER_SUBSCRIPTIONS_USER_PATH, user.getId()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User with id {"+ user.getId()+"} is not found")));
    }

    @Test
    public void recoverSubscribersOfUser_test() throws Exception {
        Mockito.when(userService.getUserById(user.getId())).thenReturn(user);
        Mockito.when(userService.getSubscribersOfUser(user)).thenReturn(List.of(user2));

        mockMvc.perform(get(RECOVER_SUBSCRBIERS_USER_PATH, user.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(user2.getId()));
    }

    @Test
    public void recoverSubscribersOfUser_with_user_not_found_test() throws Exception {
        Mockito.when(userService.getUserById(user.getId())).thenThrow(new UserNotFoundException(user.getId()));

        mockMvc.perform(get(RECOVER_SUBSCRBIERS_USER_PATH, user.getId()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User with id {"+ user.getId()+"} is not found")));
    }
}


