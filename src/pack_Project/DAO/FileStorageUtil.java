package pack_Project.DAO;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Tiện ích lưu trữ dữ liệu vào các file .txt (CSV đơn giản).
 * Không dùng bất kỳ DBMS/JDBC nào.
 */
public class FileStorageUtil {

    private static final String DATA_DIR = "data";
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    static {
        // Đảm bảo thư mục data tồn tại
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static File getDataFile(String fileName) {
        return new File(DATA_DIR, fileName);
    }

    public static List<String> readAllLines(File file) throws IOException {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
    }

    public static void writeAllLines(File file, List<String> lines) throws IOException {
        Files.write(file.toPath(), lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void appendLine(File file, String line) throws IOException {
        Files.write(file.toPath(), (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /**
     * Sinh ID tự tăng dựa trên cột đầu tiên trong file (giả sử là số nguyên).
     */
    public static int generateNextId(File file) throws IOException {
        List<String> lines = readAllLines(file);
        int maxId = 0;
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(";", -1);
            try {
                int id = Integer.parseInt(parts[0]);
                if (id > maxId) maxId = id;
            } catch (NumberFormatException ignored) {
            }
        }
        return maxId + 1;
    }

    public static String formatDate(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(DATE_PATTERN).format(date);
    }

    public static Date parseDate(String s) throws ParseException {
        if (s == null || s.trim().isEmpty()) return null;
        return new SimpleDateFormat(DATE_PATTERN).parse(s.trim());
    }
}


