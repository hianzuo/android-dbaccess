package com.flyhand.core.remote;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * User: Ryan
 * Date: 11-3-17
 * Time: A.M. 10:38
 */
public class FormFile {
    /* 上传文件的数据 */
    private byte[] data;
    /* 文件名称 */
    private String fileName;
    /* 字段名称*/
    private String name;
    /* 内容类型 */
    private String contentType = "application/octet-stream"; //需要查阅相关的资料

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public FormFile(String name, String fileName) throws IOException {
        FileInputStream fis = null;
        try {
            fileName.replace('\\', '/');
            if (fileName.indexOf('/') != -1) {
                this.fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
            } else {
                this.fileName = fileName;
            }
            File file = new File(fileName);
            fis = new FileInputStream(file);
            this.data = new byte[fis.available()];
            fis.read(this.data, 0, fis.available());
            contentType = parseContentType(this.fileName);
            this.name = name;
        } finally {
            if (null != fis) {
                fis.close();
            }
        }
    }

    private static String parseContentType(String filname) {
        if (filname != null && filname.matches("(?i).*?\\.(png|jpg|gif|bmp)")) {
            return "image/" + filname.substring(filname.lastIndexOf(".") + 1).toLowerCase();
        } else {
            return "application/octet-stream";
        }
    }

    public byte[] getData() {
        return data;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }
}
