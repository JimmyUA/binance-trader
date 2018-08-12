package geometry.prediction;

import java.time.LocalDateTime;

public class PredictionPoint {

    private LocalDateTime time;
    private Double price;

    public LocalDateTime getTime() {
        return time;
    }

    public PredictionPoint setTime(LocalDateTime time) {
        this.time = time;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public PredictionPoint setPrice(Double price) {
        this.price = price;
        return this;
    }

    @Override
    public String toString() {
        return "PredictionPoint{" +
                "time=" + time +
                ", price=" + price +
                '}';
    }
}
