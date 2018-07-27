package global_monitor.starter;

import global_monitor.coins.Coin;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TraderStopper {
    public void stopTrader(Coin coin) {
        final String uri = "http://localhost:" + coin.getPort() + "/stop";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(uri, Void.class);
    }
}
