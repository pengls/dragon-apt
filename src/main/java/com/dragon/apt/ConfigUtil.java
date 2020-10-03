package com.dragon.apt;

import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

    public static final String CONFIG_FILE_PATH = "dragon-apt.properties";
    private static Properties props;
    public static final String DEBUGGER_OPEN = "debugger.open";

    public static void init() {
        props = new Properties();
        try (InputStream in = ConfigUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE_PATH)) {
            props.load(in);
        } catch (Exception e) {
        }

        if (props.getProperty(DEBUGGER_OPEN) == null) {
            props.setProperty(DEBUGGER_OPEN, "false");
        }

    }

    public static boolean getDebuggerOpen() {
        return Boolean.parseBoolean(props.getProperty(DEBUGGER_OPEN));
    }


}
