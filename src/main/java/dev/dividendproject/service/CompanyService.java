package dev.dividendproject.service;

import dev.dividendproject.exception.impl.NoCompanyException;
import dev.dividendproject.persist.entity.CompanyEntity;
import dev.dividendproject.persist.entity.DividendEntity;
import dev.dividendproject.model.Company;
import dev.dividendproject.model.ScrapedResult;
import dev.dividendproject.persist.CompanyRepository;
import dev.dividendproject.persist.DividendRepository;
import dev.dividendproject.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {
    private final Trie trie; // 싱글톤으로 관리된다.
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    // 저장
    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);

        if(exists){
            throw new RuntimeException("already exists ticker -> " + ticker);
        }
        return storeCompanyAndDividend(ticker); // 데이터 베이스에 저장이 되어있지 않다면, storeCompanyAndDividend를 작업하여 저장
    }

    // 조회
    public Page<CompanyEntity> getAllCompany(Pageable pageable){
        return this.companyRepository.findAll(pageable); //repository의 finaAll 함수는 JPA에서 상속 받은 것
    }
    /*Spring Framework에서 Pageable은 데이터베이스에서 데이터를
    페이징하여 가져올 수 있는 기능을 제공하는 인터페이스입니다.
    Pageable 인터페이스는 페이지 번호, 페이지 크기, 정렬 기준 등의 정보를
    제공하여, 데이터를 페이징 처리하고 필요한 페이지만 가져올 수 있도록 합니다.
    데이터베이스에서 많은 양의 데이터를 처리할 때, 전체 데이터를 한 번에 불러오는
    것은 시스템 성능에 부담이 됩니다. 따라서 Pageable 인터페이스를
    사용하여 필요한 페이지만 조회하면서, 시스템 성능을 최적화할 수 있습니다.
    Spring Data JPA와 함께 사용하면, Pageable 인터페이스를
    파라미터로 전달하여 쉽게 페이징 처리된 데이터를 조회할 수 있습니다.
    이를 이용하여, 데이터를 보다 쉽고 빠르게 처리할 수 있습니다.*/
    // 스크래핑

    // 이미 저장되어있는지 회사인지 확인 하고, 저장하지 않은 경우에만 storeCompanyAndDividend 작업을 수행한다.
    // private으로 선언하고 구현해줘서 클래스 밖에서는 외부에서 호출 할 수 없다.
    private Company storeCompanyAndDividend(String ticker) {

        // ticker를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);

        if(ObjectUtils.isEmpty(company)){
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }
        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과 -- 배당금 엔터티에는 companyId도 같이 저장되어야 한다.
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));

        // e는 컬렉션의 값 하나하나가 된다.(dividend 아이템 하나하나가 e에 해당이 된다.)
        List<DividendEntity> dividendEntityList = scrapResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList()); // 리스트 타입으로 변환 해줌
                // 필요에 따라 추가로 .filter나 .sort를 사용해도 좋다.

        this.dividendRepository.saveAll(dividendEntityList);

        return company;
    }

    public List<String> getCompanyNamesByKeyword(String keyword){
        Pageable limit = PageRequest.of(0,10);
        Page<CompanyEntity> companyEntities =
                this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);

        return  companyEntities.stream()
                .map(e -> e.getName())
                .collect(Collectors.toList());
    }

    public void addAutocompleteKeyword(String keyword){
        this.trie.put(keyword, null); // null은 아파치에서 구현된것으로 응용된 형태이기 때문에 키, value 둘다 넣을 수 있지만, 키워드 하나만 필요해서 null로 넣어준다.
    }

    public List<String> autocomplete(String keyword){
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    } // limit(가져올 갯수) 를 적어서 가져오는 데이터 수의 제한을 걸 수 있다.
//    public AutoComplete(Trie trie){
//        this.trie = trie; // 이런식으로 오토컴플릿 메서드 안에 트라이를 초기화 해주면 각 인스턴스에서 사용 가능
//    }

    public void deleteAutocompleteKeyword(String keyword){
        this.trie.remove(keyword);
    }

    public String deleteCompany(String ticker){
        var company = this.companyRepository.findByTicker(ticker)
                .orElseThrow(()-> new NoCompanyException());

        this.dividendRepository.deleteAllByCompanyId(company.getId()); // 배당금 데이터 지우기
        this.companyRepository.delete(company);

        // 자동완성을 하기 위해 넣어놨던 트라이의 데이터도 삭제
        this.deleteAutocompleteKeyword(company.getName());

        return company.getName();
    }
}

/*
* Like 연산자
* - 부분적으로 일치하는 조건으로 데이터를 찾기 위해 사용
* - 특정 문자열이 포함된 경우
* - 특정 문자열로 시작하거나 특정 문자열로 종료될 때
*
*
* SELECT*
*       FROM company
*       WHERE name LIKE "LA%";   -- > LA로 시작하는 모든 문자
*
 * SELECT*
 *       FROM company
 *       WHERE name LIKE "%A%";   -- > 중간에 A가 있는 문자
*
* SELECT*
*       FROM company
*       WHERE name LIKE "%A";   -- > 끝에 A가 있는 문자
*
* LIKE 연산자 : NOT LIKE, LIKE IN
*
* %: 모든 문자
* _: 한 글자
* */


/*Trie : 트리형 자료구조, 문자열 탐색을 효율적으로 할 수 있음, 중복 저장 X

Trie에 데이터 저장하기
- 삽입하고자 하는 문자열을 앞에서부터 한 글자씩 가져온다
- 트리의 루트부터 적합한 노드 위치를 찾아가면서 저장
- 마지막 글자까지 삽입이 되면 isEnd플래그로 단어의 끝을 표시

Trie에서 데이터 검색하기
_ 인풋으로 받은 문자열을 한글자씩 파싱
- 파싱된 문자를 앞에서부터 비교
- 해당 문자 노드가 존재하지 않거나, 리프노드에 도달할 때 까지 탐색

- 시간 복잡도(O(L), L은 문자열의 길이)
- 메모리도 훨씬 더 많이 차지를 하게 된다. (모든 문자열에 해당하는 하위 노드들을 가질 수 있다.)

Trie 자료구조는 아파치의 Trie로 사용하게 된다.*/