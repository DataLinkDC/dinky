#!/bin/bash

EXTENDS_HOME=$1


echo -e "${GREEN}====================== Hadoop dependency initialization ======================${RESET}"

read -p "Please select the Hadoop-uber version to download (enter 2 or 3):" hadoop_uber_version
hadoop_uber_version=$(echo "$hadoop_uber_version" | tr '[:upper:]' '[:lower:]' | tr -d '[:space:]')

case $hadoop_uber_version in
    2)
        echo -e "${YELLOW}Start downloading Hadoop-uber 2 version package...${RESET}"
        if [ -f "$EXTENDS_HOME/flink-shaded-hadoop-2-uber-2.8.3-10.0.jar" ]; then
            echo -e "${YELLOW}The flink-shaded-hadoop-2-uber-2.8.3-10.0.jar file already exists and there is no need to download it again.${RESET}"
        else
          download_url="https://repo1.maven.org/maven2/org/apache/flink/flink-shaded-hadoop-2-uber/2.8.3-10.0/flink-shaded-hadoop-2-uber-2.8.3-10.0.jar"
          download_file "$download_url" "$EXTENDS_HOME"
        fi
        ;;
    3)
        if [ -f "$EXTENDS_HOME/flink-shaded-hadoop-3-uber-3.1.1.7.2.9.0-173-9.0.jar" ]; then
            echo -e "${YELLOW}The flink-shaded-hadoop-3-uber-3.1.1.7.2.9.0-173-9.0.jar file already exists and there is no need to download it again.${RESET}"
        else
           echo -e "${YELLOW}Start downloading Hadoop-uber 3 version package...${RESET}"
           download_url="https://repository.cloudera.com/artifactory/cloudera-repos/org/apache/flink/flink-shaded-hadoop-3-uber/3.1.1.7.2.9.0-173-9.0/flink-shaded-hadoop-3-uber-3.1.1.7.2.9.0-173-9.0.jar"
           download_file "$download_url" "$EXTENDS_HOME"
        fi
        ;;
    *)
        echo -e "${RED}The entered version number is incorrect, please re-run the script to select the correct version.${RESET}"
        ;;
esac
