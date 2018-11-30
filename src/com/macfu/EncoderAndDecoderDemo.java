package com.macfu;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * @Author: liming
 * @Date: 2018/11/29 14:18
 * @Description: 编码和解码（在严格的网络传输中对于编码的处理都建议使用Charset类完成）
 */
public class EncoderAndDecoderDemo {
    public static void main(String[] args) throws Exception {
        // 创建一个指定编码的处理Charset
        Charset charset = Charset.forName("UTF-8");
        // 获取编码类对象
        CharsetEncoder encoder = charset.newEncoder();
        // 获取解码类对象
        CharsetDecoder decoder = charset.newDecoder();
        String str = "你是我的唯一";
        CharBuffer buf = CharBuffer.allocate(20);
        // 像缓冲区保存数据
        buf.put(str);
        // 进行缓冲区的重置
        buf.flip();
        // 进行编码处理
        ByteBuffer buffer = encoder.encode(buf);
        // 对字节缓冲区中的数据进行解码
        System.out.println(decoder.decode(buffer));
    }
}
