package dev.dividendproject.persist;

import dev.dividendproject.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    // 리스트에 회사 존재 여부를 불린값으로 받아옴
    boolean existsByTicker(String ticker);

    // 회사명을 기준으로 조회
    Optional<CompanyEntity> findByName(String name);

    Optional<CompanyEntity> findByTicker(String ticker);

    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);
}

/*Java 8 이상에서 추가된 Optional은 null-safe한 프로그래밍을 도와주는
클래스입니다. Optional은 객체가 null인 경우에 대한 처리를 명시적으로
다룰 수 있도록 합니다.
예를 들어, 위 코드에서 findByName 메서드가 이름으로 회사 엔티티를 찾는
메서드라고 가정하면, 만약 해당 이름으로 회사가 존재하지 않으면 null을 반환할
것입니다. 하지만 Optional을 사용하면, 반환값이 null이 될 가능성이 있는
경우에도 메서드 시그니처에 Optional 객체를 명시하여 반환값이 존재하지
않는 경우를 나타내는 데 사용할 수 있습니다.
따라서, 위 코드에서 반환값으로 Optional<CompanyEntity>이
사용된 것은 해당 메서드가 이름으로 회사를 찾는데, 만약 해당 이름으로
회사가 존재하지 않을 경우 반환값이 null이 아니라 Optional.empty()를
반환하도록 명시된 것입니다. 이를 통해 클라이언트는 반환값이 null일
가능성이 있다는 것을 명시적으로 알 수 있고, 이에 대한
처리를 구현할 수 있습니다.*/