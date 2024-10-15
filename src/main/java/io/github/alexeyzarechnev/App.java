package io.github.alexeyzarechnev;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import io.github.alexeyzarechnev.config.Config;

public class App 
{
    public static void main(String[] args)
    {
        Config config;
        if (args.length > 0) {
            config = new Config(System.getProperty("java.class.path"));
            try {
                config.setConfig(args[0], args[1]);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            config = new Config(System.getProperty("java.class.path"));
            try {
                Files.copy(config.getDeployment().toPath(), new File(new File(config.getTomcatDir(), "webapps"), 
                        config.getDeployment().getName()).toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }   

        config.save();
    }
}
