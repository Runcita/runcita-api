package com.runcita.api.config.security;

import com.runcita.api.shared.models.User;
import com.runcita.api.user.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;


/**
 * User detail service
 * (recover user informations)
 */
@Component
public class AppUserDetailService implements UserDetailsService {

  private final UserService userService;

  public AppUserDetailService(UserService userService) {
    this.userService = userService;
  }

  /**
   * Recover user by username
   * Attention: for our case, username = email
   * @param email
   * @return user details
   */
  @Override
  public final UserDetails loadUserByUsername(String email)
    throws UsernameNotFoundException {
    final User user = this.userService.getUserByEmail(email);
    if (user == null) {
      throw new UsernameNotFoundException("User with email '" + email + "' not found");
    }

    return org.springframework.security.core.userdetails.User.withUsername(email)
      .password(user.getPassword()).authorities(Collections.emptyList())
      .accountExpired(false).accountLocked(false).credentialsExpired(false)
      .disabled(false).build();
  }

}
