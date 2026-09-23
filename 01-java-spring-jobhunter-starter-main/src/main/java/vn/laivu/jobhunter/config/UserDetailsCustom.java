package vn.laivu.jobhunter.config;

import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import vn.laivu.jobhunter.service.ServiceImpl.UserServiceImpl;

@Component("userDetailsService")
public class UserDetailsCustom implements UserDetailsService {

  private final UserServiceImpl userService;

  public UserDetailsCustom(UserServiceImpl userServiceImpl) {
    this.userService = userServiceImpl;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      vn.laivu.jobhunter.unity.User user = this.userService.handleGetUserByUserName(username);
      if(user == null) {
        throw new UsernameNotFoundException("username/password không hợp lệ !");
      }

      return new User(
          user.getEmail(),
          user.getPassword(),
          Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
  }
}
