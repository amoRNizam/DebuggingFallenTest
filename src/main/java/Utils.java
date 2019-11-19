import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Utils {

    public static Map<String, String> getProperty() {
        String location = "src\\main\\resources\\settings.ini";
        Map<String, String> properties = new HashMap<>();
        Properties props = new Properties();
        if (location != null) {
            try {
                props.load(new FileInputStream(new File(location)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            props.forEach((key, value) -> properties.put(key.toString(), value.toString()));
        }
        props.clear();
        return properties;
    }
}
