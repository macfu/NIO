package com.macfu;

import java.nio.ByteBuffer;

/**
 * @Author: liming
 * @Date: 2018/11/28 16:16
 * @Description: buffer缓冲区
 */
public class BufferDemo {
    public static void main(String[] args) {
        String str = "www.google.com";  //长度为14
        ByteBuffer buf = ByteBuffer.allocate(20);   //该缓冲区的容量为20
        System.out.println("【没有存放数据】:capacity = " + buf.capacity() + ",limit = " + buf.limit() + ",position =" + buf.position());
        buf.put(str.getBytes());
        System.out.println("【保存数据】:capacity = " + buf.capacity() + ",limit = " + buf.limit() + ",position =" + buf.position());
        buf.flip();
        System.out.println("【刷新数据】:capacity = " + buf.capacity() + ",limit = " + buf.limit() + ",position =" + buf.position());
    }
}
