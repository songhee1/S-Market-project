package com.flab.s_market.domains.file.dto.request;

import jakarta.validation.constraints.NotBlank;

public record JwtTokenRequestDTO(
    @NotBlank(message = "accessToken을 입력해주세요")
    String accessToken
) {

}
