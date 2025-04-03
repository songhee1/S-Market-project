package com.flab.s_market.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    EXIST_EMAIL("ACCOUNT-01", "이미 사용중인 이메일입니다.", "이미 사용중인 이메일입니다. 다른 이메일을 작성해주세요.", HttpStatus.BAD_REQUEST),
    NOT_VALID_EMAIL_CODE("ACCOUNT-02", "인증코드를 올바르게 작성해주세요.", "인증코드가 틀립니다. 이메일을 확인해주세요.", HttpStatus.NOT_FOUND),
    NOT_VALID_PASSWORD("ACCOUNT-03", "비밀번호를 동일하게 입력해주세요.", "비밀번호와 재확인 비밀번호가 다릅니다. 비밀번호를 다시 확인해주세요.", HttpStatus.BAD_REQUEST),
    NOT_MATCH_TERM_VERSION("ACCOUNT-04", "약관에 오류가 있습니다.", "약관 버전이 잘못 되었습니다. 약관 버전을 다시 확인해주세요.", HttpStatus.NOT_FOUND),
    MAIL_SYSTEM_ERROR("ACCOUNT-05", "이메일을 확인해주세요.", "이메일 시스템 에러가 발생했습니다. 발송 이메일을 다시 확인해주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    ENCRYPTION_FAILED("ACCOUNT-06", "이메일 오류가 있습니다.", "이메일 암호화에 실패하였습니다. 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    DECRYPTION_FAILED("ACCOUNT-07", "이메일을 확인해주세요.", "이메일키 복호화에 실패하였습니다. 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_ALL_AGREED_REQUIRED_TERMS("ACCOUNT-08", "필수 약관에 모두 동의해야 합니다.", "필수 약관에 모두 동의해야합니다.", HttpStatus.BAD_REQUEST),
    NOT_PASSED_FIVE_MINUTES("ACCOUNT-09", "이메일 인증코드를 확인하세요.", "이메일에 인증코드를 발송한 지 5분이 지나지 않았습니다. 이메일을 확인해주세요.", HttpStatus.NOT_FOUND),
    NOT_EXIST_TERM("ACCOUNT-10", "약관에 오류가 있습니다.", "존재하지 않는 약관 이름 또는 약관 버전입니다. 약관 이름 또는 약관 버전을 확인해주세요.", HttpStatus.NOT_FOUND),
    EXIST_USER("ACCOUNT-11", "이미 해당 계정은 존재합니다.", "이미 해당 계정은 회원가입되어있습니다. 이메일을 다시 확인해주세요.", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN("ACCOUNT-12", "토큰에 오류가 있습니다.", "잘못된 토큰을 전송하였습니다. 토큰 정보를 다시 확인해주세요.", HttpStatus.BAD_REQUEST),
    EXPIRED_TOKEN("ACCOUNT-13", "토큰에 오류가 있습니다.", "토큰이 만료되었습니다. 새로운 토큰을 발급받아주세요.", HttpStatus.BAD_REQUEST),
    ALREADY_LOGIN("ACCOUNT-14", "이미 로그인이 되어있습니다.", "이미 로그인이 한 상태입니다. 로그인한 페이지로 돌아가주세요.", HttpStatus.NOT_FOUND),
    NOT_EQUAL_REFRESH_TOKEN("ACCOUNT-15", "로그인에 오류가 있습니다.", "리프레시 토큰 정보가 일치하지 않습니다. 토큰 정보를 다시 확인해주세요", HttpStatus.BAD_REQUEST),
    VERIFY_CODE("ACCOUNT-16", "이메일에 오류가 있습니다.", "이미 인증코드가 전송되었습니다. 인증코드를 작성해주세요.", HttpStatus.NOT_FOUND),
    REQUIRE_PARAMETER("SYSTEM-01", "요청에 오류가 있습니다.", "요청 파라미터 중 필수적인 파라미터는 모두 보내주세요.", HttpStatus.BAD_REQUEST),
    ERROR("SYSTEM-02", "에러가 발생했습니다.", "에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);


    private final String code;
    private final String externalMessage;
    private final String internalMessage;
    private final HttpStatus httpStatus;
}
