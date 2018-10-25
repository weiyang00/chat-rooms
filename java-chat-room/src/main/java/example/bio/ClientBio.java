package example.bio;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by weiyang on 2018/8/8.
 *
 * @Author: weiyang
 * @Package com.example
 * @Project: chat
 * @Title:
 * @Description: Please fill description of the file here
 * @Date: 2018/8/8 8:56
 */
public class ClientBio {

    //相当于自定义协议格式，与客户端协商好
    private static String USER_CONTENT_SPILIT = "#@#";

    public static final int port = 8081;
    public String hostname = "127.0.0.1";
    Socket socket = null;
    String userName = "";


    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static void main(String[] args) {
        ClientBio client = new ClientBio();

        // 启动内部类的线程
        new Thread(client.formatStart()).start();
    }

    // 创建内部类的对象
    public Start formatStart(){
        Start start = new Start();
        return start;
    }

    class Cthread extends Thread {

        public void run() {
            try {

                BufferedReader re = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                // 向server 注册用户名
                String msg2 = "客户1号";
                pw.println(msg2);

                //向服务器端发送数据
                while (true) {
                    // 阻塞读取 控制台 的输入
                    msg2 = re.readLine();
                    pw.println(msg2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }


    class Start implements Runnable{

        public void run() {

            try {
                // 创建一个信息socket连接
                socket = new Socket(hostname, port);

                new Cthread().start();

                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String msg;

                //读取服务器端输入数据
                while ((msg = br.readLine()) != null) {

                    System.out.println(msg);
                }

            } catch (Exception e) {
            }
        }
    }

}
