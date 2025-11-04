package com.husky.algo.other;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Base64;
import java.util.Enumeration;

/**
 * P12Extractor.java
 * 解析 .p12 文件，并导出私钥和公钥证书到独立的 PEM 文件中。
 */
public class P12Extractor {

    // PEM 文件的行分隔符
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * 将字节数组写入文件
     */
    private static void writeToFile(String filename, byte[] content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(content);
            System.out.println("成功生成文件: " + filename);
        }
    }

    /**
     * 将私钥编码为 PKCS#8 PEM 格式并写入文件
     */
    private static void writePrivateKeyToFile(PrivateKey key, String filename) throws IOException {
        String privateKeyPEM = "-----BEGIN PRIVATE KEY-----" + LINE_SEPARATOR
                + Base64.getEncoder().encodeToString(key.getEncoded()) + LINE_SEPARATOR
                + "-----END PRIVATE KEY-----" + LINE_SEPARATOR;
        writeToFile(filename, privateKeyPEM.getBytes());
    }

    /**
     * 将证书编码为 X.509 PEM 格式并写入文件
     */
    private static void writeCertificateToFile(Certificate cert, String filename) throws IOException, CertificateEncodingException {
        String certPEM = "-----BEGIN CERTIFICATE-----" + LINE_SEPARATOR
                + Base64.getEncoder().encodeToString(cert.getEncoded()) + LINE_SEPARATOR
                + "-----END CERTIFICATE-----" + LINE_SEPARATOR;
        writeToFile(filename, certPEM.getBytes());
    }

    /**
     * 将公钥编码为 PEM 格式并写入文件
     */
    private static void writePublicKeyToFile(PublicKey publicKey, String filename) throws IOException {
        String publicKeyPEM = "-----BEGIN PUBLIC KEY-----" + LINE_SEPARATOR
                + Base64.getEncoder().encodeToString(publicKey.getEncoded()) + LINE_SEPARATOR
                + "-----END PUBLIC KEY-----" + LINE_SEPARATOR;
        writeToFile(filename, publicKeyPEM.getBytes());
    }


    public static void extractP12(String p12FilePath, String p12FileName, String storePassword, String aliasPassword) {
        String targetAlias = null; // 用于存储找到的密钥对别名
        try {
            // 1. 加载 KeyStore 实例
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            char[] storePwdChars = storePassword.toCharArray();

            try (FileInputStream fis = new FileInputStream(p12FilePath + p12FileName)) {
                keyStore.load(fis, storePwdChars);
            }

            // 2. 查找密钥对别名
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (keyStore.isKeyEntry(alias)) {
                    targetAlias = alias;
                    System.out.println("找到密钥对别名: " + targetAlias);
                    break;
                }
            }

            if (targetAlias == null) {
                System.err.println("错误: 在 KeyStore 中未找到任何密钥对 (Key Entry)。");
                return;
            }

            // 3. 提取私钥
            char[] aliasPwdChars = aliasPassword.toCharArray();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(targetAlias, aliasPwdChars);

            // 4. 提取公钥证书
            Certificate cert = keyStore.getCertificate(targetAlias);
            PublicKey publicKey = cert.getPublicKey();

            // 5. 写入文件
            String baseName = p12FilePath + "extracted_" + targetAlias;

            // 写入私钥 (PKCS#8 PEM 格式)
            writePrivateKeyToFile(privateKey, baseName + "_private.key");

            // 写入公钥证书 (X.509 PEM 格式)
            writeCertificateToFile(cert, baseName + "_certificate.pem");

            // 写入公钥 (从证书中提取)
            writePublicKeyToFile(publicKey, baseName + "_public.key");

            System.out.println("\n--- 提取完成 ---");

        } catch (java.io.FileNotFoundException e) {
            System.err.println("错误: 文件未找到。请检查路径: " + p12FilePath);
        } catch (java.security.UnrecoverableKeyException e) {
            System.err.println("错误: 私钥密码错误或无法恢复。请检查 'aliasPassword'。");
        } catch (java.security.KeyStoreException e) {
            System.err.println("错误: KeyStore 密码错误或 KeyStore 文件格式无效。请检查 'storePassword'。");
        } catch (Exception e) {
            System.err.println("解析或写入文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // --- 请修改以下三个参数 ---
        String filePath = "C:\\Users\\"; // 替换为您的 .p12 文件路径
        String fileName = "test.p12";      // 替换为您的 .p12 文件名称
        String storePwd = "test";      // 替换为 KeyStore 的密码
        String aliasPwd = "test";      // 替换为私钥的密码
        // ------------------------

        extractP12(filePath, fileName, storePwd, aliasPwd);
    }
}