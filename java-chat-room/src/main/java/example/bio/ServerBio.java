package example.bio;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weiyang on 2018/8/8.
 *
 * @Author: weiyang
 * @Package com.example
 * @Project: chat
 * @Title:
 * @Description: Please fill description of the file here
 * @Date: 2018/8/8 8:54
 */
public class ServerBio {

    //相当于自定义协议格式，与客户端协商好
    private static String USER_CONTENT_SPILIT = "#@#";

    public static int port = 8081;

    List<ClientBio> clients;

    ServerSocket server;

    public static void main(String[] args) {
        ServerBio server = new ServerBio();

        server.start();
    }

    public void start(){
        new AcceptThread().start();
    }

    // 第二个线程： 接受client的信息并推送给所有client
    class Mythread extends Thread {
        Socket ssocket;
        private BufferedReader br;
        private PrintWriter pw;
        public String msg;

        public Mythread(Socket s) {
            ssocket = s;
        }

        public void run() {

            try {
                br = new BufferedReader(new InputStreamReader(ssocket.getInputStream()));
                // 接受client 的信息
                while ((msg = br.readLine()) != null) {

                    msg = "【" + ssocket.getInetAddress() +":" + ssocket.getLocalPort() + "】say：" + msg;
                    sendMsg();

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void sendMsg() {
            try {
                System.out.println(msg);

                for (int i = clients.size() - 1; i >= 0; i--) {
                    pw = new PrintWriter(clients.get(i).getSocket().getOutputStream(), true);
                    pw.println(msg);
                    pw.flush();
                }
            } catch (Exception ex) {
            }
        }
    }

    // 第一个线程： 接受client的连接和注册
    class AcceptThread extends Thread {
        public void run() {
            try {
                clients = new ArrayList<ClientBio>();
                server = new ServerSocket(port);

                while (true) {
                    // 监听,等待连接,一旦有client端连接便创建socket实例，阻塞.
                    Socket socket = server.accept();

                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    // 阻塞
                    String first = br.readLine();
                    System.out.println(first );

                    if (first.length() > 0){
                        String[] arrayContent = first.split(USER_CONTENT_SPILIT);
                        //注册用户
                        if (arrayContent != null && arrayContent.length >= 1) {
                            String name = arrayContent[0];

                            ClientBio client = new ClientBio();
                            client.setUserName(name);
                            client.setSocket(socket);
                            clients.add(client);

                            String message = "welcome " + name + " to chat room! in "+ socket.getLocalAddress() + ":"+ socket.getLocalPort()  +"!  Online numbers:" + clients.size();
                            System.out.println(message);

                        }
                    } else {
                        continue;
                    }

                    Mythread mythread = new Mythread(socket);
                    mythread.start();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


}

