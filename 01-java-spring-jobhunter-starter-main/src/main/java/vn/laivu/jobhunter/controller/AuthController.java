package vn.laivu.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.laivu.jobhunter.domain.response.user.ResUpdateDTO;
import vn.laivu.jobhunter.domain.response.user.RestCreateUserDTO;
import vn.laivu.jobhunter.unity.User;
import vn.laivu.jobhunter.domain.request.ReqLoginDTO;
import vn.laivu.jobhunter.domain.response.user.RestLoginDTO;
import vn.laivu.jobhunter.service.UserService;
import vn.laivu.jobhunter.util.Annotation.ApiMessage;
import vn.laivu.jobhunter.util.SecurityUtil;
import vn.laivu.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil,
            UserService userService,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/login")
    @ApiMessage("Login success")
    public ResponseEntity<RestLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) { // add @Valid to validate <add dependencies/>
        // Nap input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Set thông tin người dùng đăng nhập vào context (Có thể sử dụng sau này)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        RestLoginDTO resLoginDTO = new RestLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUserName(loginDTO.getUsername());

        if (currentUserDB != null) {
            RestLoginDTO.UserLogin userLogin = new RestLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName()
//                    currentUserDB.getRole()
            );
            resLoginDTO.setUser(userLogin);
        }

        // ********************************************************
        // Có thể tách logic này riêng để tái sử dụng
        // create a token (authentication.getName() => lấy ra email từ authentication)
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), resLoginDTO);
        resLoginDTO.setAccessToken(accessToken);

        // create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);
        // ********************************************************

        // Update User Token
        this.userService.updateUserToken(refreshToken, loginDTO.getUsername());

        // Set Cookies
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", refreshToken)
                // Cho phép cookie chỉ cho server sử dụng
                .httpOnly(true)
                .secure(true)
                // Cookie sử dụng được trong tắt cả dự án chứ không phải /api/${api.version}
                .path("/")
                // Cookie sau bao lâu thì hết hạn, hết hạn tự xoá khỏi cookie browser
                .maxAge(refreshTokenExpiration)
                // Khi nào gửi cookie này? Nếu không định nghĩa thì web nào cũng gửi cookie
                // .domain("example.com")
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(resLoginDTO);
    }

    //    Get account nguoi dung
    @GetMapping("/auth/account") //Lay thong tin cua ng dang nhap
    public ResponseEntity<RestLoginDTO.UserLogin> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        User currentUserDB = this.userService.handleGetUserByUserName(email);
        RestLoginDTO.UserLogin userLogin = new RestLoginDTO.UserLogin();

        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
        }
        return ResponseEntity.ok().body(userLogin);
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<RestLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "toang") String refresh_token)
            throws Exception {
        if (refresh_token.equals("toang")) {
            throw new IdInvalidException("Ban khong co refresh token o cookie");
        }

//        Check valid
        Jwt decoded_token = this.securityUtil.checkValueRefreshToken(refresh_token);

        //Khai bao doi tuong email
        String email = decoded_token.getSubject();

        //check user by token + email
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new IdInvalidException("Token khong hop le");
        }

        // ... gán thông tin vào đối tượng RestLoginDTO res ...
        RestLoginDTO res = new RestLoginDTO();
        User currentUserDB = userService.handleGetUserByUserName(email);
        if (currentUserDB != null) {
            RestLoginDTO.UserLogin userLogin = new RestLoginDTO.UserLogin();
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
            res.setUser(userLogin);
        }

        //Create access token
        String access_token = this.securityUtil.createAccessToken(email, res);
        res.setAccessToken(access_token);

//        Create refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

        //Update user
        this.userService.updateUserToken(new_refresh_token, email);

//        set cookie
        ResponseCookie resCookie = ResponseCookie.from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookie.toString()).body(res);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        if (email.equals("")) {
            throw new IdInvalidException("Access token khong hop le");
        }

        //Update refresh token = null
        this.userService.updateUserToken(email, null);

        //remove refresh token = null
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).build();
    }

    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<User> register(@Valid @RequestBody User reqUser) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(reqUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email " + reqUser.getEmail() + " đã tồn tại, vui lòng sử dụng email khác");
        }
        String hashPassword = this.passwordEncoder.encode(reqUser.getPassword());
        reqUser.setPassword(hashPassword);
        User user = this.userService.handleCreateUser(reqUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.handleCreateUser(user));
    }
}
