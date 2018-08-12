package geometry.prediction;

import java.time.LocalDate;

public class PredictionPoint {

    private LocalDate date;
    private Double price;

    public LocalDate getDate() {
        return date;
    }

    public PredictionPoint setDate(LocalDate date) {
        this.date = date;
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
                "date=" + date +
                ", price=" + price +
                '}';
    }
}
