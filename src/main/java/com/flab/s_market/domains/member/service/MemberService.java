package com.flab.s_market.domains.member.service;

import com.flab.s_market.common.config.EncryptionService;
import com.flab.s_market.common.exception.CustomException;
import com.flab.s_market.common.exception.ErrorCode;
import com.flab.s_market.domains.member.domain.Member;
import com.flab.s_market.domains.member.domain.MemberSubTermId;
import com.flab.s_market.domains.member.dto.request.AgreedTermDTO;
import com.flab.s_market.domains.member.dto.request.JoinInfoDTO;
import com.flab.s_market.domains.member.dto.request.LoginRequestDTO;
import com.flab.s_market.domains.member.repository.MemberRepository;
import com.flab.s_market.domains.member.repository.MemberSubTermRepository;
import com.flab.s_market.domains.security.dto.request.JwtTokenLogoutRequestDTO;
import com.flab.s_market.domains.security.dto.request.JwtTokenReissueRequestDTO;
import com.flab.s_market.domains.security.dto.response.JwtTokenResponseDTO;
import com.flab.s_market.domains.security.service.JwtTokenProvider;
import com.flab.s_market.domains.term.domain.SubTerm;
import com.flab.s_market.domains.term.domain.Term;
import com.flab.s_market.domains.term.repository.TermRepository;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final MemberRepository userRepository;
    private final TermRepository termRepository;
    private final MemberSubTermRepository memberSubTermRepository;
    private final EmailService emailService;
    private final EncryptionService encryptionService;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate redisTemplate;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30*60*1000L; // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7*24*60*60*1000L; // 7일
    private static final String TOKEN_GRANT_TYPE = "Bearer";

    public void checkEmailDuplicated(String email) {
        if(userRepository.existsByEmail(email)){
            throw new CustomException(ErrorCode.EXIST_EMAIL, Map.of("email", email), log::info);
        }
    }

    public void join(JoinInfoDTO dto) {
        String rawPassword = dto.password();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        String confirmPassword = dto.confirmPassword();
        String emailKey = dto.emailKey();
        String email = encryptionService.decrypt(emailKey);

        if(userRepository.existsByEmail(email)){
            throw new CustomException(ErrorCode.EXIST_USER, Map.of("email", email), log::info);
        }

        if(!emailService.existEmailData("VE:"+email)){
            throw new CustomException(ErrorCode.DECRYPTION_FAILED, Map.of("emailKey", emailKey), log::info);
        }
        emailService.deleteEmailData("VE:"+email);

        if(!ObjectUtils.equals(rawPassword, confirmPassword)){ // NULL?
            throw new CustomException(ErrorCode.NOT_VALID_PASSWORD,
                Map.of("password", rawPassword, "confirmPassword", confirmPassword), log::info);
        }

        List<AgreedTermDTO> agreedTermsDTO = dto.agreedTerms();
        Map<String, Integer> agreedTermsMap = agreedTermsDTO.stream().collect(
            Collectors.toMap(AgreedTermDTO::title,AgreedTermDTO::version));
        List<SubTerm> allTermsInDB = termRepository.findByTermIdAndVersionWithJoin();

        Member savedMember = userRepository.save(dto.toUserEntity(email, encPassword));

        validateRequiredTerms(agreedTermsMap, allTermsInDB, agreedTermsDTO);
        allTermsInDB.forEach(subTerm -> processSubTerm(savedMember, dto, agreedTermsMap, subTerm));

    }

    private void validateRequiredTerms(Map<String, Integer> agreedTermsMap, List<SubTerm> allTermsInDB,
        List<AgreedTermDTO> agreedTermsDTO
        ) {
        for (SubTerm subTerm : allTermsInDB) {
            Term term = subTerm.getTerm();
            if(term.getIsRequired() && !agreedTermsMap.containsKey(term.getTitle())){
                throw new CustomException(ErrorCode.NOT_ALL_AGREED_REQUIRED_TERMS,
                    Map.of("agreedTermsDTO", agreedTermsDTO, "allTermsInDB", allTermsInDB), log::info);
            }
        }
    }

    private void processSubTerm(Member savedMember, JoinInfoDTO dto, Map<String, Integer> agreedTermsMap, SubTerm subTerm) {
        Term term = subTerm.getTerm();
        Integer termVersion = agreedTermsMap.get(term.getTitle());

        if(!agreedTermsMap.containsKey(term.getTitle())){
            saveUserSubTerm(savedMember, dto, false, subTerm);
        }else{
            if(!ObjectUtils.equals(subTerm.getId().getVersion(), termVersion)){
                throw new CustomException(ErrorCode.NOT_MATCH_TERM_VERSION,
                    Map.of("agreedSubTerm", subTerm, "version", agreedTermsMap.get(term.getTitle())), log::info);
            }

            saveUserSubTerm(savedMember, dto, true, subTerm);
        }
    }

    private void saveUserSubTerm(Member member, JoinInfoDTO dto, boolean agree, SubTerm subTerm) {
        MemberSubTermId memberSubTermId = MemberSubTermId.builder()
            .memberId(member.getId())
            .subTerm(subTerm)
            .build();
        memberSubTermRepository.save(dto.toUserSubTermEntity(memberSubTermId, agree, member));
    }

    public JwtTokenResponseDTO login(LoginRequestDTO dto){
        String email = dto.email();
        String password = dto.password();

        // 1. email + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken
            = new UsernamePasswordAuthenticationToken(email, password);

        // 2. 실제 사용자 검증(사용자 비밀번호 체크)가 이뤄지는 부분
        // authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        // authenticated값이 true
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. accessToken이 발급목록에 있는지 확인
        if(Boolean.TRUE.equals(redisTemplate.hasKey("AT:" + email))){
            throw new CustomException(ErrorCode.ALREADY_LOGIN, Map.of("dto", dto), log::info);
        }

        // 4. 인증 정보를 기반으로 JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        JwtTokenResponseDTO jwtToken = new JwtTokenResponseDTO(TOKEN_GRANT_TYPE, accessToken, refreshToken, ACCESS_TOKEN_EXPIRE_TIME, REFRESH_TOKEN_EXPIRE_TIME);

        // 5. RefreshToken Redis 저장 (expirationTime 설정을 통해 자동 삭제 처리) + accesstoken이 발급한 목록이 있는지 확인
        redisTemplate.opsForValue()
                .set("AT:" + email, jwtToken.accessToken(), jwtToken.accessTokenExpirationTime(), TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue()
            .set("RT:" + email, jwtToken.refreshToken(), jwtToken.refreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return jwtToken;
    }


    public JwtTokenResponseDTO reissueToken(JwtTokenReissueRequestDTO dto) {

        // 1. Refresh Token 검증
        jwtTokenProvider.validateToken(dto.refreshToken());

        // 2. Refresh Token 에서 User email 를 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(dto.accessToken());

        // 3. Redis 에서 User email 을 기반으로 저장된 Refresh Token 값을 가져옵니다.
        String refreshToken = "";
        if(redisTemplate.hasKey("RT:" + authentication.getName())){
            refreshToken = (String)redisTemplate.opsForValue().get("RT:" + authentication.getName());
            if(!refreshToken.equals(dto.refreshToken())) {
                throw new CustomException(ErrorCode.NOT_EQUAL_REFRESH_TOKEN, Map.of("refreshToken", refreshToken, "dto.refreshToken", dto.refreshToken()), log::info);
            }
        }else{
            throw new CustomException(ErrorCode.EXPIRED_TOKEN, Map.of("refreshToken", refreshToken), log::info);
        }

        // 4. 새로운 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        JwtTokenResponseDTO jwtToken = new JwtTokenResponseDTO(
            TOKEN_GRANT_TYPE, accessToken, refreshToken, ACCESS_TOKEN_EXPIRE_TIME, redisTemplate.getExpire("RT:"+authentication.getName())
        );

        // 5. RefreshToken Redis 업데이트
        redisTemplate.opsForValue()
            .set("AT:" + authentication.getName(), jwtToken.accessToken(), jwtToken.accessTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return jwtToken;
    }

    public void logout(JwtTokenLogoutRequestDTO dto) {
        jwtTokenProvider.validateToken(dto.accessToken());

        Authentication authentication = jwtTokenProvider.getAuthentication(dto.accessToken());
        if(redisTemplate.hasKey("AT:"+authentication.getName())){
            redisTemplate.delete("AT:"+authentication.getName());
        }
        if(redisTemplate.hasKey("RT:"+authentication.getName())){
            redisTemplate.delete("RT:"+authentication.getName());
        }
    }
}
