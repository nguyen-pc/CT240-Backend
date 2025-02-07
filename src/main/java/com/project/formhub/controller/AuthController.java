package com.project.formhub.controller;

import org.springframework.web.bind.annotation.RestController;

import com.project.formhub.domain.User;
import com.project.formhub.domain.request.ReqLoginDTO;
import com.project.formhub.domain.response.ResCreateUserDTO;
import com.project.formhub.domain.response.RestLoginDTO;
import com.project.formhub.service.UserService;
import com.project.formhub.util.SecurityUtil;
import com.project.formhub.util.error.IdInvalidException;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
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

@RestController
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${formhub.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<RestLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        System.out.println(">>>>>" + loginDTO.getUsername() + " " + loginDTO.getPassword());
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());
        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // nạp thông tin (nếu xử lý thành công) vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        RestLoginDTO res = new RestLoginDTO();

        User currentUserDB = this.userService.handleGetUserByUserName(loginDTO.getUsername());

        if (currentUserDB != null) {
            RestLoginDTO.UserLogin userLogin = new RestLoginDTO.UserLogin(currentUserDB.getId(),
                    currentUserDB.getEmail(), currentUserDB.getName());
            res.setUser(userLogin);
        }

        // Create token
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);
        res.setAccessToken(access_token);

        // Create refresh token
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);

        // update user
        this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

        // set cookie
        ResponseCookie resCookie = ResponseCookie.from("refresh_token", refresh_token).httpOnly(true).path("/")
                .maxAge(refreshTokenExpiration).build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookie.toString()).body(res);

    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : " ";
        if (email.equals(" ")) {
            throw new IdInvalidException("Access token khong hop le");
        }

        this.userService.updateUserToken(null, email);

        ResponseCookie resCookie = ResponseCookie.from("refresh_token", "").httpOnly(true).secure(true).path("/")
                .maxAge(0).build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookie.toString()).body(null);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User postManUser) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(postManUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException(
                    "Email" + postManUser.getEmail() + "da ton tai, vui long su dung email khac");
        }
        String hasPassword = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hasPassword);
        User user = this.userService.handleCreateUser(postManUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(user));
    }

    @GetMapping("/auth/account")
    public ResponseEntity<RestLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : " ";

        User currentUserDB = this.userService.handleGetUserByUserName(email);
        RestLoginDTO.UserLogin userLogin = new RestLoginDTO.UserLogin();
        RestLoginDTO.UserGetAccount userGetAccount = new RestLoginDTO.UserGetAccount();

        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());

            // set Role
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<RestLoginDTO> getRefreshToken(@CookieValue(name = "refresh_token") String refresh_token)
            throws IdInvalidException {
        // check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        // check user by token and email
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);

        if (currentUser == null) {
            throw new IdInvalidException("Refresh token khong hop le");
        }

        // issue new token/set refresh token as cookies
        RestLoginDTO res = new RestLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUserName(email);

        if (currentUserDB != null) {
            RestLoginDTO.UserLogin userLogin = new RestLoginDTO.UserLogin(currentUserDB.getId(),
                    currentUserDB.getEmail(), currentUserDB.getName());
            res.setUser(userLogin);
        }

        // Create access token
        String access_token = this.securityUtil.createAccessToken(email, res);
        res.setAccessToken(access_token);

        // Create refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

        // update user
        this.userService.updateUserToken(new_refresh_token, email);

        // set cookie
        ResponseCookie resCookie = ResponseCookie.from("refresh_token", new_refresh_token).httpOnly(true).secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration).build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookie.toString()).body(res);
    }

    @GetMapping("/path")
    public String getMethodName() {
        return new String("hello");
    }

}
