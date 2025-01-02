#!/bin/bash

CURRENT_FLINK_FULL_VERSION=$1
FLINK_VERSION_SCAN=$2
DINKY_TMP_DIR=$3
EXTENDS_HOME=$4
DINKY_HOME=$5

echo -e "${GREEN}====================== Flink dependency initialization ======================${RESET}"

echo -e "${BLUE}Parameters: The current Flink version is：${CURRENT_FLINK_FULL_VERSION}，The scanned Flink version is：${FLINK_VERSION_SCAN} ，The temporary directory is：${DINKY_TMP_DIR} ，The expansion package directory is：${EXTENDS_HOME} ，Dinky The root directory is：${DINKY_HOME}${RESET}"

if [ -z "$CURRENT_FLINK_FULL_VERSION" ] || [ -z "$FLINK_VERSION_SCAN" ] || [ -z "$DINKY_TMP_DIR" ] || [ -z "$EXTENDS_HOME" ] || [ -z "$DINKY_HOME" ]; then
  echo -e "${RED}Parameter error, please check!${RESET}"
  exit 1
fi

FLINK_STORE_DIR=${DINKY_TMP_DIR}/flink-download

mkdir -p ${FLINK_STORE_DIR}

if [ -f "${FLINK_STORE_DIR}/flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz" ]; then
  echo -e "${YELLOW}${FLINK_STORE_DIR} ALREADY EXISTS flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz file，To ensure completeness, delete first ${FLINK_STORE_DIR}/flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz Download the file again${RESET}"
  rm -rf ${FLINK_STORE_DIR}/flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz
  if [ -d "${FLINK_STORE_DIR}/flink-${CURRENT_FLINK_FULL_VERSION}" ]; then
    echo -e "${YELLOW}The flink directory already exists, delete it ${FLINK_STORE_DIR}/flink-${CURRENT_FLINK_FULL_VERSION}"
    rm -rf ${FLINK_STORE_DIR}/flink-${CURRENT_FLINK_FULL_VERSION}
  fi
fi

try_mirrors_download_file() {
    local tsinghua_url="https://mirrors.tuna.tsinghua.edu.cn/apache/flink/flink-${CURRENT_FLINK_FULL_VERSION}/flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz"
    local aliyun_url="https://mirrors.aliyun.com/apache/flink/flink-${CURRENT_FLINK_FULL_VERSION}/flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz"
    local apache_url="https://archive.apache.org/dist/flink/flink-${CURRENT_FLINK_FULL_VERSION}/flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz"
    echo -e "${GREEN}Start downloading the Flink-${FLINK_VERSION_SCAN} installation package... Store it in the ${FLINK_STORE_DIR} directory${RESET}"

    exec_tsinghua_result=$(download_file "$tsinghua_url" "${FLINK_STORE_DIR}")
    if [ ! "$exec_tsinghua_result" ]; then
        echo -e "${BLUE}The tsinghua address of the currently downloaded Flink installation package is：${tsinghua_url}${RESET}"
        return 0
    else
        echo -e "${YELLOW}Failed to download from Tsinghua mirror, try downloading from Aliyun mirror...${RESET}"
        exec_aliyun_result=$(download_file "$aliyun_url" "${FLINK_STORE_DIR}")
        if [ "$exec_aliyun_result" ]; then
            echo -e "${BLUE}The aliyun address of the currently downloaded Flink installation package is：${aliyun_url}${RESET}"
            return 0
        else
            echo -e "${YELLOW}Failed to download from Aliyun mirror too, try downloading from Apache official source...${RESET}"

            exec_apache_result=$(download_file "$apache_url" "${FLINK_STORE_DIR}")
            if [ "$exec_apache_result" ]; then
                echo -e "${RED}Downloading from Apache official source also failed, please check the network or download manually。${RESET}"
                return 1
            else
                echo -e "${BLUE}The apache address of the currently downloaded Flink installation package is：${apache_url}${RESET}"
                return 0
            fi
        fi
    fi
}

