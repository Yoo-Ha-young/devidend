package dev.dividendproject.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    // 컴퍼니 엔터티에서 아이디만 따진 티커와 네임
    // 모델 클래스를 따로 정의해준 것은
    // 엔터티는 데이터베이스와 직접 맵핑되는 클래스라
    // 서비스 클래스로 사용해준다.
    private String ticker;
    private String name;

    /*
    * Coffee americano = new Coffee(1, 150, null, false, 1);
    *  =
    * Coffee americano = Coffee.builder()
    *                       .shots(1).water(250).syrup(1).build();
    * */


    // 왜 이렇게 설계를 했는지, 왜 이렇게 구현을 했는지, 왜 이런 라이브러리를 썼는지
    // 회원 API
    // 회원가입, 로그인, 로그아웃

    // 서비스 API
    // 배당금 조회, 배당금 검색 - 자동완성 - 회사 리스트 조회
    // 배당금 데이터 저장, 배당금 데이터 삭제
    // TODO 회원 인증
}
/*
[구현 동작]
1. 인풋으로 저장할 회사의 ticker를 받는다.
2. 이미 저장 되어있는 회사의 ticker일 경우 오류 처리
3. 받은 ticker의 데이터를 야후 파이낸스에서 스크래핑한다.
4. 스크래핑 데이터가 조회되지 않는 경우 오류 처리
5. 스크래핑한 회사의 메타정보와 배당금 정보를 각각 DB에 저장한다.
6. 저장한 회사의 메타 정보를 응답으로 내려준다.
**/