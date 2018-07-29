package global_monitor.starter;

import global_monitor.coins.Coin;
import io.github.unterstein.BinanceBotApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TraderStarter {
    public void startTrader(Coin coin) {
        final String uri = "http://localhost:" + coin.getPort() + "/startTrading";
        System.out.println(uri);
        RestTemplate restTemplate = new RestTemplate();
        try {

            restTemplate.getForObject(uri, Void.class);
        } catch (HttpMessageNotReadableException e){
            e.printStackTrace();
        }
        coin.start();
    }
}
