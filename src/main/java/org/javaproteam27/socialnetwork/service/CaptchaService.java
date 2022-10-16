package org.javaproteam27.socialnetwork.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.response.CaptchaRs;
import org.javaproteam27.socialnetwork.model.entity.Captcha;
import org.javaproteam27.socialnetwork.repository.CaptchaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final CaptchaRepository captchaRepository;

    public CaptchaRs getCaptcha() throws IOException {

        StringBuilder image = new StringBuilder("data:image/png;base64, ");
        CaptchaRs response = new CaptchaRs();
        Cage cage = new GCage();
        String captchaCode = cage.getTokenGenerator().next();
        String secretCode = cage.getTokenGenerator().next();

        OutputStream os = new FileOutputStream("captcha.jpg", false);
        cage.draw(secretCode, os);
        os.flush();
        os.close();

        byte[] captchaByte = Files.readAllBytes(Paths.get("captcha.jpg"));
        Files.delete(Paths.get("captcha.jpg"));
        String encodedCaptcha = DatatypeConverter.printBase64Binary(captchaByte);
        image.append(encodedCaptcha);

        captchaRepository.addCaptcha(System.currentTimeMillis(), captchaCode, secretCode);

        response.setImage(image.toString());
        response.setCode(captchaCode);
        return response;
    }

    @Scheduled(fixedDelayString = "PT1H")
    @Async
    public void deleteCaptcha() {
        captchaRepository.findAll().stream()
                .filter(captcha -> captcha.getTime().isBefore(LocalDateTime.now().minusMinutes(20)))
                .forEach(captchaRepository::deleteCaptcha);
    }
}
