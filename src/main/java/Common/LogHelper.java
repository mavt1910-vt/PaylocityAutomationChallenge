package Common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogHelper {

    private static final Logger log = LoggerFactory.getLogger(LogHelper.class);
    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static void info(String message) {
        log.info(message);
    }

    public static void debug(String message) {
        log.debug(message);
    }

    public static void warn(String message) {
        log.warn(message);
    }

    public static void error(String message) {
        log.error(message);
    }

    public static void pretty(Object obj) {
        try {
            String json = mapper.writeValueAsString(obj);
            log.info("\n{}", json);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to JSON", e);
        }
    }

    public static void prettyJson(String jsonString) {
        try {
            Object json = mapper.readValue(jsonString, Object.class);
            log.info("\n{}", mapper.writeValueAsString(json));
        } catch (Exception e) {
            log.error("Invalid JSON string", e);
        }
    }
}
