package io.github.unterstein.persistent.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "amplitude")
public class Amplitude implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "max")
    private Double max;

    @Column(name = "min")
    private Double min;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }
}
