package vn.ptit.library.web;

import java.time.*;
import java.time.format.DateTimeParseException;

public final class Ui {
    private Ui() {}

    public static String date(LocalDate value) {
        return value == null
                ? ""
                : value.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static String receiptStatus(String value) {
        if (value == null) return "";
        return switch (value) {
            case "BORROWED" -> "Đang mượn";
            case "RETURNED" -> "Đã trả";
            default -> value;
        };
    }

    public static String h(Object value) {
        return value == null
                ? ""
                : value.toString()
                        .replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                        .replace("\"", "&quot;")
                        .replace("'", "&#39;");
    }

    public static int id(String value) {
        try {
            int id = Integer.parseInt(value);
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Mã không hợp lệ.");
        }
    }

    public static LocalDate[] period(String start, String end) {
        if (start == null || end == null || start.isBlank() || end.isBlank())
            throw new IllegalArgumentException(
                    "Vui lòng chọn đầy đủ ngày bắt đầu và ngày kết thúc.");
        try {
            if (!start.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")
                    || !end.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"))
                throw new DateTimeParseException("format", start, 0);
            LocalDate s = LocalDate.parse(start), e = LocalDate.parse(end);
            if (s.getYear() < 1000 || e.getYear() > 9999)
                throw new IllegalArgumentException("Ngày nằm ngoài phạm vi hỗ trợ.");
            if (s.isAfter(e))
                throw new IllegalArgumentException(
                        "Ngày bắt đầu không được lớn hơn ngày kết thúc.");
            return new LocalDate[] {s, e};
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Ngày không hợp lệ. Vui lòng dùng định dạng YYYY-MM-DD.");
        }
    }
}
