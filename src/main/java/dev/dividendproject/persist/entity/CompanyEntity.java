package dev.dividendproject.persist.entity;

import dev.dividendproject.model.Company;
import lombok.*;

import javax.persistence.*;

@Entity(name = "COMPANY")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticker;
    private String name;

    // 회사
    // column type unique example
    // id     long    O       1
    // name   String      Coca-Cola
    // ticker String  O   COKE

    // Company에 있는 ticker와 name을 가져와서 CompanyEntity에 저장
    public CompanyEntity(Company company){
        this.ticker = company.getTicker();
        this.name = company.getName();
    }

}

/*
* DB 설계에 고려할 수 있는 것들
* - 어떤 타입의 데이터가 저장되는지(문자열(어느 정도의 길이), 숫자(숫자의 범위) 등..)
* - 데이터의 규모는 어떻게 되는지(매일 수십만건이 쌓일 수 있고 1천만건일 수 있음)
* - 데이터의 저장 주기는 어떻게 되는지(하루 단위 혹은 몇달 단위로 지워져야 하는 데이터도 있다)
* - 데이터의 읽기와 쓰기의 비율(읽기가 더 많은지 쓰기가 더 많은지 생각해서 데이터 연산을 고려해줘야 한다.)
* - 속도 vs 정확도 (속도, 정확성)
* - READ 연산시 어떤 컬럼을 기준으로 읽어오는 지(인덱스)
* - 키는 어떻게 생성 해줄건지
* - 예상 트래픽은 어느 정도인지
* - 파티션은 어떻게 구성할 건지
* 등등등...
* */

