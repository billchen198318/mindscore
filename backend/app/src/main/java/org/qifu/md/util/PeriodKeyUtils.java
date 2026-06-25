package org.qifu.md.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ServiceException;

public final class PeriodKeyUtils {
    public static final String DAY = "DAY";
    public static final String WEEK = "WEEK";
    public static final String MONTH = "MONTH";
    public static final String QUARTER = "QUARTER";
    public static final String HALFYEAR = "HALFYEAR";
    public static final String YEAR = "YEAR";

    private PeriodKeyUtils() { }

    public static boolean isValid(String type, String key) {
        try {
            parseStart(type, key);
            return true;
        } catch (ServiceException e) {
            return false;
        }
    }

    public static LocalDate parseStart(String periodType, String periodKey) throws ServiceException {
        String type = normalize(periodType);
        String key = StringUtils.trimToEmpty(periodKey);
        try {
            LocalDate date = switch (type) {
                case DAY -> LocalDate.parse(key, DateTimeFormatter.ISO_LOCAL_DATE);
                case WEEK -> parseWeek(key);
                case MONTH -> LocalDate.parse(key + "-01", DateTimeFormatter.ISO_LOCAL_DATE);
                case QUARTER -> parseQuarter(key);
                case HALFYEAR -> parseHalfYear(key);
                case YEAR -> parseYear(key);
                default -> throw new ServiceException("Unsupported period type: " + periodType);
            };
            if (!format(type, date).equals(key)) throw new ServiceException("Invalid period key: " + periodKey);
            return date;
        } catch (ServiceException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ServiceException("Invalid period key: " + periodKey);
        }
    }

    public static LocalDate next(String periodType, LocalDate current) throws ServiceException {
        return switch (normalize(periodType)) {
            case DAY -> current.plusDays(1);
            case WEEK -> current.plusWeeks(1);
            case MONTH -> current.plusMonths(1);
            case QUARTER -> current.plusMonths(3);
            case HALFYEAR -> current.plusMonths(6);
            case YEAR -> current.plusYears(1);
            default -> throw new ServiceException("Unsupported period type: " + periodType);
        };
    }

    public static LocalDate end(String type, String key) throws ServiceException {
        LocalDate start = parseStart(type, key);
        return next(type, start).minusDays(1);
    }

    public static String format(String periodType, LocalDate date) throws ServiceException {
        return switch (normalize(periodType)) {
            case DAY -> date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            case WEEK -> String.format("%04d-W%02d", date.get(WeekFields.ISO.weekBasedYear()), date.get(WeekFields.ISO.weekOfWeekBasedYear()));
            case MONTH -> String.format("%04d-%02d", date.getYear(), date.getMonthValue());
            case QUARTER -> String.format("%04d-Q%d", date.getYear(), ((date.getMonthValue() - 1) / 3) + 1);
            case HALFYEAR -> String.format("%04d-H%d", date.getYear(), date.getMonthValue() <= 6 ? 1 : 2);
            case YEAR -> String.valueOf(date.getYear());
            default -> throw new ServiceException("Unsupported period type: " + periodType);
        };
    }

    public static java.util.Date toDate(String type, String key) throws ServiceException {
        return java.util.Date.from(parseStart(type, key).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static String normalize(String type) {
        return StringUtils.trimToEmpty(type).toUpperCase(Locale.ROOT);
    }

    private static LocalDate parseWeek(String key) {
        if (!key.matches("\\d{4}-W\\d{2}")) throw new IllegalArgumentException();
        String[] value = key.split("-W");
        return LocalDate.of(Integer.parseInt(value[0]), 1, 4)
                .with(WeekFields.ISO.weekOfWeekBasedYear(), Integer.parseInt(value[1]))
                .with(WeekFields.ISO.dayOfWeek(), 1);
    }

    private static LocalDate parseQuarter(String key) {
        if (!key.matches("\\d{4}-Q[1-4]")) throw new IllegalArgumentException();
        String[] value = key.split("-Q");
        return LocalDate.of(Integer.parseInt(value[0]), (Integer.parseInt(value[1]) - 1) * 3 + 1, 1);
    }

    private static LocalDate parseHalfYear(String key) {
        if (!key.matches("\\d{4}-H[1-2]")) throw new IllegalArgumentException();
        String[] value = key.split("-H");
        return LocalDate.of(Integer.parseInt(value[0]), "1".equals(value[1]) ? 1 : 7, 1);
    }

    private static LocalDate parseYear(String key) {
        if (!key.matches("\\d{4}")) throw new IllegalArgumentException();
        return LocalDate.of(Integer.parseInt(key), 1, 1);
    }
}
