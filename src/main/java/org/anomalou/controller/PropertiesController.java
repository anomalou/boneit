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

    private void writeDefaultProperties(FileOutputStream fileOutputStream) throws IOException {
        properties.put("ruler.width", "10");
        properties.put("ruler.height", "10");
        properties.put("ruler.corner.l.offset.x", "0");
        properties.put("ruler.corner.l.offset.y", "25");
        properties.put("ruler.corner.u.offset.x", "30");
        properties.put("ruler.corner.u.offset.y", "10");
        properties.put("ruler.offset.x", "1");
        properties.put("ruler.offset.y", "10");
        properties.put("scale.min", "1");
        properties.put("scale.max", "50");
        properties.put("preview.width", "20");
        properties.put("preview.height", "20");
        properties.put("toolicon.scale", "25");
        properties.store(fileOutputStream, "Bone-it configuration file");
        fileOutputStream.close();
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
