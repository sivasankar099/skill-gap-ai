package com.skillgap.ai.entity;

public enum Importance {
    HIGH(3),
    MEDIUM(2),
    LOW(1);

    private final int weight;

    Importance(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
