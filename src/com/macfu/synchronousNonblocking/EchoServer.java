package com.macfu.synchronousNonblocking;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: liming
 * @Date: 2018/11/29 14:31
 * @Description: 同步非阻塞IO服务器端
 */
class SocketClientChannelThread implements Runnable {
    private SocketChannel clientChannel;
    private boolean flag = true;

    public SocketClientChannelThread(SocketChannel clientChannel) throws Exception {
        this.clientChannel = clientChannel;
        System.out.println("【客户端连接成功】，该客户端地址为：" + clientChannel.getRemoteAddress());
    }

    @Override
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(50);
        try {
            while (flag) {
                buffer.clear();
                int readCount = this.clientChannel.read(buffer);
                String readMessage = new String(buffer.array(), 0, readCount).trim();
                System.out.println("【服务器接收到消息】" + readMessage);
                String writeMessage = "【ECHO】" + readMessage + "\n";
                if ("exit".equals(readMessage)) {
                    writeMessage = "【exit】拜拜，下次再见";
                    this.flag = false;
                }
                buffer.clear();
                buffer.put(writeMessage.getBytes());
                buffer.flip();
                this.clientChannel.write(buffer);
            }
            this.clientChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
public class EchoServer {
    public static final int PORT = 9999;

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(PORT));
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务端启动程序，改程序在" + PORT + "端口上监听，等待客户端连接.......");
        int keySelect = 0;
        while ((keySelect = selector.select()) > 0) {
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
            while (selectionKeyIterator.hasNext()) {
                SelectionKey next = selectionKeyIterator.next();
                if (next.isAcceptable()) {
                    SocketChannel clientChannel = serverSocketChannel.accept();
                    if (clientChannel != null) {
                        executorService.submit(new SocketClientChannelThread(clientChannel));
                    }
                }
                selectionKeyIterator.remove();
            }
        }
        executorService.shutdown();
        serverSocketChannel.close();
    }
}
