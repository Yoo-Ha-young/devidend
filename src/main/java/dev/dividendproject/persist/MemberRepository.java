package dev.dividendproject.persist;

import dev.dividendproject.persist.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity,Long> {
    Optional<MemberEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
// EXISTS(서브 쿼리)는 서브 쿼리의 결과가 "한 건이라도 존재하면" TRUE 없으면 FALSE를 리턴한다.
