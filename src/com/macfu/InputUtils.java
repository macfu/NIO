package com.macfu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Author: liming
 * @Date: 2018/11/30 11:30
 * @Description:
 */
public class InputUtils {
    private static BufferedReader KEYBOARD_INPUT = new BufferedReader(new InputStreamReader(System.in));

    private InputUtils() {
    }

    public static String getString(String promot) throws IOException {
        boolean flag = true;
        String str = null;
        while (flag) {
            System.out.println(promot);
            str = KEYBOARD_INPUT.readLine();
            if (str == null || "".equals(str)) {
                System.out.println("输入数据有误，请重新输入");
            } else {
                flag = false;
            }
        }
        return str;
    }

}
