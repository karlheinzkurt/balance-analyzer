package org.insaneheadoflettuce.balanceAnalyzer;

import java.util.Objects;

public class Number {
    private final Double value;
    private final boolean absolute;

    public Number(Double value) {
        this(value, false);
    }

    public Number(Double value, boolean absolute) {
        this.value = value;
        this.absolute = absolute;
    }

    public boolean isPositive() {
        return value > 0.;
    }

    public boolean isNegative() {
        return value < 0.;
    }

    public Double getValue() {
        return value;
    }

    public String getColorClass() {
        if (value == 0.) {
            return "";
        }
        if (absolute) {
            return "absolute";
        }
        return isNegative() ? "negative" : "positive";
    }

    @Override
    public String toString() {
        return value == 0. ? "0" : String.format("%.2f", value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (getClass() != other.getClass()) {
            return false;
        }
        return Objects.equals(value, ((Number) other).value);
    }
}
