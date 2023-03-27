package dev.dividendproject.scraper;

import dev.dividendproject.model.Company;
import dev.dividendproject.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
