package org.javaproteam27.socialnetwork.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.request.EmailRq;
import org.javaproteam27.socialnetwork.model.dto.request.PasswordRq;
import org.javaproteam27.socialnetwork.model.dto.request.PersonSettingsRq;
import org.javaproteam27.socialnetwork.model.dto.request.RegisterRq;
import org.javaproteam27.socialnetwork.model.dto.response.*;
import org.javaproteam27.socialnetwork.service.EmailService;
import org.javaproteam27.socialnetwork.service.PasswordService;
import org.javaproteam27.socialnetwork.service.PersonSettingsService;
import org.javaproteam27.socialnetwork.service.RegisterService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Tag(name = "account", description = "Взаимодействие с аккаунтом")
public class AccountController {

    private final RegisterService registerService;
    private final EmailService emailService;
    private final PasswordService passwordService;
    private final PersonSettingsService personSettingsService;

    @PostMapping("/register")
    public RegisterRs register(@RequestBody RegisterRq request) {
        return registerService.postRegister(request);
    }

    @PutMapping("/email/recovery")
    public RegisterRs putRecoveryEmail(@RequestHeader("Authorization") String token) {
        return emailService.putEmail(token);
    }
    @PutMapping("/email")
    public RegisterRs putEmail(@RequestHeader("Authorization") String token, @RequestBody EmailRq rq) {
        return emailService.recoverEmail(token, rq);
    }

    @PutMapping("/password/recovery")
    public RegisterRs putEmailPassword(@RequestHeader("Authorization") String token) {
        return emailService.putPassword(token);
    }

    @PutMapping("/password/set")
    public RegisterRs putPassword(@RequestHeader("Authorization") String token,
                                  @RequestBody PasswordRq rq) {
        return passwordService.putPassword(token, rq.getPassword());
    }

    @GetMapping("/notifications")
    public ListResponseRs<PersonSettingsRs> getPersonSettings() {
        return personSettingsService.getPersonSettings();
    }

    @PutMapping("/notifications")
    public ResponseRs<ComplexRs> editPersonSettings(@RequestBody PersonSettingsRq ps) {
        return personSettingsService.editPersonSettings(ps);
    }

}
