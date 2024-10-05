package com.sportZplay.sportZplay.ServiceImpl;

import com.sportZplay.sportZplay.Exception.CustomValidationException;
import com.sportZplay.sportZplay.Exception.ErrorCode;
import com.sportZplay.sportZplay.Model.File;
import com.sportZplay.sportZplay.Repository.FileRepository;
import com.sportZplay.sportZplay.Service.FileService;
import com.sportZplay.sportZplay.Utils.SZP_Constants;
import com.sportZplay.sportZplay.Utils.GeneralFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class FileServiceImpl implements FileService {

    /**
     *  Logger instance to log important events and errors in the service.
     */
    public final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * The FileRepository
     */
    @Autowired
    private FileRepository fileRepository;

    /**
     * The generalFunctions of type GeneralFunctions
     */
    @Autowired
    private GeneralFunctions generalFunctions;

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        try{
            File newFile = new File();
            newFile.setFileName(file.getOriginalFilename());
            newFile.setFileType(file.getContentType());
            newFile.setFileData(file.getBytes());
            newFile.setUniqueId(generalFunctions.generateUniqueIdForProfilePictures());
            newFile.setType(SZP_Constants.DEFAULT_FILE_TYPE);
            newFile.setCreatedTs(LocalDateTime.now());
            newFile.setCreatedBy(SZP_Constants.DEFAULT_USER);
            newFile.setUpdatedBy(SZP_Constants.DEFAULT_USER);
            newFile.setUpdatedTs(LocalDateTime.now());
            newFile.setDeletedFlag(false);
            newFile = fileRepository.save(newFile);
            return newFile.getUniqueId();
        }catch (Exception e){
            logger.error("Error in uploading file - {}",e.getMessage());
            throw new CustomValidationException(ErrorCode.ERR_AP_2002);
        }

    }

    /**
     *
     * @param fileId
     * @return
     */
    public ResponseEntity<ByteArrayResource> getFileById(String fileId){
        try{
            File file = fileRepository.findByUniqueIdAndDeletedFlagFalse(fileId);
            if(null==file){
                throw new CustomValidationException(ErrorCode.ERR_AP_2004);
            }else{
                ByteArrayResource resource = new ByteArrayResource(file.getFileData());
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"");
                headers.setContentType(MediaType.parseMediaType(file.getFileType()));
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(file.getFileData().length)
                        .body(resource);
            }
        }catch (Exception e){
            logger.error("Error in downloading the file - {}",e.getMessage());
            if(e.getMessage().equals(ErrorCode.ERR_AP_2004.getMessage())){
                throw new CustomValidationException(ErrorCode.ERR_AP_2004);
            }
            throw new CustomValidationException(ErrorCode.ERR_AP_2003);
        }
    }
}
