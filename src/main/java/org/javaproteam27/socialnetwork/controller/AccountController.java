package org.javaproteam27.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.request.PasswordRq;
import org.javaproteam27.socialnetwork.model.dto.request.PersonSettingsRq;
import org.javaproteam27.socialnetwork.model.dto.request.RegisterRq;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.PersonSettingsRs;
import org.javaproteam27.socialnetwork.model.dto.response.RegisterRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.service.EmailService;
import org.javaproteam27.socialnetwork.service.PasswordService;
import org.javaproteam27.socialnetwork.service.PersonSettingsService;
import org.javaproteam27.socialnetwork.service.RegisterService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
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

    @GetMapping("/notifications")
    public ListResponseRs<PersonSettingsRs> getPersonSettings() {
        return personSettingsService.getPersonSettings();
    }

    @PutMapping("/notifications")
    public ResponseRs<Object> editPersonSettings(@RequestBody PersonSettingsRq ps) {
        return personSettingsService.editPersonSettings(ps);
    }

}
