package global_monitor.coins;

public class Coin {

    private String symbol;
    private boolean isStarted;

    public Coin(String symbol) {
        this.symbol = symbol;
        isStarted = false;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void start() {
        isStarted = true;
    }
    public void stop() {
        isStarted = false;
    }

}
