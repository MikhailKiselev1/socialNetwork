package org.javaproteam27.socialnetwork.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javaproteam27.socialnetwork.config.ParserConfig;
import org.javaproteam27.socialnetwork.model.dto.request.CurrencyRateRq;
import org.javaproteam27.socialnetwork.model.entity.Currency;
import org.javaproteam27.socialnetwork.repository.CurrencyRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyRateService {

    private final CurrencyRepository currencyRepository;
    private final ParserConfig parserConfig;
    private static final String CURRENCY_TOKEN = "347140588eb5bf9ecc56961cf20e44af";

    public CurrencyRateRq getCurrencyRate() {
        String url = "https://currate.ru/api/?get=rates&pairs=USDRUB,EURRUB&key=" + CURRENCY_TOKEN;
        CurrencyRateRq currencyRateRq = new CurrencyRateRq();
        try {
            var currencyDoc = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .execute()
                    .body();
            JSONObject currency = (JSONObject) new JSONParser().parse(currencyDoc);
            JSONObject data = (JSONObject) currency.get("data");
            currencyRateRq.setUsd(data.get("USDRUB").toString());
            currencyRateRq.setEuro(data.get("EURRUB").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currencyRateRq;
    }

    @Scheduled(initialDelay = 3000, fixedRateString = "PT2H")
    private void updateCurrencyRate() {
        if (parserConfig.isEnabled()) {
            log.info("Started updating currency rate");
            var rate = getCurrencyRate();
            if (rate.getEuro() != null) {
                Currency currencyUsd = Currency.builder().name("USD").price(rate.getUsd()).build();
                Currency currencyEuro = Currency.builder().name("EUR").price(rate.getEuro()).build();
                currencyRepository.saveOrUpdate(currencyUsd);
                currencyRepository.saveOrUpdate(currencyEuro);
                log.info("Currency rate updated");
            } else log.info("An error occurred while getting the currency rate");
        } else {
            log.info("Parsing disabled");
        }
    }
}
