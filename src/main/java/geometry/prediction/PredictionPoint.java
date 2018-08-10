package geometry.prediction;

import java.time.LocalDate;

public class PredictionPoint {

    private LocalDate date;
    private Double price;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "PredictionPoint{" +
                "date=" + date +
                ", price=" + price +
                '}';
    }
}
