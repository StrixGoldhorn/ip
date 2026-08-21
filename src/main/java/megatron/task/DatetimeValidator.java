package megatron.task;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parses supported user date/time formats into local date/time values. */
public final class DatetimeValidator {
    private static final String DEFAULT_OUTPUT_PATTERN = "dd MMM uu, HHmm'hrs'";
    private static final Map<String, DayOfWeek> WEEKDAYS = Map.ofEntries(
            Map.entry("mon", DayOfWeek.MONDAY), Map.entry("monday", DayOfWeek.MONDAY),
            Map.entry("tue", DayOfWeek.TUESDAY), Map.entry("tuesday", DayOfWeek.TUESDAY),
            Map.entry("wed", DayOfWeek.WEDNESDAY), Map.entry("wednesday", DayOfWeek.WEDNESDAY),
            Map.entry("thu", DayOfWeek.THURSDAY), Map.entry("thursday", DayOfWeek.THURSDAY),
            Map.entry("fri", DayOfWeek.FRIDAY), Map.entry("friday", DayOfWeek.FRIDAY),
            Map.entry("sat", DayOfWeek.SATURDAY), Map.entry("saturday", DayOfWeek.SATURDAY),
            Map.entry("sun", DayOfWeek.SUNDAY), Map.entry("sunday", DayOfWeek.SUNDAY));
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE
            .withResolverStyle(ResolverStyle.STRICT);
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            strictFormatter("d/M/uuuu HHmm"),
            strictFormatter("d/M/uuuu H:mm"),
            strictFormatter("d/M/uuuu ha"),
            strictFormatter("d/M/uuuu h:mma"),
            textFormatter("MMM d uuuu HHmm"),
            textFormatter("MMM d uuuu H:mm"),
            textFormatter("MMM d uuuu h:mma"),
            textFormatter("MMM d uuuu ha"),
            textFormatter("MMMM d uuuu HHmm"),
            textFormatter("MMMM d uuuu H:mm"),
            textFormatter("MMMM d uuuu h:mma"),
            textFormatter("MMMM d uuuu ha"),
            textFormatter("d MMM uuuu HHmm"),
            textFormatter("d MMM uuuu H:mm"),
            textFormatter("d MMM uuuu h:mma"),
            textFormatter("d MMM uuuu ha"),
            textFormatter("d MMMM uuuu HHmm"),
            textFormatter("d MMMM uuuu H:mm"),
            textFormatter("d MMMM uuuu h:mma"),
            textFormatter("d MMMM uuuu ha"));
    private static final List<DateTimeFormatter> DATE_ONLY_FORMATTERS = List.of(
            strictFormatter("d/M/uuuu"),
            textFormatter("MMM d uuuu"),
            textFormatter("MMMM d uuuu"),
            textFormatter("d MMM uuuu"),
            textFormatter("d MMMM uuuu"));
    private static final List<DateTimeFormatter> TIME_FORMATTERS = List.of(
            strictFormatter("HHmm"),
            strictFormatter("H:mm"),
            strictFormatter("ha"),
            strictFormatter("h:mma"));
    private static final Pattern TEXT_MONTH_WITHOUT_YEAR = Pattern.compile(
            "(?i)^(Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|"
                    + "Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|"
                    + "Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\s+"
                    + "(\\d{1,2})(?:\\s+(.*))?$");

    private DatetimeValidator() { }

    /** Converts a supported date/time string to a local date/time value. */
    public static LocalDateTime parseToLocalDateTime(String input) {
        return parse(input).value;
    }

    /** Returns whether the input explicitly included a time. */
    public static boolean hasExplicitTime(String input) {
        return parse(input).timeSpecified;
    }

    /** Formats a local date/time using the supplied DateTimeFormatter pattern. */
    public static String format(LocalDateTime value, String pattern) {
        return value.format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
    }

    /** Formats a local date/time using the chatbot's default numeric 24-hour format. */
    public static String formatForUser(LocalDateTime value) {
        return format(value, DEFAULT_OUTPUT_PATTERN);
    }

    /** Parses a user input and records whether its time was explicitly supplied. */
    private static ParsedDateTime parse(String input) {
        if (input == null || input.isBlank()) {
            throw invalidDateTime();
        }

        String value = normaliseInput(input);
        ParsedDateTime weekdayDateTime = parseWeekday(value);
        if (weekdayDateTime != null) {
            return weekdayDateTime;
        }

        // Try an explicit year before adding a default year for text-month inputs.
        ParsedDateTime dateTime = tryParseDateTime(value);
        if (dateTime != null) {
            return dateTime;
        }

        String valueWithYear = addCurrentYearIfMissing(value);
        if (valueWithYear != null) {
            dateTime = tryParseDateTime(valueWithYear);
            if (dateTime != null) {
                return dateTime;
            }
        }

        throw invalidDateTime();
    }

    /** Tries the supported date/time and date-only formatters. */
    private static ParsedDateTime tryParseDateTime(String value) {
        ParsedDateTime isoDateTime = tryParseIsoDateTime(value);
        if (isoDateTime != null) {
            return isoDateTime;
        }

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return new ParsedDateTime(LocalDateTime.parse(value, formatter), true);
            } catch (DateTimeParseException ignored) { }
        }
        for (DateTimeFormatter formatter : DATE_ONLY_FORMATTERS) {
            try {
                return new ParsedDateTime(LocalDate.parse(value, formatter).atStartOfDay(), false);
            } catch (DateTimeParseException ignored) { }
        }
        try {
            return new ParsedDateTime(LocalDate.parse(value, ISO_DATE).atStartOfDay(), false);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    /** Parses an ISO date followed by one of the supported time formats. */
    private static ParsedDateTime tryParseIsoDateTime(String value) {
        if (value.length() <= 10 || value.charAt(10) != ' ') {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(value.substring(0, 10), ISO_DATE);
            return new ParsedDateTime(date.atTime(parseTime(value.substring(11))), true);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    /** Adds the current year only to a text-month input that does not have a year. */
    private static String addCurrentYearIfMissing(String value) {
        Matcher matcher = TEXT_MONTH_WITHOUT_YEAR.matcher(value);
        if (!matcher.matches()) {
            return null;
        }
        String time = matcher.group(3);
        return matcher.group(1) + " " + matcher.group(2) + " " + LocalDate.now().getYear()
                + (time == null ? "" : " " + time);
    }

    /** Resolves a weekday to its next occurrence. A missing time means midnight. */
    private static ParsedDateTime parseWeekday(String value) {
        String[] parts = value.toLowerCase(Locale.ENGLISH).split("\\s+", 2);
        DayOfWeek weekday = WEEKDAYS.get(parts[0]);
        if (weekday == null) {
            return null;
        }

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        int daysUntil = weekday.getValue() - today.getDayOfWeek().getValue();
        if (daysUntil < 0) {
            daysUntil += 7;
        }
        boolean timeSpecified = parts.length > 1;
        LocalTime time = timeSpecified ? parseTime(parts[1]) : LocalTime.MIDNIGHT;
        if (daysUntil == 0 && !today.atTime(time).isAfter(now)) {
            daysUntil = 7;
        }
        return new ParsedDateTime(today.plusDays(daysUntil).atTime(time), timeSpecified);
    }

    /** Parses one of the supported time formats. */
    private static LocalTime parseTime(String value) {
        String normalised = value.trim().replaceAll("\\s+", "").toUpperCase(Locale.ENGLISH);
        for (DateTimeFormatter formatter : TIME_FORMATTERS) {
            try {
                return LocalTime.parse(normalised, formatter);
            } catch (DateTimeParseException ignored) { }
        }
        throw invalidDateTime();
    }

    /** Normalises whitespace, ordinal day suffixes, and spaces before am/pm. */
    private static String normaliseInput(String input) {
        String value = input.trim().replaceAll("\\s+", " ")
                .replaceAll("(?i)(\\d+)(st|nd|rd|th)\\b", "$1");
        return value.replaceAll("(?i)(\\d{1,2}(?::\\d{2})?)\\s+(am|pm)\\b", "$1$2");
    }

    private static DateTimeFormatter strictFormatter(String pattern) {
        return new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(pattern)
                .toFormatter(Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);
    }

    private static DateTimeFormatter textFormatter(String pattern) {
        return strictFormatter(pattern);
    }

    private static IllegalArgumentException invalidDateTime() {
        return new IllegalArgumentException("Invalid date/time. Use datetime-help to view supported "
                + "date/time formats.");
    }

    /** Holds the parsed value and whether the user supplied a time. */
    private static final class ParsedDateTime {
        private final LocalDateTime value;
        private final boolean timeSpecified;

        private ParsedDateTime(LocalDateTime value, boolean timeSpecified) {
            this.value = value;
            this.timeSpecified = timeSpecified;
        }
    }
}
