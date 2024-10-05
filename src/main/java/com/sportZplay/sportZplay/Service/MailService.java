package com.sportZplay.sportZplay.Service;

import com.sportZplay.sportZplay.DTO.GeneralResponseDTO;
import com.sportZplay.sportZplay.DTO.VerificationDTO;
import org.springframework.stereotype.Service;

@Service
public interface MailService {

    /**
     *
     * @param verificationDTO
     * @return
     */
    GeneralResponseDTO sendOtpForVerification(VerificationDTO verificationDTO);

    /**
     *
     * @param verificationDTO
     * @return
     */
    GeneralResponseDTO verifyOtp(VerificationDTO verificationDTO);
}
