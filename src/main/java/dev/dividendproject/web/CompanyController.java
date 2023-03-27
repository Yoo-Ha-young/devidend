package dev.dividendproject.web;

import dev.dividendproject.model.constants.CacheKey;
import dev.dividendproject.persist.entity.CompanyEntity;
import dev.dividendproject.model.Company;
import dev.dividendproject.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final CacheManager redisCacheManager;
    // 배당금 검색 + 자동완성
    // GET /company/autocomplete?keyword=O
    // {result:["O", "OAS",...]}
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword){
        var result = this.companyService.getCompanyNamesByKeyword(keyword);
        return ResponseEntity.ok(result);
    }

    // 회사리스트 조회
    // GET /company
    // {result:[{companyName:"좋은 회사", ticker:"GOOD"},...]]}

    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(final Pageable pageable){
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companies);
    }


    // 관리자 기능 - 배당금 저장
    /**
     * 회사 및 배당금 정보 추가
     * @param request
     * @return
      */
    // POST /company
    // {ticker : "GOOD"}
    // {ticker:"GOOD", companyName:"좋은회사",...}
    // 호출하는 즉시 데이터베이스에 저장되어서 POST 방식으로 진행된다.
    @PostMapping
    @PreAuthorize("hasRole('WRITE')") // Authority에 있는 ROLE_ 하고 다음에 나오는 글자에 대한 권한
    public ResponseEntity<?> addCompany(@RequestBody Company request){
        String ticker = request.getTicker().trim();
        if(ObjectUtils.isEmpty(ticker)){
            throw new RuntimeException("ticker is empty");
        }

        Company company = this.companyService.save(ticker);
        this.companyService.addAutocompleteKeyword(company.getName());
        return ResponseEntity.ok(company); // ok 응답은 컴퍼니 엔터티 값으로 반환
    }

    // 관리자 기능 - 배당금 삭제
    // DELETE
    // /company?ticker=GOOD
    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker){
        String companyName = this.companyService.deleteCompany(ticker);
        this.clearFinanceCache(companyName);
        return ResponseEntity.ok(companyName);
    }

    public void clearFinanceCache(String companyName){
        this.redisCacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);
    }
}
/*@Controller와 @RestController는 Spring 프레임워크에서
컨트롤러 역할을 하는 클래스에 대한 어노테이션입니다.

@Controller는 전통적인 MVC 패턴에서 사용되는 컨트롤러입니다.
주로 View를 반환하고, View와 관련된 모델 데이터를 설정하고,
사용자 입력에 대한 처리를 담당합니다. @Controller를 사용하면
메서드 반환 값으로 String, ModelAndView, View를 사용할 수 있습니다.

@RestController는 Spring 4.0부터 추가된 어노테이션으로,
@Controller와 유사하지만 주로 RESTful 웹 서비스를 제공하는 경우 사용됩니다. @RestController를 사용하면 JSON, XML 등의 형식으로 데이터를 반환할 수 있습니다. 메서드 반환 값으로 객체를 반환하면 Spring은 객체를 JSON 형식으로 변환하여 HTTP 응답으로 반환합니다.

즉, @Controller는 View를 반환하고, @RestController는
 데이터를 반환합니다.*/