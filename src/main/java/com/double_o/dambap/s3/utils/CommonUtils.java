package com.double_o.dambap.s3.utils;

import java.util.UUID;

public class CommonUtils {

    public static final String FILE_EXTENSION_SEPARATOR = ".";

    public static String buildFileName(String originalFileName) {
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        String fileExtension = originalFileName.substring(fileExtensionIndex)
                .toLowerCase(); // 확장자 소문자로 변환
        String uuid = UUID.randomUUID().toString();

        return uuid + fileExtension;
    }
}