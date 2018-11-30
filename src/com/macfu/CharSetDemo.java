package com.macfu;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.SortedMap;

/**
 * @Author: liming
 * @Date: 2018/11/29 11:27
 * @Description: 获取当前支持的字符集
 */
public class CharSetDemo {
    public static void main(String[] args) {
        SortedMap<String, Charset> stringCharsetSortedMap = Charset.availableCharsets();
        for(Map.Entry<String, Charset> entry : stringCharsetSortedMap.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

}
