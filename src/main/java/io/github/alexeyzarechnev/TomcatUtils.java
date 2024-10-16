package io.github.alexeyzarechnev;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.github.alexeyzarechnev.config.Config;

public class TomcatUtils
{
    @FunctionalInterface
    private static interface Util {
        void process(Config config, String[] args);
    }

    private static void setConfig(Config config, String[] args) {
        if (args.length != 3) {
            System.err.println("Incorrect options! Expected: tomcat-directory deployment-path");
        }
        try {
            config.setConfig(args[1], args[2]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void deploy(Config config, String[] args) {
        if (args.length != 1) {
            System.err.println("Incorrect options! Expected: no options");
        }
        try {
            Files.copy(config.getDeployment().toPath(), new File(new File(config.getTomcatDir(), "webapps"), 
                    config.getDeployment().getName()).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void changeDeployment(Config config, String[] args) {
        if (args.length != 2) {
            System.err.println("Incorrect options! Expected: deployment-path");
        }
        try {
            config.setConfig(config.getTomcatDir().getAbsolutePath(), args[1]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void startupScript(Config config, String[] args) {
        Config.OS os = config.os();
        File startupScript = new File(config.getTomcatDir(), "bin/startup" + os.scriptExtension());
        if (!startupScript.isFile()) {
            System.err.println("Startup script not found: " + startupScript.getAbsolutePath());
            return;
        }
        System.out.println(startupScript.getAbsolutePath());
    }

    private static void shutdownScript(Config config, String[] args) {
        Config.OS os = config.os();
        File shutdownScript = new File(config.getTomcatDir(), "bin/shutdown" + os.scriptExtension());
        if (!shutdownScript.isFile()) {
            System.err.println("Shutdown script not found: " + shutdownScript.getAbsolutePath());
            return;
        }
        System.out.println(shutdownScript.getAbsolutePath());
    }

    private static void printHelp(Config config, String[] args) {
        //TODO: write better help
        System.out.println("Help");
    }

    private static Map<List<String>, Util> utils = Map.of(
        Arrays.asList("-g", "--set-config"), TomcatUtils::setConfig,
        Arrays.asList("-d", "--deploy"), TomcatUtils::deploy,
        Arrays.asList("-c", "--change-deployment"), TomcatUtils::changeDeployment,
        Arrays.asList("-s", "--startup-script"), TomcatUtils::startupScript, 
        Arrays.asList("-t", "--shutdown-script"), TomcatUtils::shutdownScript,
        Arrays.asList("-h", "--help"), TomcatUtils::printHelp
    );

    private static Util parseArguments(String[] args) {
        for (Map.Entry<List<String>, Util> e : utils.entrySet()) {
            for (String flag : e.getKey()) {
                if (flag.equals(args[0])) {
                    return e.getValue();
                }
            }
        }
        return null;
    }

    public static void main(String[] args)
    {
        if (args.length == 0) {
            System.err.println("Invalid arguments! Usage: java -jar tomcat-utils.jar <method> [options]");
        }
        Config config = new Config(System.getProperty("java.class.path"));
        Util util = parseArguments(args);
        if (util == null) {
            System.err.println("Unsupported method: " + args[0] + " put -h, --help to see available methods");
        } else {
            util.process(config, args);
        }
        config.save();
    }
}
