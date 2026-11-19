import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MeasurementLogicTest {

    @Test
    void shouldTrimAndParseValidId() {
        int parsed = Integer.parseInt(" 42 ".trim());
        assertEquals(42, parsed);
    }

    @Test
    void shouldFailOnBlankId() {
        assertThrows(NumberFormatException.class, () -> Integer.parseInt("".trim()));
    }
}
