import au.com.bytecode.opencsv.CSVWriter;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static final String URL_PATTERN = "https://gpsfront.aliexpress.com/getRecommendingResults.do?widget_id=5547572&platform=pc&limit=%s&offset=%s&postback=0";

    // On Ali maximum page size 50 (but really count can be different from this).
    private static final int DEFAULT_SIZE = 50;

    private static final int PRODUCTS_COUNT = 39;

    public static void main(String[] args) throws IOException, ParseException {
        long start = new Date().getTime();
        String fileName = String.format("AliProducts-%d.csv", start);

        for (int i = 0; i < PRODUCTS_COUNT;) {
            int delta = PRODUCTS_COUNT - i;

            List<String[]> records;

            do {
                records = Parser.parse(load(Math.min(delta, DEFAULT_SIZE), i));
            }
            while (records == null);

            i += records.size();

            write(fileName, records);
        }

        long finish = new Date().getTime();

        System.out.printf("File name: %s%nTime: %f min.%n", fileName, (finish - start) / 1000 / 60d);
    }

    private static String load(int size, int offset) throws IOException {
        URL url = new URL(String.format(URL_PATTERN, size, offset));

        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

        System.out.println("Page loaded.");

        return reader.lines().collect(Collectors.joining("\n"));
    }

    private static void write(String fileName, List<String[]> records) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(fileName, true));

        for (String[] record : records) {
            writer.writeNext(record);

            // Since buffer size limited 8192 characters and page size over 8192 need clear buffer.
            writer.flush();
        }
    }
}
