package com.sportZplay.sportZplay.ServiceImpl;

import com.sportZplay.sportZplay.DTO.GeneralResponseDTO;
import com.sportZplay.sportZplay.DTO.VerificationDTO;
import com.sportZplay.sportZplay.Exception.CustomValidationException;
import com.sportZplay.sportZplay.Exception.ErrorCode;
import com.sportZplay.sportZplay.Model.OtpVerification;
import com.sportZplay.sportZplay.Model.User;
import com.sportZplay.sportZplay.Repository.OtpVerificationRepository;
import com.sportZplay.sportZplay.Repository.UserRepository;
import com.sportZplay.sportZplay.Service.MailService;
import com.sportZplay.sportZplay.Utils.SZP_Constants;
import com.sportZplay.sportZplay.Utils.AsyncService;
import com.sportZplay.sportZplay.Utils.GeneralFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class MailServiceImpl implements MailService {


    /**
     *  Logger instance to log important events and errors in the service.
     */
    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    /**
     * The LOGGER_MESSAGE_MAIL_FAILURE of type String
     */
    private static final String LOGGER_MESSAGE_MAIL_FAILURE = "Error in sending otp verification mail - {}";

    /**
     * Initiate Random Function
     */
    private Random random = new Random();

    /**
     * SENDER's MAIL ADDRESS
     */
    @Value("${spring.mail.username}")
    String fromMail;

    /**
     * THE Java Mail Sender of type JavaMailSender
     */
    @Autowired
    JavaMailSender javaMailSender;

    /**
     * The otpVerificationRepository of type OtpVerificationRepository
     */
    @Autowired
    OtpVerificationRepository otpVerificationRepository;

    /**
     * The userRepository of type UserRepository
     */
    @Autowired
    UserRepository userRepository;

    /**
     * The asyncService of type AsyncService
     */
    @Autowired
    AsyncService asyncService;

    /**
     * The generalFunctions of type GeneralFunctions
     */
    @Autowired
    GeneralFunctions generalFunctions;

    /**
     * Sends an email and logs different stages of the process.
     *
     * @param verificationDTO   recipient's email address
     * @throws Exception if email sending fails
     */
    @Override
    public GeneralResponseDTO sendOtpForVerification(VerificationDTO verificationDTO) {

        // Response Body
        GeneralResponseDTO response = new GeneralResponseDTO();
        response.setStatus(SZP_Constants.TRUE);
        try{
            Integer otp = 100000 + random.nextInt(900000);

            // check weather the email is present or not
            User user = userRepository.getUserByEmailIdAndDeletedFlagFalse(verificationDTO.getMail());
            if(null!=user){
                // saving otp data asynchronously
                asyncService.saveOtpVerification(verificationDTO.getMail(),otp);

                // sending the mail
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(verificationDTO.getMail());

                // setting subject
                mailMessage.setSubject(SZP_Constants.OTP_VERIFICATION_SUBJECT);

                // setting Body
                String message = generalFunctions.buildOtpEmailContent(user.getName(),otp);
                mailMessage.setText(message);
                mailMessage.setFrom(fromMail);
                javaMailSender.send(mailMessage);
                response.setMessage(SZP_Constants.MAIL_SEND_SUCCESS_MESSAGE);
            }else{
                throw new CustomValidationException(ErrorCode.ERR_AP_2007);
            }

        }catch (Exception ex){
            logger.error(LOGGER_MESSAGE_MAIL_FAILURE, ex.getMessage());
            response.setStatus(SZP_Constants.FALSE);
            response.setMessage(SZP_Constants.MAIL_SEND_FAILURE_MESSAGE);
        }

        return response;
    }

    /**
     *
     * @param verificationDTO
     * @return
     */
    @Override
    public GeneralResponseDTO verifyOtp(VerificationDTO verificationDTO) {
        // Response Body
        GeneralResponseDTO response = new GeneralResponseDTO();
        response.setStatus(SZP_Constants.TRUE);

        if (null == verificationDTO.getMail() && null == verificationDTO.getPhoneNumber()) {
            throw new CustomValidationException(ErrorCode.ERR_AP_2006);
        }

        // fetch the otp from db
        List<OtpVerification> otps = validationOtp(verificationDTO);

        //Found the otp
        OtpVerification otpVerification = otps.get(0);

        // Deactiving all the Otp of the user
        if (otpVerification.getOtp().equals(verificationDTO.getOtp())) {

            // welcoming mail only for mailId users
            if(null!=verificationDTO.getMail()){
                asyncService.sendWelcomeMail(verificationDTO.getMail());
            }
            response.setMessage(SZP_Constants.USER_VERIFIED);
            otps.parallelStream().forEach(ot -> {
                ot.setIsActive(SZP_Constants.FALSE);
                ot.setUpdatedBy(SZP_Constants.DEFAULT_USER);
                ot.setUpdatedTs(LocalDateTime.now());
            });
            otpVerificationRepository.saveAll(otps);
        } else {
            // otp does not match
            throw new CustomValidationException(ErrorCode.ERR_AP_2005);
        }
        return response;
    }

    /**
     *
     * @param verificationDTO
     * @return
     */
    public List<OtpVerification> validationOtp(VerificationDTO verificationDTO){
        List<OtpVerification> otps = null;


        if (null != verificationDTO.getMail()) {
            // validation for mail
            User user = userRepository.getUserByEmailIdDeletedFlagFalse(verificationDTO.getMail());
            if(null!=user){
                throw new CustomValidationException(ErrorCode.ERR_AP_2009);
            }
            otps = otpVerificationRepository.findByUniqueTypeAndValidTs(verificationDTO.getMail(), LocalDateTime.now());
            if (null == otps || otps.isEmpty()) {
                throw new CustomValidationException(ErrorCode.ERR_AP_2007); // cannot find otp by mail
            }
        } else {
            // validation for phone number
            User user = userRepository.getUserByPhoneNumberDeletedFlagFalse(verificationDTO.getPhoneNumber());
            if(null!=user){
                throw new CustomValidationException(ErrorCode.ERR_AP_2010);
            }
            otps = otpVerificationRepository.findByUniqueTypeAndValidTs(verificationDTO.getPhoneNumber(), LocalDateTime.now());
            if (null == otps || otps.isEmpty()) {
                throw new CustomValidationException(ErrorCode.ERR_AP_2008); // cannot find otp by Number
            }
        }
        return otps;
    }
}
