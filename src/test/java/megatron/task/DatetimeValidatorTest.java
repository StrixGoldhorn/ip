package megatron.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import org.junit.jupiter.api.Test;

/** Tests supported and invalid inputs for {@link DatetimeValidator}. */
class DatetimeValidatorTest {

    @Test
    void parseToLocalDateTime_isoInput_parsesDateAndSupportedTimeStyles() {
        assertAll(
                () -> assertEquals(LocalDateTime.of(2026, 8, 6, 0, 0),
                        DatetimeValidator.parseToLocalDateTime("2026-08-06")),
                () -> assertEquals(LocalDateTime.of(2026, 8, 6, 14, 0),
                        DatetimeValidator.parseToLocalDateTime("2026-08-06 1400")),
                () -> assertEquals(LocalDateTime.of(2026, 8, 6, 14, 5),
                        DatetimeValidator.parseToLocalDateTime("2026-08-06 14:05")),
                () -> assertEquals(LocalDateTime.of(2026, 8, 6, 14, 0),
                        DatetimeValidator.parseToLocalDateTime("2026-08-06 2pm")),
                () -> assertEquals(LocalDateTime.of(2026, 8, 6, 14, 5),
                        DatetimeValidator.parseToLocalDateTime("2026-08-06 2:05pm")));
    }

    @Test
    void parseToLocalDateTime_supportedDateStyles_parsesEachDateStyle() {
        assertAll(
                () -> assertEquals(LocalDateTime.of(2026, 8, 6, 14, 0),
                        DatetimeValidator.parseToLocalDateTime("6/8/2026 1400")),
                () -> assertEquals(LocalDateTime.of(2026, 8, 6, 14, 0),
                        DatetimeValidator.parseToLocalDateTime("Aug 6 2026 14:00")),
                () -> assertEquals(LocalDateTime.of(2026, 8, 6, 14, 0),
                        DatetimeValidator.parseToLocalDateTime("August 6 2026 2pm")),
                () -> assertEquals(LocalDateTime.of(2026, 8, 6, 14, 5),
                        DatetimeValidator.parseToLocalDateTime("6 Aug 2026 2:05pm")),
                () -> assertEquals(LocalDateTime.of(2026, 8, 6, 0, 0),
                        DatetimeValidator.parseToLocalDateTime("6 August 2026")));
    }

    @Test
    void parseToLocalDateTime_irregularCaseWhitespaceAndOrdinal_normalisesInput() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 14, 5),
                DatetimeValidator.parseToLocalDateTime("  AUGUST   6th   2026   2:05 pm  "));
    }

    @Test
    void parseToLocalDateTime_textDateWithoutYear_usesCurrentYear() {
        int currentYear = LocalDate.now().getYear();

        assertAll(
                () -> assertEquals(LocalDateTime.of(currentYear, 8, 6, 0, 0),
                        DatetimeValidator.parseToLocalDateTime("Aug 6")),
                () -> assertEquals(LocalDateTime.of(currentYear, 8, 6, 14, 0),
                        DatetimeValidator.parseToLocalDateTime("August 6 2pm")));
    }

    @Test
    void parseToLocalDateTime_weekdayWithoutTime_usesNextOccurrenceAtMidnight() {
        LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        assertEquals(nextMonday.atStartOfDay(), DatetimeValidator.parseToLocalDateTime("monday"));
    }

    @Test
    void parseToLocalDateTime_invalidInput_throwsIllegalArgumentException() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DatetimeValidator.parseToLocalDateTime(null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DatetimeValidator.parseToLocalDateTime("")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DatetimeValidator.parseToLocalDateTime("   ")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DatetimeValidator.parseToLocalDateTime("31/4/2026")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DatetimeValidator.parseToLocalDateTime("2025-02-29")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DatetimeValidator.parseToLocalDateTime("2026-08-06 2400")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> DatetimeValidator.parseToLocalDateTime("not a date")));
    }
}
