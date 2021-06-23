package com.dlink.explainer.trans;

import lombok.Getter;
import lombok.Setter;

/**
 * Predecessor
 *
 * @author wenmo
 * @since 2021/6/22
 **/
@Getter
@Setter
public class Predecessor {
    private Integer id;
    private String shipStrategy;
    private String side;

    public Predecessor(Integer id, String shipStrategy, String side) {
        this.id = id;
        this.shipStrategy = shipStrategy;
        this.side = side;
    }

    @Override
    public String toString() {
        return "Predecessor{" +
                "id=" + id +
                ", shipStrategy='" + shipStrategy + '\'' +
                ", side='" + side + '\'' +
                '}';
    }
}
