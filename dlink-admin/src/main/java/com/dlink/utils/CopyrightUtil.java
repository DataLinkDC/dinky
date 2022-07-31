/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.dlink.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


/**
 * 给指定类型文件，增加版权信息
 * dir: 文件夹(会递归获取该文件夹下符合 @fileEndWithStr 的目标文件)
 * fileEndWithStr: 文件类型后缀
 * copyright : 版权信息 Apache LICENSE-2.0
 */
public class CopyrightUtil {
	private static Logger log = LoggerFactory.getLogger(CopyrightUtil.class);
	public  static String dir = "d:\\your-dir";
	public  static String fileEndWithStr = ".less";
	public  static String copyright = "/*\n" +
			" *\n" +
			" *  Licensed to the Apache Software Foundation (ASF) under one or more\n" +
			" *  contributor license agreements.  See the NOTICE file distributed with\n" +
			" *  this work for additional information regarding copyright ownership.\n" +
			" *  The ASF licenses this file to You under the Apache License, Version 2.0\n" +
			" *  (the \"License\"); you may not use this file except in compliance with\n" +
			" *  the License.  You may obtain a copy of the License at\n" +
			" *\n" +
			" *     http://www.apache.org/licenses/LICENSE-2.0\n" +
			" *\n" +
			" *  Unless required by applicable law or agreed to in writing, software\n" +
			" *  distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
			" *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
			" *  See the License for the specific language governing permissions and\n" +
			" *  limitations under the License.\n" +
			" *\n" +
			" */\n\n\n";

	public static void main(String[] args) throws Exception {
		//java文件所在目录
		File file = new File(dir);
		addCopyright4Directory(file);
	}

	public static void addCopyright4Directory(File file) throws Exception {
		File[] files = file.listFiles();
		if (files == null || files.length == 0){
			return;
		}

		for (File f : files) {
			if (f.isFile()) {
				addCopyright4File(f);
				System.out.println("文件===" + f.getName());
			} else {
				System.out.println("目录==" + f.getName());
				addCopyright4Directory(f);
			}
		}
	}

	public static void addCopyright4File(File file) throws Exception {
		String fileName = file.getName();
		boolean endWith = fileName.endsWith(fileEndWithStr);
		if (!endWith) {
			log.info("This file is not "+ fileEndWithStr +" source file,filaName=" + fileName);
			return;
		}

		if (endWith) {
			// 版权字符串
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			String content = "";
			String lineSeperator ="\n";
			while ((line = br.readLine()) != null) {
				content += line + lineSeperator;
			}
			br.close();
	        //把拼接后的字符串写回去
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(copyright);
			fileWriter.write(content);
			fileWriter.close();	
		}

	}
}