package com.macfu.synchronousNonblocking;


import com.macfu.InputUtils;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @Author: liming
 * @Date: 2018/11/30 11:37
 * @Description: 同步非阻塞IO客户端连接通道
 */
public class EchoClient {
    // 连接主机
    public static final String HOST = "localhost";
    // 连接端口
    public static final int PORT = 9999;

    public static void main(String[] args) throws Exception {
        // 获取客户端的SocketChannel对象
        SocketChannel clientChannel = SocketChannel.open();
        // 连接服务器
        clientChannel.connect(new InetSocketAddress(HOST, PORT));
        // 进行数据接受的对象定义
        ByteBuffer buffer = ByteBuffer.allocate(50);
        boolean flag = true;
        while (flag) {
            // 清空数据缓冲区
            buffer.clear();
            String msg = InputUtils.getString("请输入要发送的消息");
            // 将数据保存在缓冲区
            buffer.put(msg.getBytes());
            buffer.flip();
            clientChannel.write(buffer);
            // 在进行数据读取之前一定要进行缓冲区数据的清空
            buffer.clear();
            int readCount = clientChannel.read(buffer);
            // 读取完毕进行缓冲区的重设，为了获取内容
            buffer.flip();
            System.out.println(new String(buffer.array(), 0, readCount));
            if ("exit".equals(msg)) {
                flag = false;
            }
        }
        clientChannel.close();
    }
}
