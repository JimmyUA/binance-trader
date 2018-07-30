package global_monitor.coins;

public class Coin {

    private String symbol;
    private String port;
    private boolean isStarted;

    public Coin(String symbol, String port) {
        this.symbol = symbol;
        this.port = port;
        isStarted = true;
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

    public String getPort() {
        return port;
    }
}
