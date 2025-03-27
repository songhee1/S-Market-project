package com.flab.s_market.common.config;

import com.flab.s_market.domains.security.domain.Role;
import com.flab.s_market.domains.security.service.JwtAuthenticationFilter;
import com.flab.s_market.domains.security.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    @Bean
    public BCryptPasswordEncoder encode(){
        return new BCryptPasswordEncoder();
    }

    // HttpSecurity 구성을 통해 보안설정
    @Bean
    public SecurityFilterChain filterChan(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            // Rest api 이므로 basic auth 및 csrf 보안을 사용하지 않음
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            // jwt를 사용하므로 세션을 사용하지 않음
            .sessionManagement(sessionManagementConfigurer ->
                sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize ->
                authorize
                // 해당 api에 대해서 모든 사용자 요청을 허가
                .requestMatchers("/v1/api/user/**").permitAll()
                // user 권한을 가진 사용자만 허가
                .requestMatchers("/v1/api/main").permitAll()
                .requestMatchers("/v1/api/file/**").hasRole(Role.PROVIDER.toString())
                // 이 밖의 모든 요청에 대해서 인증을 필요
                .anyRequest().authenticated())
            .addFilterBefore(
                // jwtAuthenticationFilter를 usernamePasswordAuthenticationFilter 앞에 추가해 jwt 인증 처리
                new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class
            ).build();
    }

}
