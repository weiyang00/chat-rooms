package example.nio;

/**
 * Created by weiyang on 2018/8/14.
 *
 * @Author: weiyang
 * @Package com.example
 * @Project: chat
 * @Title:
 * @Description: Please fill description of the file here
 * @Date: 2018/8/14 12:54
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;


public class ClientNio {

    private Selector selector = null;
    static final int port = 8082;
    private Charset charset = Charset.forName("UTF-8");
    private SocketChannel sc = null;
    private String name = "";
    private static String USER_EXIST = "system message: user exist, please change a name";
    private static String USER_CONTENT_SPILIT = "#@#";

    public void init() throws IOException {

        name = System.currentTimeMillis() + "";

        System.out.println("user name  === " + name);

        selector = Selector.open();
        //连接远程主机的IP和端口
        sc = SocketChannel.open(new InetSocketAddress("127.0.0.1", port));
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
        //开辟一个新线程来读取从服务器端的数据
        new Thread(() -> {
            try {
                while (true) {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) continue;
                    Set selectedKeys = selector.selectedKeys();  //可以通过这个方法，知道可用通道的集合
                    Iterator keyIterator = selectedKeys.iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey sk = (SelectionKey) keyIterator.next();
                        keyIterator.remove();
                        dealWithSelectionKey(sk);
                    }
                }
            } catch (IOException io) {
                io.printStackTrace();
            }

        }).start();
        //在主线程中 从键盘读取数据输入到服务器端
        Scanner scan = new Scanner(System.in);
        //注册
        sc.write(charset.encode(name));

        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if ("".equals(line)) continue; //不允许发空消息
            if ("".equals(name)) {
                name = line;
                line = name + USER_CONTENT_SPILIT;
            } else {
                line = name + USER_CONTENT_SPILIT + line;
            }
            sc.write(charset.encode(line));//sc既能写也能读，这边是写
        }

    }


    private void dealWithSelectionKey(SelectionKey sk) throws IOException {
        if (sk.isReadable()) {
            //使用 NIO 读取 Channel中的数据，这个和全局变量sc是一样的，因为只注册了一个SocketChannel
            //sc既能写也能读，这边是读
            SocketChannel sc = (SocketChannel) sk.channel();

            ByteBuffer buff = ByteBuffer.allocate(1024);
            StringBuilder content = new StringBuilder();
            while (sc.read(buff) > 0) {
                buff.flip();
                content.append(charset.decode(buff));
            }
            //若系统发送通知名字已经存在，则需要换个昵称
            if (USER_EXIST.equals(content.toString())) {
                name = "";
            }
            System.out.println(content);
            sk.interestOps(SelectionKey.OP_READ);
        }
    }


    public static void main(String[] args) throws IOException {
        new ClientNio().init();
    }


}