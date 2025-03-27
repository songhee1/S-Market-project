package com.flab.s_market.domains.file.dto.response;

public record AttachedFileDTO(
    String originalFileName,
    String encryptedFilePath
) {

}
