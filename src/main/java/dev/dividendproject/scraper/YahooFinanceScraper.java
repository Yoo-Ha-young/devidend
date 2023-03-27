package dev.dividendproject.scraper;

import dev.dividendproject.model.constants.Month;
import dev.dividendproject.model.Company;
import dev.dividendproject.model.Dividend;
import dev.dividendproject.model.ScrapedResult;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {
    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400; // 60초*60분*24하루

    @Override
    public ScrapedResult scrap(Company company){
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000; //  끝시간
            // 현재시간을 밀리세컨으로 받는 값이라 밀리에서 1000으로 나눠서 받는다.    // 시작시간, 끝시간
            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now); // 치환받을 값, 되는 값들을 차례대로 넣어줌

            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableEle = parsingDivs.get(0); // table 전체

            Element tbody = tableEle.children().get(1);

            // 스크래핑 된 결과는 List<Dividend> 리스트인 dividends에 담아서 사용한다.
            List<Dividend> dividends = new ArrayList<>();
            for(Element e: tbody.children()){
                String txt = e.text();
                if(!txt.endsWith("Dividend")){
                    continue;
                }
                String[] splits = txt.split(" ");

                //                        .date(LocalDateTime.of(year,month,day,0,0))
                int month = Month.strToNumber(splits[0]);
                int day = Integer.valueOf(splits[1].replace(",",""));
                int year = Integer.valueOf(splits[2]);

                //                         .dividend(dividend)
                String dividend = splits[3];

                if(month < 0){
                    throw new RuntimeException("Unexpected Month enum value ->" + splits[0]);
                }
                // 스크래핑이 정상적으로 되었다면 Dividend에 데이터를 저장
                // List<Dividend> dividends = new ArrayList<>(); 그리고 이 리스트에 저장이 바로 되도록 함.
                dividends.add(new Dividend(LocalDateTime.of(year,month,day,0,0),dividend));
//                System.out.println(year + "/" + month + "/" + day + " -> " + dividend);

            }
            scrapResult.setDividends(dividends);

        } catch (IOException e) {
            // 맵핑이 되지 않았다면 출력하는 것
            // TODO
            e.printStackTrace();
        }
        return scrapResult;
    }

    // 회사명 뿐만 아니라, 다른 정보도 같이 가져오고 싶다면 해당 메소드
    // 내에서 스크래핑 해올때 추가로 긁어올 수 있도록 구현
    @Override
    public Company scrapCompanyByTicker(String ticker){
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            // 태그를 사용하는 요소로 회사명을 가져옴
            Element titleEle = document.getElementsByTag("h1").get(0);
            // 깔끔하게 가져오기 위한 문자 후처리 작업
            String title = titleEle.text().split(" - ")[0].trim();
            // abc - def - xzy 형태로 있다면 , - 기준으로 쪼갠 뒤 배열에 들어가게 되면, 그 중 1번째 인덱스의 값을 가져온다.

            return new Company(ticker, title);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

/*
 * Document :
 * Java 언어로 작성된 jsoup 라이브러리를 사용하여 URL에서 HTML문서를 가져와서
 * Document 객체로 파싱하는 코드
 *
 * Jsoup 라이브러리는 HTML 문서를 파싱하여 원하는 정보를 추출하거나, 문서 내용을 수정하거나,
 * 새로운 HTML 문서를 생성하는 등의 작업을 수행할 수 있는 기능을 제공
 *
 * 위 코드에서 connect() 메서드는 지정된 URL에 연결해주고,
 * get() 메서드를 사용하여 해당 URL HTML 문서를 가져와서 Document 객체로 반환한다.
 *
 * 이후 반횐된 Document 객체를 사용해 Jsoup 라이브러리에서 제공하는 다양한 메서드를 호출하여 작업할 수 있다.

 * Document 클래스는 Jsoup 라이브러리에서 핵심적인 클래스 중 하나이며,
 * HTML 문서를 파싱하여 나온 결과물을 저장하는 클래스이다.
 * HTML 문서를 모델링한 것으로, HTML 문서의 각 요소들을 트리 구조로 나타내는 DOM(Document Object Model)을 기반으로 만들어 졌다.
 *
 * Document 객체는 HTML 문서의 루트 요소에 해당하는 <html>요소를 나타내고
 * 이 객체를 통해 HTML 문서 내의 모든 요소에 대한 접근이 가능하다.
 * Document 객체는 HTML 문서 내의 모든 요소들을 탐색하고, 필요한 정보를 추출하거나 수정하는 등의 작업에 사용된다.
 *
 *주요 메서드 :
 * select(String cssQuery) : 지정한 CSS선택자를 사용하여 HTML 문서 내에서 요소 선택
 * getElementById(String id) : 지정한 ID 속성을 가진 요소를 선택
 * getElementByTag(String tagName) : 지정한 태그 이름을 가진 모든 요소를 선택
 * append(String html) : HTML 문자열을 파싱하여 Document 객체에 추가
 * text() : HTML 문서 내의 텍스트를 추출
 * html() : HTML 문서 내의 HTML 코드를 추출
 */
