package com.sportZplay.sportZplay.Controller;

import com.sportZplay.sportZplay.DTO.GeneralResponseDTO;
import com.sportZplay.sportZplay.DTO.VerificationDTO;
import com.sportZplay.sportZplay.Service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MailController {

    @Autowired
    MailService mailService;

    @PostMapping("/unsecure/sendOtp")
    public ResponseEntity<GeneralResponseDTO> sendOtpForVerification(@RequestBody VerificationDTO verificationDTO){
        return new ResponseEntity<>(mailService.sendOtpForVerification(verificationDTO), HttpStatus.OK);
    }

    @PostMapping("/unsecure/verifyOtp")
    public ResponseEntity<GeneralResponseDTO> verifyOtp(@RequestBody VerificationDTO verificationDTO){
        return new ResponseEntity<>(mailService.verifyOtp(verificationDTO), HttpStatus.OK);
    }
}