if ! try_mirrors_download_file; then
    exit 0
fi


echo -e "${GREEN}Flink installation package download completed。${RESET}"
echo -e "\n${GREEN}===============================================================${RESET}\n"
echo -e "${GREEN}Start decompressing the Flink installation package...${RESET}"
tar -zxvf ${FLINK_STORE_DIR}/flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz -C ${FLINK_STORE_DIR}/
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Flink installation package decompression completed。${RESET}"
else
    echo -e "${RED}Flink installation package failed to decompress, please check。${RESET}"
    exit 1
fi

echo -e "\n${GREEN}===============================================================${RESET}\n"

flink_dir_tmp=$(ls -n ${FLINK_STORE_DIR} | grep '^d' | grep flink | awk '{print $9}')
full_flink_dir_tmp="${FLINK_STORE_DIR}/${flink_dir_tmp}"
echo -e "${BLUE}Unzipped directory name：${full_flink_dir_tmp}${RESET}"



echo -e "${GREEN}Process ${full_flink_dir_tmp}/lib/flink-table-planner-loader* file...${RESET}"
rm -rf ${full_flink_dir_tmp}/lib/flink-table-planner-loader*
echo -e "${GREEN}Processing completed。${RESET}"

echo -e "${GREEN}Process ${full_flink_dir_tmp}/opt/flink-table-planner_2.12-*.jar file...${RESET}"
mv ${full_flink_dir_tmp}/opt/flink-table-planner_2.12-*.jar ${full_flink_dir_tmp}/lib/
echo -e "${GREEN}Processing completed。${RESET}"

echo -e "${GREEN}Process flink jar dependencies into dinky...${RESET}"
cp -r ${full_flink_dir_tmp}/lib/*.jar ${EXTENDS_HOME}/flink${FLINK_VERSION_SCAN}/
echo -e "${GREEN}jar dependency processing completed。${RESET}"

echo -e "${GREEN}Process flink-sql-client ...${RESET}"
cp -r ${full_flink_dir_tmp}/opt/flink-sql-client-*.jar ${EXTENDS_HOME}/flink${FLINK_VERSION_SCAN}/
echo -e "${GREEN}Processing completed。${RESET}"

echo -e "${GREEN}Process flink-cep-scala ...${RESET}"
cp -r ${full_flink_dir_tmp}/opt/flink-cep-scala*.jar ${EXTENDS_HOME}/flink${FLINK_VERSION_SCAN}/
echo -e "${GREEN}Processing completed。${RESET}"

echo -e "${GREEN}Process flink-queryable-state-runtime ...${RESET}"
cp -r ${full_flink_dir_tmp}/opt/flink-queryable-state-runtime*.jar ${EXTENDS_HOME}/flink${FLINK_VERSION_SCAN}/
echo -e "${GREEN}Processing completed。${RESET}"

echo -e "${GREEN}Process flink-state-processor-api ...${RESET}"
cp -r ${full_flink_dir_tmp}/opt/flink-state-processor-api*.jar ${EXTENDS_HOME}/flink${FLINK_VERSION_SCAN}/
echo -e "${GREEN}Processing completed。${RESET}"

echo -e "${GREEN}Process flink-s3-fs-presto ...${RESET}"
cp -r ${full_flink_dir_tmp}/opt/flink-s3-fs-presto*.jar ${EXTENDS_HOME}/flink${FLINK_VERSION_SCAN}/
echo -e "${GREEN}Processing completed。${RESET}"

echo -e "${GREEN} ================= List files in the ${EXTENDS_HOME}/flink${FLINK_VERSION_SCAN}/ directory ==============${RESET}"
ls -l ${EXTENDS_HOME}/flink${FLINK_VERSION_SCAN}/

echo -e "${YELLOW}Please check the above dependent files。${RESET}"

rm -rf ${FLINK_STORE_DIR}/flink-${CURRENT_FLINK_FULL_VERSION}*

echo -e "${GREEN}The basic dependency processing is completed, please perform subsequent operations according to the actual situation.${RESET}"
