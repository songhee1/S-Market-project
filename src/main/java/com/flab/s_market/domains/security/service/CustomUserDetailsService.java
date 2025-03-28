package com.flab.s_market.domains.security.service;

import com.flab.s_market.domains.member.domain.Member;
import com.flab.s_market.domains.member.repository.MemberRepository;
import com.flab.s_market.domains.security.domain.CustomUserDetails;
import com.flab.s_market.domains.security.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // username 존재여부 검증
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("이 이메일과 매칭되는 유저가 존재하지 않습니다 : " + username));


        return new CustomUserDetails(member.getId(), member.getEmail(), member.getName(), member.getPassword(), Role.getIncludingRoles(member.getRole().toString()));

    }

}