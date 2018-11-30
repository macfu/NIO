package com.macfu;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liming
 * @Date: 2018/11/29 10:53
 * @Description: 文件锁
 */
public class FileLockDemo {
    public static void main(String[] args) throws Exception {
        // 文件路径
        File file = new File(File.separator + "Users" + File.separator + "baidu" + File.separator + "Desktop" + File.separator + "ssh.java");
        FileOutputStream outputStream = new FileOutputStream(file);
        // 获取文件通道
        FileChannel channel = outputStream.getChannel();
        // 尝试获取文件锁
        FileLock lock = channel.tryLock();
        // 如果已经获取文件锁
        if (lock != null) {
            System.out.println("******* 文件锁定300s ********");
            TimeUnit.SECONDS.sleep(300);
            // 释放锁
            lock.release();
        }
        channel.close();
        outputStream.close();
    }
}
