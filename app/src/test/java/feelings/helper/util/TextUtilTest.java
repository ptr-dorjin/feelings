package feelings.helper.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TextUtilTest {

    @Test
    public void testGetPluralText() {
        assertEquals("1 час", hour(1));
        assertEquals("2 часа", hour(2));
        assertEquals("3 часа", hour(3));
        assertEquals("4 часа", hour(4));
        assertEquals("5 часов", hour(5));
        assertEquals("6 часов", hour(6));
        assertEquals("7 часов", hour(7));
        assertEquals("8 часов", hour(8));
        assertEquals("9 часов", hour(9));
        assertEquals("10 часов", hour(10));
        assertEquals("11 часов", hour(11));
        assertEquals("12 часов", hour(12));
        assertEquals("13 часов", hour(13));
        assertEquals("14 часов", hour(14));
        assertEquals("15 часов", hour(15));
        assertEquals("16 часов", hour(16));
        assertEquals("17 часов", hour(17));
        assertEquals("18 часов", hour(18));
        assertEquals("19 часов", hour(19));
        assertEquals("20 часов", hour(20));
        assertEquals("21 час", hour(21));
        assertEquals("22 часа", hour(22));
        assertEquals("23 часа", hour(23));
        assertEquals("24 часа", hour(24));
        assertEquals("25 часов", hour(25));
    }

    private String hour(int number) {
        return number + " " + TextUtil.getPluralText(number, "час", "часа", "часов");
    }
}
