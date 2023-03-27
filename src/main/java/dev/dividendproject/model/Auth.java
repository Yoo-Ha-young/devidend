package dev.dividendproject.model;

import dev.dividendproject.persist.entity.MemberEntity;
import lombok.Data;

import java.util.List;

public class Auth {
    // 이너 클래스로 로그인, 회원가입을 위한 클래스

    @Data // 로그인
    public static class SignIn {
        private String username;
        private String password;
    }

    @Data // 회원가입
    public static class SignUp {
        private String username;
        private String password;
        private List<String> roles; // 내부적으로 관리할 수 있는 것

        // SignUp의 클래스를 멤버 엔터티로 받아올 수 있게끔 설정
        public MemberEntity toEntity(){
            return MemberEntity.builder()
                            .username(this.username)
                            .password(this.password)
                            .roles(this.roles)
                            .build();
        }
    }
}
