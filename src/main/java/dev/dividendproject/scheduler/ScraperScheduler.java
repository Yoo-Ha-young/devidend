package dev.dividendproject.scheduler;


import dev.dividendproject.persist.entity.CompanyEntity;
import dev.dividendproject.persist.entity.DividendEntity;
import dev.dividendproject.model.Company;
import dev.dividendproject.model.ScrapedResult;
import dev.dividendproject.model.constants.CacheKey;
import dev.dividendproject.persist.CompanyRepository;
import dev.dividendproject.persist.DividendRepository;
import dev.dividendproject.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;

//    @Scheduled(fixedDelay = 1000)
//    public void test1() throws InterruptedException{
//        Thread.sleep(10000); // 10초간 일시 정지
//        System.out.println(Thread.currentThread().getName() + " -> 테스트 1 :" + LocalDateTime.now());
//    }
//
//    @Scheduled(fixedRate = 1000)
//    public void test2() throws InterruptedException{
//        Thread.sleep(10000); // 10초간 일시 정지
//        System.out.println(Thread.currentThread().getName() + " -> 테스트 2 :" + LocalDateTime.now());
//    }

    // 일정 주기마다 수행       // yml에서 작성된 경로    // 이렇게 삭제를 하면 이후에 또 배당금을 조회하는 시점에 다시 새로운 데이터가 저장된다
    @CacheEvict(value= CacheKey.KEY_FINANCE, allEntries = true) // 레디스 캐시에 있는 finance 엔트리에 있는 데이터는 모두 다 비운다는 뜻
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started");
        // 저장된 회사 목록을 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        // 회사마다 배당금 정보를 서로 스크래핑
        for (var company : companies) {
            log.info("scraping scheduler is started -> " + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(
                    new Company(company.getTicker(), company.getName()));

            // 스크래핑한 배당금 정보 중 데이터베이스에 없는 값만 저장
            // 하나씩 있는지 확인하면서 저장을 해줘야 예외처리 한것이 바르게 작동하게 된다.
            scrapedResult.getDividends().stream()
                    // dividend 모델을 dividend 엔터티로 맵핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    // 엘리먼트를 하나씩 dividend 레파지토리에 삽입(없는 값만)
                    .forEach(e -> {  // 데이터들을 for each문으로 돌면서 있는지 없는지 불린값으로 받아냄
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists) {
                            this.dividendRepository.save(e);
                            log.info("insert new dividend -> " + e.toString());
                        }
                    });

            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시 정지
            try {
                Thread.sleep(3000); // 3 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            // Thread.sleep(n)
            // : 실행중인 스레드를 잠시 멈추게 할 때 사용, thread.sleep(1000); 1초간 정지
            // InterruptedException : 인터럽트를 받는 스레드가 blocking 될 수 있는 메소드를 실행할 때 발생
            // sleep() vs wait()
            // wait()는 스레드를 대기 상태에 빠뜨림
            // notify()나 notifyAll() 메소드를 호출할 때까지 자동으로 꺠지 않음
            // Thread 5 status
            // New, Ready, Running, Blocked/Waiting, Exit
        }
    }
}
// Thread Pool : 여러 개의 스레드를 유지/관리
// 적정 사이즈는? CPU 처리가 많은 경우 : n+1, I/O 작업이 많은 경우 : 코어 갯수 *2 정도