package io.github.alexeyzarechnev.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Config {

    private static final String CONFIG_FILENAME = "config.cnf";

    private File tomcatDir;
    private File deployment;
    private final String currentDir;
    private final String fileSeparator = System.getProperty("file.separator");

    private void defaultInit() {
        tomcatDir = null;
        deployment = null;
    }
    
    public Config (String currentDir) {
        this.currentDir = currentDir.substring(0, currentDir.lastIndexOf(fileSeparator));
        File lastLaunchData = new File(this.currentDir, CONFIG_FILENAME);
        if (!lastLaunchData.isFile()) {
            defaultInit();
        } else {
            try (Scanner input = new Scanner(lastLaunchData)) {
                tomcatDir = new File(input.next());
                deployment = new File(input.next());
                if (!tomcatDir.isDirectory() || !deployment.isFile()) {
                    defaultInit();
                }
            } catch (Exception e) {
                e.printStackTrace();
                defaultInit();
            }
        }
    }

    public void save() {
        if (tomcatDir == null || deployment == null) {
            return;
        }
        
        File save = new File(currentDir, CONFIG_FILENAME);
        if (!save.isFile()) {
            try {
                save.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try (FileWriter out = new FileWriter(save)) {
            out.write(tomcatDir.getAbsolutePath());
            out.write(System.lineSeparator());
            out.write(deployment.getAbsolutePath());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }

    public void setConfig(String tomcatDirPath, String deploymentPath) throws FileNotFoundException {
        tomcatDir = new File(tomcatDirPath);
        deployment = new File(deploymentPath);
        if (!tomcatDir.isDirectory() || !deployment.isFile()) {
            defaultInit();
            throw new FileNotFoundException("Invalid config tomcat: " + tomcatDirPath + "deployment: "
                         + deploymentPath);
        }
    }
    
    public File getTomcatDir() {
        return tomcatDir;
    }

    public File getDeployment() {
        return deployment;
    }

    public String fileSeparator() {
        return fileSeparator;
    }

    public static void main (String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar Config.jar <tomcatDir> <deployment>");
            return;
        }
        Config config = new Config(System.getProperty("java.class.path"));
        try {
            config.setConfig(args[0], args[1]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        config.save();
    }
}
