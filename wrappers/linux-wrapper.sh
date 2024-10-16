#!/bin/bash

# put here absolute path to the tomcat-utils.jar
utils=""

tomcat-config() {
    java -jar $utils --set-config $1 $2
}

tomcat-change-deployment() {
    java -jar $utils --change-deployment $1
}

tomcat-start() {
    java -jar $utils --startup-script | bash
}

tomcat-stop() {
    java -jar $utils --shutdown-script | bash
}

tomcat-deploy() {
    java -jar $utils --deploy
}

tomcat-run() {
    tomcat-deploy
    tomcat-start
}

tomcat-update() {
    tomcat-stop
    tomcat-run
}

tomcat-c-run() {
    mvn clean package
    tomcat-run
}

tomcat-c-update() {
    mvn clean package
    tomcat-update
}

