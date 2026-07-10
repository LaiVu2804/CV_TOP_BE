package vn.ngotien.jobhunter.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import vn.ngotien.jobhunter.util.SecurityUtil;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean //quá trình tạo ra phần ecoder
  public JwtEncoder jwtEncoder() {
    return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
  }

  private SecretKey getSecretKey() {
    byte[] keyBytes = Base64.from(jwtKey).decode();
    return new SecretKeySpec(keyBytes, 0, keyBytes.length,
        SecurityUtil.JWT_ALGORITHM.getName());
  }

  @Value("${hoidanit.jwt.base64-secret}")
  private String jwtKey;

  @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
  private long refreshTokenExpiration;

  @Bean
  //JwtAuthenticationConverter  : convert data chứa trong token, lưu  vào Spring Security Context để reuse
  //Bean này chỉ có nv lấy thông tin và nạp ngược vào setAuthoritiesClaimName
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    grantedAuthoritiesConverter.setAuthorityPrefix("");
    grantedAuthoritiesConverter.setAuthoritiesClaimName("permission");

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }


  @Bean //JwtDecoder : giải mã bear token (check tính hợp lệ của bear token)
  //phải ghi đè phần decoder này
  public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey( //lấy ra phần key
        getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
    return token -> {
      try {
        return jwtDecoder.decode(token); // và giải mã token
      } catch (Exception e) {
        System.out.println(">>> JWT error: " + e.getMessage());
        throw e;
      }
    };
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
    http
        .csrf(c -> c.disable())
        .authorizeHttpRequests(
            authz ->
                // prettier-ignore
                authz
                    .requestMatchers("/", "/api/v1/auth/login", "/api/v1/auth/refresh",
                        "/api/v1/auth/logout").permitAll()
                    .anyRequest().authenticated())
//        .exceptionHandling(
//            exceptions -> exceptions
//                .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()) //401
//                .accessDeniedHandler(new BearerTokenAccessDeniedHandler())) //403

        .oauth2ResourceServer((oauth2)
            -> oauth2.jwt(
                Customizer.withDefaults()) //sẽ kích hoạt filter BearerTokenAuthenticationFilter,
            // Filter này sẽ “tự động tách” Bear Token
            .authenticationEntryPoint(customAuthenticationEntryPoint)
        )
        .formLogin(f -> f.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    return http.build();
  }
}
