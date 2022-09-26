package org.javaproteam27.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.request.RegisterRq;
import org.javaproteam27.socialnetwork.model.dto.response.CaptchaRs;
import org.javaproteam27.socialnetwork.model.dto.response.RegisterRs;
import org.javaproteam27.socialnetwork.service.CaptchaService;
import org.javaproteam27.socialnetwork.service.EmailService;
import org.javaproteam27.socialnetwork.service.RegisterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final RegisterService registerService;
    private final EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<RegisterRs> register(@RequestBody RegisterRq request) {
        RegisterRs registerRs = emailService.putEmail("daeds");
        return registerService.postRegister(request);
    }

    @PutMapping("/email")
    public RegisterRs putEmail(@RequestParam(value = "email") String email) {
        System.out.println("сработал контроллер");
        return emailService.putEmail(email);
    }

}
