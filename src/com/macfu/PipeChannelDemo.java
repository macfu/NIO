package com.macfu;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * @Author: liming
 * @Date: 2018/11/28 18:59
 * @Description: 管道流处理
 */
public class PipeChannelDemo {
    public static void main(String[] args) throws Exception {
        // 打开管道流
        Pipe pipe = Pipe.open();
        new Thread(()-> {
            // 打开管道流输入
            Pipe.SourceChannel sourceChannel = pipe.source();
            // 开辟初始化数组空间
            ByteBuffer buf = ByteBuffer.allocate(50);
            try {
                int count = sourceChannel.read(buf);
                buf.flip();
                System.out.println("{接收端}" + new String(buf.array(),0,count));
            } catch (IOException e) {
                e.printStackTrace();
            }
        },"接收线程").start();
        new Thread(() -> {
            // 要发送的消息
            String msg = "【" + Thread.currentThread().getName() + "】www.macfu.com" ;
            Pipe.SinkChannel sinkChannel = pipe.sink();
            ByteBuffer buf = ByteBuffer.allocate(50);
            buf.put(msg.getBytes());
            buf.flip();
            while (buf.hasRemaining()) {
                try {
                    sinkChannel.write(buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },"发送线程").start();
    }
}
