package com.flab.s_market.domains.file.controller;

import com.flab.s_market.common.entity.ApiResponse;
import com.flab.s_market.domains.file.dto.request.JwtTokenRequestDTO;
import com.flab.s_market.domains.file.dto.response.AttachedFileDTO;
import com.flab.s_market.domains.file.service.FileInfoService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/file")
@Validated
public class FileInfoController {
    private final FileInfoService fileInfoService;
    @PostMapping("/upload")
    public ApiResponse<List<AttachedFileDTO>> fileAttached(
        @RequestBody @Valid JwtTokenRequestDTO loginDTO,
        @RequestParam MultipartFile[] files) throws IOException {
        List<AttachedFileDTO> encrypedFilePath = fileInfoService.fileAttached(files);
        return ApiResponse.createSuccess(encrypedFilePath);
    }
}
