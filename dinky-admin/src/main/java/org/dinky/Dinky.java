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

package org.dinky;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Dinky Starter
 *
 * @since 2021/5/28
 */
@EnableTransactionManagement
@SpringBootApplication(exclude = FreeMarkerAutoConfiguration.class)
@EnableCaching
@Slf4j
public class Dinky {
    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Dinky.class);
        app.run(args);
    }




    /**
     * 功能描述 消息摘要计算
     *
     * @param msg   平台推送消息
     * @param token 配置页面填写的token
     * @param nonce 平台生成的随机字符串
     * @return
     * @throws Exception
     */
    public static String signature(String msg, String token, String nonce) throws Exception {
        byte[] msgByte = msg.getBytes();
        byte[] tokenByte = token.getBytes();
        int tokenLen = tokenByte.length;
        int msgLen = msgByte.length;
        byte[] signature = new byte[tokenLen + 8 + msgLen];
        System.arraycopy(tokenByte, 0, signature, 0, tokenLen);
        System.arraycopy(nonce.getBytes(), 0, signature, tokenLen, 8);
        System.arraycopy(msgByte, 0, signature, tokenLen + 8, msgLen);
        MessageDigest mdInst = MessageDigest.getInstance("MD5");
        mdInst.update(signature);
        byte[] digest = mdInst.digest();
        return Base64.getEncoder().encodeToString(digest);
    }


    /**
     * 功能描述 推送消息解密
     *
     * @param encryptMsg 加密消息体对象
     * @param key        配置页面生成的AES秘钥
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String decrypt(String encryptMsg, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] encMsg = Base64.getDecoder().decode(encryptMsg);
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        //算法/模式/补码方式
        //CBC模式 向量必须是16个字节
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(key));
        byte[] msg = cipher.doFinal(encMsg);
        return new String(msg);
    }




}
