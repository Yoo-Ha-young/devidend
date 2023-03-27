package dev.dividendproject.service;

import dev.dividendproject.exception.AlreadyExistUserException;
import dev.dividendproject.model.Auth;
import dev.dividendproject.persist.entity.MemberEntity;
import dev.dividendproject.persist.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 옵셔널에 각 맵핑된 멤버 엔터티를 반환를 하는데 orElseThrow로 옵셔널이 벗겨진 멤버 엔터티를 반환해준다.
        return this.memberRepository.findByUsername(username)
                .orElseThrow( () -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    // 회원 가입에 대한 기능
    public MemberEntity register(Auth.SignUp member){
        boolean exists = this.memberRepository.existsByUsername(member.getUsername());
        if(exists){
            throw new AlreadyExistUserException();
        }
        member.setPassword(this.passwordEncoder.encode(member.getPassword()));
        var result = this.memberRepository.save(member.toEntity());
        return result;
    }

    // JWT(Json Web Token) : 사용자 인증 및 식별에 사용되는 토큰, 토큰은 사용자 정보를 포함
    // 구조 : . 으로 구분되며 앞부분이 Header 중간이 Payload, 마지막이 Signature
    // 로그인할 때 검증을 하기 위한 메서드

    public MemberEntity authenticate(Auth.SignIn member){
        var user = this.memberRepository.findByUsername(member.getUsername())
                .orElseThrow(()->new RuntimeException("존재하지 않는 ID 입니다."));

        // 인코딩되지 않은 패스워드, 인코딩된 패스워드
        if(!this.passwordEncoder.matches(member.getPassword(), user.getPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }
}
