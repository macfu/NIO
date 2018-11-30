package com.macfu.asynchronousNonblocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

class EchoHandler implements CompletionHandler<Integer,ByteBuffer> {	// 实现的是一个回调处理
    private AsynchronousSocketChannel clientChannel ; // 客户端对象
    private boolean exit = false ; // 回应是否结束，如果为exit = true表示不再接收
    public EchoHandler(AsynchronousSocketChannel clientChannel) {
        this.clientChannel = clientChannel ;
    }
    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip() ; // 如果要读取数据则应该首先重置Buffer缓冲区
        String readMessage = new String(buffer.array(), 0, buffer.remaining()).trim(); // 接收读取的数据
        System.err.println("【服务器端读取到数据】" + readMessage); // 信息提示
        String resultMessage = "【ECHO】" + readMessage ; // 保存数据的回应处理信息
        if ("exit".equalsIgnoreCase(readMessage)) {
            resultMessage = "【EXIT】拜拜，下次再见！" ; // 要回应的处理内容
            this.exit = true ; // 输出完信息之后不再需要数据读取了
        }
        this.echoWrite(resultMessage); // 回应处理
    }
    @Override
    public void failed(Throwable exp, ByteBuffer buffer) {
        this.closeClient();
    }
    private void closeClient() {
        System.out.println("客户端连接有错误，中断与此客户端的处理！");
        try {
            this.clientChannel.close();	// 关闭客户端的信息处理操作
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void echoWrite(String result) {	// 实现数据的回应处理
        ByteBuffer buffer = ByteBuffer.allocate(100) ;	// 设置回应缓冲区
        buffer.put(result.getBytes()) ; // 信息回应处理
        buffer.flip() ; // 准备进行内容的输出了
        this.clientChannel.write(buffer, buffer, new CompletionHandler<Integer,ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if (buffer.hasRemaining()) {	// 有数据的情况下才进行写入
                    EchoHandler.this.clientChannel.write(buffer, buffer, this); // 进行数据的输出操作
                } else {
                    if (EchoHandler.this.exit == false) {	// 还可以继续读取
                        ByteBuffer readBuffer = ByteBuffer.allocate(100) ;
                        EchoHandler.this.clientChannel.read(readBuffer, readBuffer, new EchoHandler(EchoHandler.this.clientChannel));
                    }
                }
            }
            @Override
            public void failed(Throwable exp, ByteBuffer buffer) {
                EchoHandler.this.closeClient();
            }});
    }
}
class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AIOServerThread> {

    @Override
    public void completed(AsynchronousSocketChannel channel, AIOServerThread aioThread) {
        aioThread.getServerChannel().accept(aioThread, this); // 接收连接
        ByteBuffer buffer = ByteBuffer.allocate(100) ; // 开一个接收的缓冲区
        channel.read(buffer, buffer, new EchoHandler(channel)); // 应该创建另外一个异步处理操作实现回应处理
    }
    @Override
    public void failed(Throwable exp, AIOServerThread aioThread) {
        System.out.println("服务器的连接处理失败... ...");
        aioThread.getLatch().countDown();  // 减1，解除阻塞状态
    }

}

class AIOServerThread implements Runnable {	// 定义一个AIO的服务处理线程
    private static final int PORT = 9999 ; // 监听端口
    private CountDownLatch latch = null ; // 保证服务端线程执行完毕后结束
    private AsynchronousServerSocketChannel serverChannel = null ;	// 需要得到一个异步服务的处理Channel
    public AIOServerThread() throws Exception {	// 在构造方法里面为相应的类实例化
        this.latch = new CountDownLatch(1) ; // 服务端线程只有一个
        this.serverChannel = AsynchronousServerSocketChannel.open() ; // 打开异步通道
        this.serverChannel.bind(new InetSocketAddress(PORT)) ; // 进行服务端口的绑定
        System.out.println("服务器启动成功，在" + PORT + "端口上进行监听，等待客户端连接 ... ...");
    }
    public AsynchronousServerSocketChannel getServerChannel() {
        return serverChannel;
    }
    public CountDownLatch getLatch() {
        return latch;
    }
    @Override
    public void run() {	// 在线程启动里面等待连接
        this.serverChannel.accept(this, new AcceptHandler()); // 等待客户端连接
        try {
            this.latch.await();	// 持续等待状态，或者你直接执行：Thread.sleep(Long.MAX_VALUE)
            System.err.println("服务器的连接失败，服务器停止运行 ... ...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

public class AIOEchoServer {
    public static void main(String[] args) throws Exception { // 实现一个AIO的服务处理
        new Thread(new AIOServerThread()).start() ; // 启动一个线程的服务器
    }

}