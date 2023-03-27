package dev.dividendproject.persist.entity;

import dev.dividendproject.model.Dividend;
import lombok.*;


import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Unique Key
/*
중복 데이터 저장을 방지하는 제약조건
단일 컬럼 뿐 아니라 복합 컬럼을 지정할 수도 있음
* */

@Entity(name = "DIVIDEND")
@Getter
@ToString
@NoArgsConstructor
@Table(
        uniqueConstraints =
                {
        @UniqueConstraint(
                columnNames = {"companyId", "date"}
        )
    }
)
public class DividendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;
    private LocalDateTime date;
    private String dividend;
    // 회사
    // column type unique example
    // id     long    O       1
    // name   String      Coca-Cola
    // ticker String  O   COKE

    // 배당금
    // column       type unique example
    // id           long    O       3
    // company-id   Long            1
    // date     LocalDateTime    2022-05-05
    // dividend     String        2.00

    public DividendEntity(Long companyId, Dividend dividend) {
        this.companyId = companyId;
        this.date = dividend.getDate();
        this.dividend = dividend.getDividend();
    }
}

/*
* "ignore" 옵션
"ignore" 옵션은 중복된 레코드를 무시하고 삽입 작업을 계속합니다.
예를 들어, "insert ignore into table (id, name)
* values (1, 'John')"과 같은 쿼리를 실행하면 이미 id가 1인
* 레코드가 있더라도 중복 오류가 발생하지 않고 무시됩니다.
*
"on duplicate key update" 옵션
"on duplicate key update" 옵션은 중복된 레코드가 있을 때
* 해당 레코드를 업데이트합니다.
예를 들어, "insert into table (id, name) values (1, 'John')
* on duplicate key update name='John Smith'"과 같은 쿼리를
* 실행하면 id가 1인 레코드가 이미 있을 때 "name" 열의 값을
* 'John Smith'로 업데이트합니다.
* */

// DB Index
// Cardinality 높음: 데이터베이스에 있어서 중요한개념!
// 중복이 없는 것으로, 인덱스가 효율적으로 동작할 가능성이 높다.
// 선택도, 해당 쿼리가 얼마나 호출되는지 등을 고려해야 할 사항이 많다.