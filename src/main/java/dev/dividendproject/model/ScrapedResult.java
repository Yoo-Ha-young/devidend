package dev.dividendproject.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Data
@ToString
@AllArgsConstructor
public class ScrapedResult { // 스크랩 결과 회사정보, 배당금 엔터티를 리스트에 담음

    private Company company;
    private List<Dividend> dividends; // // dividend Entities
    public ScrapedResult() {this.dividends = new ArrayList<>();}

}


