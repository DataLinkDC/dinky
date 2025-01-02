#!/bin/bash

DINKY_LIB_DIR=$1

echo -e "${GREEN}Start downloading the mysql driver package...${RESET}"
# Run the command to check whether the file exists
exec_result=$(ll "${DINKY_LIB_DIR}"/mysql-connector-j-8.4.0.jar)
if [ "$exec_result" ]; then
    echo -e "${YELLOW}The mysql driver package already exists, no need to download it again. Skip this step。${RESET}"
else
    download_file https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar "${DINKY_LIB_DIR}"
    echo -e "${GREEN}Download is complete, please verify. The downloaded file storage address is： ${DINKY_LIB_DIR}/mysql-connector-j-8.4.0.jar${RESET}"
    if [ -f "${DINKY_LIB_DIR}/mysql-connector-j-8.4.0.jar" ]; then
        echo -e "${GREEN}mysql driver package downloaded successfully。${RESET}"
    else
        echo -e "${RED}Mysql driver package download failed, please check the network or download manually。${RESET}"
        exit 1
    fi
    echo -e "${GREEN}After the verification is completed, subsequent installation and configuration operations can be performed as needed.。${RESET}"
fi
