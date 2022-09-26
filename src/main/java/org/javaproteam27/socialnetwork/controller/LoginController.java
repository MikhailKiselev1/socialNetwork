package org.javaproteam27.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.aop.InfoLogger;
import org.javaproteam27.socialnetwork.model.dto.request.LoginRq;
import org.javaproteam27.socialnetwork.model.dto.response.CaptchaRs;
import org.javaproteam27.socialnetwork.model.dto.response.PersonRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.service.CaptchaService;
import org.javaproteam27.socialnetwork.service.LoginService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/")
@InfoLogger
public class LoginController {

    private final LoginService loginService;
    private final CaptchaService captchaService;

    @PostMapping("login")
    public ResponseRs<PersonRs> login(@RequestBody LoginRq loginRq) {
        return loginService.login(loginRq);
    }

    @PostMapping("logout")
    public ResponseRs<Object> logout() {
        return loginService.logout();
    }

    @GetMapping("captcha")
    public CaptchaRs captcha() throws IOException {
        return captchaService.getCaptcha();
    }
}
