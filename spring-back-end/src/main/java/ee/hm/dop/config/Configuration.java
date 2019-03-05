package ee.hm.dop.config;

import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class Configuration {

    private Environment env;

    public String getString(String s) {
        return env.getProperty(s);
    }

    public int getInt(String s) {
        String property = env.getProperty(s);
        return property != null ? Integer.valueOf(property) : 0;
    }

    public boolean getBoolean(String s) {
        String property = env.getProperty(s);
        return Boolean.parseBoolean(property);
    }

}
