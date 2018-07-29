package global_monitor.starter;

import global_monitor.coins.Coin;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TraderStopper {
    public void stopTrader(Coin coin) {
        final String uri = "http://localhost:" + coin.getPort() + "/stopTrading";
        RestTemplate restTemplate = new RestTemplate();
        try {

            restTemplate.getForObject(uri, Void.class);
        } catch (HttpMessageNotReadableException e){
            e.printStackTrace();
        }
        coin.stop();
    }
}
