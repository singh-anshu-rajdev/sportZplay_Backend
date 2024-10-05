package com.sportZplay.sportZplay.ServiceImpl;

import com.sportZplay.sportZplay.DTO.*;
import com.sportZplay.sportZplay.Exception.CustomValidationException;
import com.sportZplay.sportZplay.Exception.ErrorCode;
import com.sportZplay.sportZplay.Model.File;
import com.sportZplay.sportZplay.Model.User;
import com.sportZplay.sportZplay.Repository.FileRepository;
import com.sportZplay.sportZplay.Repository.UserRepository;
import com.sportZplay.sportZplay.Service.JwtService;
import com.sportZplay.sportZplay.Service.UserService;
import com.sportZplay.sportZplay.Utils.SZP_Constants;
import com.sportZplay.sportZplay.Utils.GeneralFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    /**
     *  Logger instance to log important events and errors in the service.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String LOGGER_ERROR_REGISTRATION = "Error in registering the user - {}";

    /**
     * The fileRepository of type FileRepository
     */
    @Autowired
    FileRepository fileRepository;

    /**
     * The userRepository of type UserRepository
     */
    @Autowired
    UserRepository userRepository;

    /**
     * The generalFunctions of type GeneralFunctions
     */
    @Autowired
    GeneralFunctions generalFunctions;

    /**
     * The authenticationManager of type AuthenticationManager
     */
    @Autowired
    AuthenticationManager authenticationManager;

    /**
     * The jwtService of type JwtService
     */
    @Autowired
    JwtService jwtService;

    /**
     *
     * @param userRegistrationDTO
     * @return
     */
    @Override
    public GeneralResponseDTO registerUser(UserRegistrationDTO userRegistrationDTO) {
        if(null!=userRegistrationDTO.getImageId()){
            File file = fileRepository.findByUniqueIdAndDeletedFlagFalse(userRegistrationDTO.getImageId());
            if(null==file || !file.getFileType().contains(SZP_Constants.IMAGE)){
                throw new CustomValidationException(ErrorCode.ERR_AP_2011);
            }
        }

        GeneralResponseDTO response = new GeneralResponseDTO();
        response.setMessage(SZP_Constants.USER_CREATED_SUCCESSFULLY);
        response.setStatus(SZP_Constants.TRUE);
        DataValidatingRequestDTO dataValidatingEmail = DataValidatingRequestDTO.builder()
                .emailId(userRegistrationDTO.getEmailId()).build();
        DataValidatingRequestDTO dataValidatingNumber = DataValidatingRequestDTO.builder()
                .emailId(userRegistrationDTO.getPhoneNumber()).build();
        if(checkExistingData(dataValidatingEmail).getIsExisting() || checkExistingData(dataValidatingNumber).getIsExisting()){
            throw new CustomValidationException(ErrorCode.ERR_AP_2014);
        }
        try{
            User user = new User();
            user.setName(userRegistrationDTO.getName());
            user.setUserName(userRegistrationDTO.getUserName());
            user.setImageUniqueId(userRegistrationDTO.getImageId());
            user.setPassword(generalFunctions.passwordEncoder(userRegistrationDTO.getPassword()));
            user.setClientId(SZP_Constants.USER_CLIENT_ID);
            user.setRoles(SZP_Constants.ROLE_USER);
            user.setEmailId(userRegistrationDTO.getEmailId());
            user.setPhoneNumber(userRegistrationDTO.getPhoneNumber());
            user.setIsEmailVerified(SZP_Constants.FALSE);
            user.setIsPhoneVerified(SZP_Constants.FALSE);
            user.setCreatedBy(SZP_Constants.DEFAULT_USER);
            user.setCreatedTs(LocalDateTime.now());
            user.setDeletedFlag(SZP_Constants.FALSE);
            user.setUpdatedBy(SZP_Constants.ROLE_USER);
            user.setUpdatedTs(LocalDateTime.now());
            userRepository.save(user);
        } catch (Exception ex) {
            LOGGER.error(LOGGER_ERROR_REGISTRATION,ex.getMessage());
            response.setMessage(SZP_Constants.USER_REGISTRATION_FAILED);
            response.setStatus(SZP_Constants.FALSE);
        }
        return response;
    }

    /**
     *
     * @param loginRequestDTO
     * @return
     */
    @Override
    public TokenResponseDTO generateAuthToken(LoginRequestDTO loginRequestDTO) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                    (loginRequestDTO.getUserId(),loginRequestDTO.getPassword()));

            return TokenResponseDTO.builder()
                    .accessToken(jwtService.generateToken(loginRequestDTO.getUserId()))
                    .refreshToken(jwtService.generateRefreshToken(loginRequestDTO.getUserId()))
                    .userName(loginRequestDTO.getUserId())
                    .build();
        }catch (Exception ex){
            throw new CustomValidationException(ErrorCode.ERR_AP_2012);
        }
    }

    /**
     *
     * @param dataValidatingRequestDTO
     * @return
     */
    @Override
    public DataValidatingResponseDTO checkExistingData(DataValidatingRequestDTO dataValidatingRequestDTO) {

        DataValidatingResponseDTO response = new DataValidatingResponseDTO();
        // checking the request Body
        if (null == dataValidatingRequestDTO ||
                (null == dataValidatingRequestDTO.getEmailId()
                        && null == dataValidatingRequestDTO.getPhoneNumber())) {
            throw new CustomValidationException(ErrorCode.ERR_AP_2006);
        }

        User user = null;
        // validating the data
        if (null != dataValidatingRequestDTO.getEmailId()) {
            response.setUniqueValue(dataValidatingRequestDTO.getEmailId());
            user = userRepository.getUserByEmailIdAndDeletedFlagFalse(dataValidatingRequestDTO.getEmailId());
        } else {
            response.setUniqueValue(dataValidatingRequestDTO.getPhoneNumber());
            user = userRepository.getUserByPhoneNumberAndDeletedFlagFalse(dataValidatingRequestDTO.getPhoneNumber());
        }

        // check for existence
        if (null == user) {
            response.setIsExisting(SZP_Constants.FALSE);
        } else {
            response.setIsExisting(SZP_Constants.TRUE);
        }
        return  response;
    }

    /**
     *
     * @param refreshTokenRequestDTO
     * @return
     */
    @Override
    public TokenResponseDTO generateTokenFromRefreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        if(Boolean.TRUE.equals(jwtService.validateToken
                (refreshTokenRequestDTO.getRefreshToken(), refreshTokenRequestDTO.getUserName()))){
            return TokenResponseDTO.builder()
                    .accessToken(jwtService.generateToken(refreshTokenRequestDTO.getUserName()))
                    .refreshToken(jwtService.generateRefreshToken(refreshTokenRequestDTO.getUserName()))
                    .userName(refreshTokenRequestDTO.getUserName())
                    .build();
        }else{
            throw new CustomValidationException(ErrorCode.ERR_AP_2006);
        }
    }
}
