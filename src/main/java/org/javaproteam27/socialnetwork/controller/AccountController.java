package org.javaproteam27.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.request.PasswordRq;
import org.javaproteam27.socialnetwork.model.dto.request.RegisterRq;
import org.javaproteam27.socialnetwork.model.dto.response.RegisterRs;
import org.javaproteam27.socialnetwork.service.EmailService;
import org.javaproteam27.socialnetwork.service.PasswordService;
import org.javaproteam27.socialnetwork.service.RegisterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final RegisterService registerService;
    private final EmailService emailService;
    private final PasswordService passwordService;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRq request) {
        return registerService.postRegister(request);
    }

    @PutMapping("/email/recovery")
    public RegisterRs putEmail(@RequestHeader("Authorization") String token) {
        return emailService.putEmail(token);
    }

    @PutMapping("/password/recovery")
    public RegisterRs putEmailPassword(@RequestHeader("Authorization") String token) {
        return emailService.putEmail(token);
    }

    @PutMapping("/password/set")
    public RegisterRs putPassword(@RequestHeader("Authorization") String token,
                                  @RequestBody PasswordRq rq) {
        return passwordService.putPassword(token, rq.getPassword());
    }

}
