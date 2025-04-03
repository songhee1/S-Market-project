package com.flab.s_market.domains.file.service;

import com.flab.s_market.common.config.EncryptionService;
import java.io.*;

import com.flab.s_market.domains.file.domain.FileInfo;
import com.flab.s_market.domains.file.dto.response.AttachedFileDTO;
import com.flab.s_market.domains.file.repository.FileInfoRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
@RequiredArgsConstructor
public class FileInfoService {
    private final FileInfoRepository fileRepository;
    private final EncryptionService encryptionService;

    @Value("${songhee.upload.path}")
    private String uploadPath;

    public List<AttachedFileDTO> fileAttached(MultipartFile[] multipartFiles) throws IOException {

        List<AttachedFileDTO> responseDTO = new ArrayList<>();

        for(MultipartFile multipartFile : multipartFiles){

            if(multipartFile.isEmpty()) continue;

            String originalName = multipartFile.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();

            assert originalName != null;

            String attachedName = uuid + "_" + originalName;
            String folderPath = makeFolder();

            String attachedPath = uploadPath + folderPath + attachedName;
            String encrypedPath = encryptFilePath(attachedPath);

            multipartFile.transferTo(new File(attachedPath));
            FileInfo fileEntity = FileInfo.builder()
                .originalFileName(originalName)
                .attachedFileName(attachedName)
                .originalFilePath(attachedPath)
                .encryptedFilePath(encrypedPath)
                .build();

            fileRepository.save(fileEntity);

            responseDTO.add(new AttachedFileDTO(originalName, encrypedPath));
        }

        return responseDTO;
    }

    private String makeFolder(){
        String path = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd/"));
        String folderPath = path.replace("/", java.io.File.separator);

        File uploadPathFolder = new File(uploadPath, folderPath);

        if(!uploadPathFolder.exists()){
            uploadPathFolder.mkdirs();
        }
        return folderPath;
    }

    private String encryptFilePath(String path){
        return encryptionService.encrypt(path);
    }
}
