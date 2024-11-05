import com.fastcgi.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Main {
    private static final String RESPONSE_TEMPLATE = "Content-Type: application/json\n" +
            "Content-Length: %d\n\n%s";

    public static void main(String[] args) {
        while(new FCGIInterface().FCGIaccept() >= 0) {
            try {
                long startTime = System.nanoTime(); // Начало измерения времени

                HashMap<String, String> params = Params.parse(FCGIInterface.request.params.getProperty("QUERY_STRING"));
                int x = Integer.parseInt(params.get("x"));
                float y = Float.parseFloat(params.get("y"));
                float r = Float.parseFloat(params.get("r"));


                if (Validator.validateX(x) && Validator.validateY(y) && Validator.validateR(r)) {
                    double endTime = System.nanoTime();
                    double execTimeMillis = (endTime - startTime) / 1_000_000;

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String currentTime = LocalDateTime.now().format(formatter);

                    sendJson(String.format(
                            "{\"result\": %b, \"current_time\": \"%s\", \"exec_time\": \"%.6f\"}",
                            Checker.hit(x, y, r), currentTime, execTimeMillis
                    ));
                } else {
                    sendJson("{\"error\": \"invalid data\"}");
                }
            } catch (NumberFormatException e) {
                sendJson("{\"error\": \"wrong query param type\"}");
            } catch (NullPointerException e) {
                sendJson("{\"error\": \"missed necessary query param\"}");
            } catch (Exception e) {
                sendJson(String.format("{\"error\": \"%s\"}", e.toString()));
            }
        }
    }

    private static void sendJson(String jsonDump) {
        System.out.println(String.format(RESPONSE_TEMPLATE, jsonDump.getBytes(StandardCharsets.UTF_8).length, jsonDump));
    }
}
