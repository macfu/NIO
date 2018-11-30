package com.macfu;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author: liming
 * @Date: 2018/11/28 16:25
 * @Description: 使用channel实现文件读取
 */
public class FileChannelDemo {
    public static void main(String[] args) throws Exception {
//        读取文件
        File file = new File(File.separator + "Users" + File.separator + "baidu" + File.separator + "Desktop" + File.separator + "a.png");
        // 获取文件流
        FileInputStream input = new FileInputStream(file);
        // 获取文件通道
        FileChannel channel = input.getChannel();   //获取文件通道
        // 开辟缓存大小
        ByteBuffer buf = ByteBuffer.allocate(20);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 保存读取个数
        int count = 0;
        while ((count = channel.read(buf)) != -1) {
            buf.flip();
            while (buf.hasRemaining()) {
                bos.write(buf.get());
            }
            buf.clear();
        }
        System.out.println(new String(bos.toByteArray()));
        channel.close();
    }
}
