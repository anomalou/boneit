package org.anomalou.controller;

import java.io.*;
import java.util.Properties;

public class PropertiesController extends Controller{
    private final String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath(); //TODO may null pointer exception source
    private final String propertiesPath = rootPath + "config.properties";

    private final Properties properties;

    public PropertiesController(){
        properties = new Properties();

        loadPropertiesFile();
    }

    public int getInt(String property){
        String value = properties.getProperty(property);

        if(value != null){
            logger.fine(String.format("Property [%s=%s] was loaded!", property, value));
            return Integer.valueOf(value);
        }

        logger.warning(String.format("Property [%s] was not found!", property));

        return 0;
    }

    public String getString(String property){
        String value = properties.getProperty(property);

        if(value != null){
            logger.fine(String.format("Property [%s=%s] was loaded!", property, value));
            return value;
        }

        logger.warning(String.format("Property [%s] was not found!", property));

        return "NaN";
    }

    private void writeDefaultProperties(FileOutputStream fileOutputStream) throws IOException{
        properties.put("ruler.width", "10");
        properties.put("ruler.height", "10");
        properties.put("ruler.corner.l.offset.x", "30");
        properties.put("ruler.corner.l.offset.y", "10");
        properties.put("ruler.corner.u.offset.x", "1");
        properties.put("ruler.corner.u.offset.y", "10");
        properties.put("ruler.offset.x", "1");
        properties.put("ruler.offset.y", "10");
        properties.put("scale.min", "1");
        properties.put("scale.max", "50");
        properties.store(fileOutputStream, "Bone-it configuration file");
        fileOutputStream.close();
    }

    private void loadPropertiesFile(){
        File file = new File(propertiesPath);

        if(!file.exists()){
            try{
                file.createNewFile();
                writeDefaultProperties(new FileOutputStream(file));
            }catch (FileNotFoundException exception){
                logger.severe(String.format("File with path %s not founded!", propertiesPath));
            }catch (IOException exception){
                logger.severe(String.format("IO exception! Message:\n%s", exception.getMessage()));
            }
        }else{
            try{
                properties.load(new FileInputStream(file));
                logger.fine("Properties file loaded successfully!");
            }catch (IOException exception){
                logger.severe(String.format("Properties file with path \"%s\" was not found! Check write/read rights! Error message:\n%s", propertiesPath, exception.getMessage()));
            }
        }
    }


}
