package org.anomalou.controller;

import java.io.*;
import java.net.URL;
import java.util.Properties;

public class PropertiesController extends Controller {
    private final URL propertiesPath = getClass().getResource("config.properties"); //TODO move it to resources

    private final Properties properties;

    public PropertiesController() {
        properties = new Properties();

        loadPropertiesFile();
    }

    public int getInt(String property) {
        String value = properties.getProperty(property);

        if (value != null) {
            logger.fine(String.format("Property [%s=%s] was loaded!", property, value));
            return Integer.parseInt(value);
        }

        logger.warning(String.format("Property [%s] was not found!", property));

        return 0;
    }

    public String getString(String property) {
        String value = properties.getProperty(property);

        if (value != null) {
            logger.fine(String.format("Property [%s=%s] was loaded!", property, value));
            return value;
        }

        logger.warning(String.format("Property [%s] was not found!", property));

        return "NaN";
    }

    private void loadPropertiesFile() {
        try {
            InputStream inputStream;
            try{
                inputStream = new FileInputStream("config.properties");
            }catch (FileNotFoundException exception){
                inputStream = getClass().getResourceAsStream("default_config.properties");
                File file = new File("config.properties");
                file.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(inputStream.readAllBytes());
                outputStream.close();
                FileInputStream fileInputStream = new FileInputStream(file);
                properties.load(fileInputStream);
                fileInputStream.close();
            }

            properties.load(inputStream);
            logger.fine("Properties file loaded successfully!");
        } catch (Exception exception) {
            logger.warning(exception.getMessage());
        }
    }


}
