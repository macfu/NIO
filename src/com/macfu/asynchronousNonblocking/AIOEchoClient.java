package com.macfu.asynchronousNonblocking;

import com.macfu.InputUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: liming
 * @Date: 2018/11/30 16:03
 * @Description: AIO客户端
 */
class ClientReadHandler implements CompletionHandler<Integer, ByteBuffer> {
    private CountDownLatch latch ;
    private AsynchronousSocketChannel clientChannel = null ; // 客户端的连接对象
    public ClientReadHandler(AsynchronousSocketChannel clientChannel,CountDownLatch latch) {
        this.clientChannel = clientChannel ;
        this.latch = latch ;
    }
    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip() ; // 重设缓冲区
        String receiveMessage = new String(buffer.array(), 0, buffer.remaining()); // 读取返回的内容
        System.err.println(receiveMessage); // 输出回应的处理数据
    }
    @Override
    public void failed(Throwable exp, ByteBuffer buffer) {
        System.out.println("对不起，发送出现了问题，该客户端被关闭 ...");
        try {
            this.clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.latch.countDown(); 	// 接触阻塞状态
    }
}

class ClientWriteHandler implements CompletionHandler<Integer, ByteBuffer> {
    private CountDownLatch latch ;
    private AsynchronousSocketChannel clientChannel = null ; // 客户端的连接对象
    public ClientWriteHandler(AsynchronousSocketChannel clientChannel,CountDownLatch latch) {
        this.clientChannel = clientChannel ;
        this.latch = latch ;
    }
    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        if (buffer.hasRemaining()) {	// 有数据要进行发送
            this.clientChannel.write(buffer,buffer,this) ;
        } else {	// 需要考虑到数据的读取问题
            ByteBuffer readBuffer = ByteBuffer.allocate(100) ;
            this.clientChannel.read(readBuffer,readBuffer,new ClientReadHandler(this.clientChannel,this.latch)) ;
        }
    }
    @Override
    public void failed(Throwable exp, ByteBuffer buffer) {
        System.out.println("对不起，发送出现了问题，该客户端被关闭 ...");
        try {
            this.clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.latch.countDown(); 	// 接触阻塞状态
    }

}

class AIOClientThread implements Runnable {// 定义客户端的线程类
    public static final String HOST = "localhost" ; // 连接主机
    public static final int PORT = 9999 ; // 设置绑定端口
    private CountDownLatch latch ;
    private AsynchronousSocketChannel clientChannel = null ; // 客户端的连接对象
    public AIOClientThread() throws Exception {	// 在构造方法里面进行服务主机的连接
        this.clientChannel = AsynchronousSocketChannel.open() ; // 打开客户端的Channel
        this.clientChannel.connect(new InetSocketAddress(HOST,PORT)) ; // 进行客户端连接
        this.latch = new CountDownLatch(1) ; // 做一个阻塞处理操作
    }
    @Override
    public void run() {
        try {
            this.latch.await(); // 等待处理
            this.clientChannel.close(); // 关闭客户端的连接处理了
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean sendMessge(String msg) {	// 实现消息的发送
        ByteBuffer buffer = ByteBuffer.allocate(100) ; // 设置一个定长的操作数据
        buffer.put(msg.getBytes()) ; // 保存要发送的内容
        buffer.flip() ; // 重设缓冲区要进行发送处理
        this.clientChannel.write(buffer,buffer,new ClientWriteHandler(this.clientChannel,this.latch)) ;
        if ("exit".equalsIgnoreCase(msg)) {
            return false ;
        }
        return true ;
    }
}

public class AIOEchoClient {

    public static void main(String[] args) throws Exception {
        AIOClientThread client = new AIOClientThread() ;
        new Thread(client).start(); // 启动客户端的线程
        while(client.sendMessge(InputUtils.getString("请输入要发送的消息："))) {
            ;
        }
    }

}
