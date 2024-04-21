package org.insaneheadoflettuce.balance_analyzer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NumberTest {
    @Test
    void isPositive() {
        Assertions.assertTrue(new Number(3.).isPositive());
        Assertions.assertFalse(new Number(-3.).isPositive());
    }

    @Test
    void isNegative() {
        Assertions.assertFalse(new Number(3.).isNegative());
        Assertions.assertTrue(new Number(-3.).isNegative());
    }

    @Test
    void getValue() {
        Assertions.assertEquals(23., new Number(23.).getValue());
    }

    @Test
    void getColorClass() {
        Assertions.assertEquals("", new Number(0.).getColorClass());
        Assertions.assertEquals("", new Number(0., true).getColorClass());
        Assertions.assertEquals("", new Number(0., false).getColorClass());
        Assertions.assertEquals("absolute", new Number(5., true).getColorClass());
        Assertions.assertEquals("positive", new Number(5., false).getColorClass());
        Assertions.assertEquals("positive", new Number(5.).getColorClass());
        Assertions.assertEquals("absolute", new Number(-5., true).getColorClass());
        Assertions.assertEquals("negative", new Number(-5., false).getColorClass());
        Assertions.assertEquals("negative", new Number(-5.).getColorClass());
    }

    @Test
    void equals() {
        final var _23 = new Number(23.);
        Assertions.assertFalse(_23.equals(null));
        Assertions.assertTrue(_23.equals(_23));
        Assertions.assertFalse(_23.equals(5));
        Assertions.assertTrue(_23.equals(new Number(23.)));
        Assertions.assertFalse(_23.equals(new Number(5.)));
    }

    @Test
    void testToString() {
        Assertions.assertEquals("0", new Number(0.).toString());
        // Can contain , or . depending on location, hence we use a regex for matching
        Assertions.assertTrue(new Number(1. / 3.).toString().matches("0.33"));
        Assertions.assertTrue(new Number(0.1).toString().matches("0.10"));
        Assertions.assertTrue(new Number(10000000.1).toString().matches("10000000.10"));
    }

    @Test
    void hashCodeTest() {
        Assertions.assertEquals(Double.hashCode(23.), new Number(23.).hashCode());
    }

}
