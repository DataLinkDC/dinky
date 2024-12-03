#!/bin/bash

EXTERNAL_CONNECTIVITY_CHECK_URL="www.baidu.com"

echo -e "${YELLOW}Checking whether your network can connect to the Internet properly (${EXTERNAL_CONNECTIVITY_CHECK_URL}) ...${RESET}"
if ! ping -c 1 ${EXTERNAL_CONNECTIVITY_CHECK_URL} &> /dev/null; then
    echo -e "${RED}Your network cannot connect to the Internet using ping mode, please check whether your network environment is normal, and the program will try to use curl mode to detect the network connection again...${RESET}"
    if ! curl -I -s --connect-timeout 5 ${EXTERNAL_CONNECTIVITY_CHECK_URL} -w '%{http_code}' | tail -n1 | grep "200" &> /dev/null; then
        echo -e "${RED}Your network cannot be connected to the Internet using curl mode, please check whether your network environment is normal。${RESET}"
        echo -e "${YELLOW}Note that in some network environments, firewalls or security policies may block ICMP requests (i.e., pings). If this happens, you can use curl to detect the network connection.${RESET}"
        exit 1
    else
        echo -e "${GREEN}Your network can use curl to connect to the Internet and proceed to the next step...${RESET}"
    fi
else
    echo -e "${GREEN}Your network can use ping to connect to the Internet and proceed to the next step...${RESET}"
fi