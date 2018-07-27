package global_monitor.coins;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CoinsContainer {

    private List<Coin> coins;

    public CoinsContainer() {
        coins = new ArrayList<Coin>();
    }

    public void add(Coin coin) {
        coins.add(coin);
    }

    public List<Coin> getStartedCoins() {
        return coins.stream().filter(Coin::isStarted).collect(Collectors.toList());
    }

    public List<Coin> getStoppedCoins() {
        return coins.stream().filter(coin -> !coin.isStarted()).collect(Collectors.toList());
    }
}
