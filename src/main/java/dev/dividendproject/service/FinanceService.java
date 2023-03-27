package dev.dividendproject.service;


import dev.dividendproject.exception.impl.NoCompanyException;
import dev.dividendproject.persist.entity.CompanyEntity;
import dev.dividendproject.persist.entity.DividendEntity;
import dev.dividendproject.model.Company;
import dev.dividendproject.model.Dividend;
import dev.dividendproject.model.ScrapedResult;
import dev.dividendproject.model.constants.CacheKey;
import dev.dividendproject.persist.CompanyRepository;
import dev.dividendproject.persist.DividendRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;


    // 요청이 자주 들어오는가?
    // 자주 변경되는 데이터인가?

    @Cacheable(key = "#companyName",value= CacheKey.KEY_FINANCE)  // key 메소드에 파라미터로 들어오는 companyName, value는 finance
    public ScrapedResult getDividendByCompanyName(String companyName){
        log.info("search company -> " + companyName);
        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new NoCompanyException());
        // 값이 없으면 인자로 넘겨주는 값을 넘겨주고, 정상일 경우 원래 있던 데이터를 넘겨준다.
        /*
        * 람다 표현식에서 ->는 "인자를 받아서 어떤 일을 한다"는 의미를 가집니다.
        *  위의 람다 표현식에서는 인자가 없으므로 () 는 비어있고,
        *  -> 다음에는 RuntimeException을 생성하는 코드가 옵니다.
        *  이 코드는 () -> 표현식의 결과로 람다 표현식을 구성합니다.
        * */

        // 2. 조회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 ScrapedResult로 반환
        // ScrapedResult company와 dividends이 모두
        // 엔터티 타입(가져온 값이 CompanyEntity, DividendEntity)이
        // 아닌 일반형이기 때문에, 가져온 값을 일반형으로 바꾸어주는 맵핑이 필요하다.
        // builder를 사용해서 company 모델로 바꿔줌


        // 엔터티를 모델 클래스로 맵핑해주는 가공해주는 작업의 방법 : for each문, stream
        //        List<Dividend> dividends = new ArrayList<>();
        //        for(var entity : dividendEntities){
        //            dividends.add(Dividend.builder()
        //                            .date(entity.getDate())
        //                            .dividend(entity.getDividend())
        //                            .build());
        //        }

        // 스트림을 쓰는 방법도 가능하다.
        List<Dividend> dividends = dividendEntities.stream()
                                    .map(e -> new Dividend(e.getDate(),
                                            e.getDividend()))
                                    .collect(Collectors.toList());

        return new ScrapedResult(new Company(company.getTicker(),
                company.getName()),dividends);
    }
}
