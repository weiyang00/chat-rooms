package com.example.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

/**
 * netty客户端 . <br>
 *
 * @author hkb
 */
public class NettyClient {

    /**
     * 主机
     */
    private String host;

    /**
     * 端口号
     */
    private int port;

    /**
     * 客户端昵称
     */
    private String name = "client1";

    private static String USER_EXIST = "system message: user exist, please change a name";
    private static String USER_CONTENT_SPILIT = "#@#";

    /**
     * 构造函数
     *
     * @param host
     * @param port
     */
    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 连接方法
     */
    public void connect() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new NettyClientInitializer());
            Channel channel = bootstrap.connect(host, port).sync().channel();

            //在主线程中 从键盘读取数据输入到服务器端
            Scanner scan = new Scanner(System.in);

            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                System.out.println( line);
                // 向服务端server 发送信息
                channel.writeAndFlush(line + "\n");
//                channel.closeFuture().sync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    /**
     * 测试入口
     *
     * @param args
     */
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 11111;
        NettyClient nettyClient = new NettyClient(host, port);
        nettyClient.connect();
    }

}