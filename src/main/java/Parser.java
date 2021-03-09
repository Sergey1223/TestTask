import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Parser {
    private static final String SUCCESS_KEY = "success";
    private static final String RESULTS_KEY = "results";

    private static JSONParser jsonParser = new JSONParser();

    public static List<String[]> parse(String json) throws ParseException {
        JSONObject base = (JSONObject) jsonParser.parse(json);

        boolean success = ((Boolean) base.get(SUCCESS_KEY));

        if (!success) {
            return null;
        }

        JSONArray products = (JSONArray) base.get(RESULTS_KEY);

        List<String[]> result = new ArrayList<>(products.size());

        for (Object o : products) {
            JSONObject product = (JSONObject) o;

            Set<String> keys = product.keySet();

            result.add(keys.stream().map(key -> product.get(key).toString()).toArray(String[]::new));
        }

        System.out.printf("Page parsed (%s products).%n", result.size());

        return result;
    }
}
