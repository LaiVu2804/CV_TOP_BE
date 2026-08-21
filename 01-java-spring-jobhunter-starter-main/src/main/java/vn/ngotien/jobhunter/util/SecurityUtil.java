package vn.ngotien.jobhunter.util;

import com.nimbusds.jose.util.Base64;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import vn.ngotien.jobhunter.domain.response.user.RestLoginDTO;

@Service
public class SecurityUtil {

  private final JwtEncoder jwtEncoder;

  public SecurityUtil(JwtEncoder jwtEncoder) {
    this.jwtEncoder = jwtEncoder;
  }

  public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

  @Value("${hoidanit.jwt.access-token-validity-in-seconds}")
  private long accessTokenExpiration;

  @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
  private long refreshTokenExpiration;

  @Value("${hoidanit.jwt.base64-secret}")
  private String jwtKey;

  public String createAccessToken(String email,
      RestLoginDTO.UserLogin dto) { //thời gian tạo ra token

    Instant now = Instant.now();
    Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

    //hardcode permission (for testing )
    List<String> listAuthority = new ArrayList<String>();

    listAuthority.add("ROLE_USER_CREATE");
    listAuthority.add("ROLE_USER_UPDATE");

    // @formatter:off ( tạo ra phần body )
    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuedAt(now)
        .expiresAt(validity)
        .subject(email) //trả ra email người dùng
        .claim("permission", listAuthority)
        .claim("user", dto)
        .build();

    //Tạo ra phần hearder
    JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
    return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims))
        .getTokenValue();
  }


  public String createRefreshToken(String email, RestLoginDTO dto) { //thời gian tạo ra token
    Instant now = Instant.now();
    Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);

    // @formatter:off ( tạo ra phần body )
    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuedAt(now)
        .expiresAt(validity)
        .subject(email)
        .claim("user", dto.getUser())
        .build();

    //Tạo ra phần hearder
    JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
    return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims))
        .getTokenValue();
  }

  /**
   * Get the JWT of the current user.
   *
   * @return the JWT of the current user.
   */
  public static Optional<String> getCurrentUserLogin() { //Lấy người dùng trực tiếp khi đăng nhập
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
  }

  private static String extractPrincipal(Authentication authentication) {
    if (authentication == null) {
      return null;
    } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
      return springSecurityUser.getUsername();
    } else if (authentication.getPrincipal() instanceof Jwt jwt) {
      String subject = jwt.getSubject();
      if (subject == null || subject.isEmpty()) {
        throw new IllegalStateException("JWT principal has a null or empty subject.");
      }
      return subject;
    } else if (authentication.getPrincipal() instanceof String s) {
      if (s.isEmpty()) {
        throw new IllegalStateException("String principal is empty.");
      }
      return s;
    }
    throw new IllegalStateException("Unsupported principal type: " + authentication.getPrincipal().getClass().getName());
  }


  public static Optional<String> getCurrentUserJWT() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(securityContext.getAuthentication())
        .filter(authentication -> authentication.getCredentials() instanceof String)
        .map(authentication -> (String) authentication.getCredentials());
  }


  private static Stream<String> getAuthorities(Authentication authentication) {
    return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
  }

  private SecretKey getSecretKey() {
    byte[] keyBytes = Base64.from(jwtKey).decode();
    return new SecretKeySpec(keyBytes, 0, keyBytes.length,
        JWT_ALGORITHM.getName());
  }

  public Jwt checkValueRefreshToken(String token){
    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey( //lấy ra phần key
        getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
    try {
       return jwtDecoder.decode(token);// và giải mã token
    } catch (Exception e) {
      System.out.println(">>> Refresh_token error: " + e.getMessage());
      throw e;
    }
  }

}
