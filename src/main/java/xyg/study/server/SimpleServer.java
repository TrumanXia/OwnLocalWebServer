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
 * ��com.xyg.server.HTTPServer�������ع���֮ǰ�汾���Բο������
 * ����ע���е�֪ʶ��ѧϰ����java����֪ʶ��this �����ھ�̬������ʹ��
 * 
 * @author Truman_SSD
 * @version [�汾��, 2018��1��27��]
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
        // �����˿ں�8080��ServerSocket
        int port = 6666;
        ServerSocket serverSocket;

        // �û����Զ���main������������ķ�ʽ���趨�˿ں�
        // if (args[0] != null && (args[0]).trim() != " ") {
        // port = Integer.parseInt(args[0]);
        // }
        // ����ServerSocket��Ĭ�϶�8080�˿ڽ��м���
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("�����������ڼ����˿ںţ�" + port);
            while (true) {
                // �������ϵļ����ͻ������󣬵��пͻ�������ʱ���õ�һ���µ�socket
                Socket socket = serverSocket.accept();
                System.out.println("��ͻ��˽�������" + socket.getInetAddress() + "�ϵ����ӣ��˿ں�Ϊ" + socket.getPort());
                // �����ɵ��µ�socket����service��������
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
        // ����������صĵط����׳����쳣�϶��࣬���ʹ��try/catch��Ӱ�����Ŀ��Ķ��ԡ�
        // ���׳�����ʹ��������������������쳣��

        String request = getRequest(socket);
        resolveRequest(request);

        generateResponse();
        sendResponse(socket);
        // TODO ȥ��sleep����ʲôЧ��
        Thread.sleep(1000);
        // �ر���
        closeResources(socket);
    }

    private static String getRequest(Socket socket) throws IOException {
        // �ͻ��˺ͷ�������ͨ�ţ�ͨ���������У�������Ҫ�����
        InputStream socketIn = socket.getInputStream();
        int size = socketIn.available();
        byte[] buffer = new byte[size];
        // ע�����������ʹ��
        socketIn.read(buffer);
        // ��ȡHTTP����
        // String request = buffer.toString();
        String request = new String(buffer);
        // closeSocket(socket);  
//        �����Ҫע�⣬�ر�ͨ��socket�򿪵�����socketҲ�ᱻ�رա��ܽ᣺ͨ�ų������ر� socket ���ɡ�
//        closeResources(socketIn);
        // TODO ��̬��ӡ����ҳ��
        System.out.println(request);
        return request;
    }

    private static void resolveRequest(String request) {
        // ����HTTP����CRLF (Carriage Return Linefeed)��ָ�س������н�����
        System.out.println(request);
        String firstLineOfRequest = request.substring(0, request.indexOf("\r\n"));
        // ���������Դ�����ж�
        // ���տո������ַ����ָ��
        String[] parts = firstLineOfRequest.split(" ");
        httpRequestEntity.setUri(parts[1]);
    }

    private static void generateResponse() {
        String[] partsOfUri = new String[2];
        String uri = httpRequestEntity.getUri();
        partsOfUri[0] = uri.substring(1, uri.indexOf("."));
        partsOfUri[1] = uri.substring(uri.indexOf(".") + 1, uri.length());
        // ����HTTP��Ӧ���ĵ�Contentype
        if (partsOfUri[1].equals("html") || partsOfUri[1].equals("htm")) {
            httpResponseEntity.setContentType("text/html");
            if (!partsOfUri[0].equals("hello")) {
                badRequestFlag = true;
            }
        }
        else {
            // TODO ������չ
        }
        // ��ӦHTTP����
        // ������Ӧ��һ��
        httpResponseEntity.setFirstLineOfResponse("HTTP/1.1 200 OK\r\n");
        // ������Ӧͷ
        httpResponseEntity.setResponseHeader("Content-Type:" + httpResponseEntity.getContentType() + "\r\n\r\n");
    }

    private static void sendResponse(Socket socket) throws IOException {
        // ������Ӧ��������
        InputStream in = SimpleServer.class.getResourceAsStream("/hello.htm");
        // ��߻�ȡ��Դд��
        if (badRequestFlag) {
//            TODO �������ı�������
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

    // TODO ȫ�����׳��쳣��ʲô���ã��п���Ҫ�о���
    // TODO ���������ʵ�����˴������������Ż�����Ҫ���Ƿ��ͣ���ʱ�������û�д��ڵı�Ҫ
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
