package xyg.study.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import xyg.study.model.HTTPRequestEntity;
import xyg.study.model.HTTPResponseEntity;

/**
 * 
 * 对com.xyg.server.HTTPServer进行了重构，之前版本可以参看这个类
 * 除了注释中的知识：学习到的java基础知识：this 不能在静态方法中使用
 * 
 * @author Truman_SSD
 * @version [版本号, 2018年1月27日]
 *
 */

public class SimpleServer
{

    private static HTTPRequestEntity httpRequestEntity;
    private static HTTPResponseEntity httpResponseEntity;
    private static boolean badRequestFlag = false;

    public SimpleServer() {
        httpRequestEntity = new HTTPRequestEntity();
        httpResponseEntity = new HTTPResponseEntity();
    }

    public static void main(String[] args) {

        new SimpleServer();
        // 声明端口号8080及ServerSocket
        int port = 6666;
        ServerSocket serverSocket;

        // 用户可以对于main函数输入参数的方式来设定端口号
        // if (args[0] != null && (args[0]).trim() != " ") {
        // port = Integer.parseInt(args[0]);
        // }
        // 创建ServerSocket，默认对8080端口进行监听
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("服务器端正在监听端口号：" + port);
            while (true) {
                // 持续不断的监听客户端请求，当有客户端请求时，得到一个新的socket
                Socket socket = serverSocket.accept();
                System.out.println("与客户端建立了在" + socket.getInetAddress() + "上的连接，端口号为" + socket.getPort());
                // 将生成的新的socket交由service方法处理
                try {
                    service(socket);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void service(Socket socket) throws Exception {
        // 与流操作相关的地方，抛出的异常肯定多，如果使用try/catch会影响代码的可阅读性。
        // 故抛出，由使用这个方法的类来处理异常。

        String request = getRequest(socket);
        resolveRequest(request);

        generateResponse();
        sendResponse(socket);
        // TODO 去掉sleep看看什么效果
        Thread.sleep(1000);
        // 关闭流
        closeResources(socket);
    }

    private static String getRequest(Socket socket) throws IOException {
        // 客户端和服务器端通信，通过流来进行，所以先要获得流
        InputStream socketIn = socket.getInputStream();
        int size = socketIn.available();
        byte[] buffer = new byte[size];
        // 注意这个方法的使用
        socketIn.read(buffer);
        // 读取HTTP请求
        // String request = buffer.toString();
        String request = new String(buffer);
        // closeSocket(socket);  
//        这边需要注意，关闭通过socket打开的流，socket也会被关闭。总结：通信程序最后关闭 socket 即可。
//        closeResources(socketIn);
        // TODO 动态打印到网页上
        System.out.println(request);
        return request;
    }

    private static void resolveRequest(String request) {
        // 解析HTTP请求CRLF (Carriage Return Linefeed)是指回车符和行结束符
        System.out.println(request);
        String firstLineOfRequest = request.substring(0, request.indexOf("\r\n"));
        // 对请求的资源进行判断
        // 按照空格来将字符串分割开来
        String[] parts = firstLineOfRequest.split(" ");
        httpRequestEntity.setUri(parts[1]);
    }

    private static void generateResponse() {
        String[] partsOfUri = new String[2];
        String uri = httpRequestEntity.getUri();
        partsOfUri[0] = uri.substring(1, uri.indexOf("."));
        partsOfUri[1] = uri.substring(uri.indexOf(".") + 1, uri.length());
        // 设置HTTP响应正文的Contentype
        if (partsOfUri[1].equals("html") || partsOfUri[1].equals("htm")) {
            httpResponseEntity.setContentType("text/html");
            if (!partsOfUri[0].equals("hello")) {
                badRequestFlag = true;
            }
        }
        else {
            // TODO 后期扩展
        }
        // 响应HTTP请求
        // 设置响应第一行
        httpResponseEntity.setFirstLineOfResponse("HTTP/1.1 200 OK\r\n");
        // 设置响应头
        httpResponseEntity.setResponseHeader("Content-Type:" + httpResponseEntity.getContentType() + "\r\n\r\n");
    }

    private static void sendResponse(Socket socket) throws IOException {
        // 设置响应的输入流
        InputStream in = SimpleServer.class.getResourceAsStream("/hello.htm");
        // 这边获取资源写死
        if (badRequestFlag) {
//            TODO 处理中文编码问题
            in = SimpleServer.class.getResourceAsStream("/hello_error.htm");
        }
        OutputStream socketOut = socket.getOutputStream();

        socketOut.write(httpResponseEntity.getFirstLineOfResponse().getBytes());
        socketOut.write(httpResponseEntity.getResponseHeader().getBytes());

        int len = in.available();
        byte[] buffer = new byte[len];
        if ((len = in.read(buffer)) != -1) {
            socketOut.write(buffer, 0, len);
        }
        closeResources(socketOut);
    }

    // TODO 全部都抛出异常有什么不好，有空需要研究下
    // TODO 这个方法其实增加了代码量，后期优化，需要考虑泛型，暂时这个方法没有存在的必要
    private static void closeResources(Object object) throws IOException {
        if (object instanceof Socket) {
            Socket socket = (Socket) object;
            socket.close();
        }
        if (object instanceof InputStream) {
            InputStream inputStream = (InputStream) object;
            inputStream.close();
        }
        if (object instanceof OutputStream) {
            OutputStream outputStream = (OutputStream) object;
            outputStream.close();
        }
    }
}
