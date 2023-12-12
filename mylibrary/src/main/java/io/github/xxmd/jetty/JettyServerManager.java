package io.github.xxmd.jetty;


import android.content.Context;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import static android.content.Context.WIFI_SERVICE;

public class JettyServerManager {
    private static Server server;
    private static int port = 9000;

    public static Server getSingleton() {
        if (server == null) {
            server = createNewServe();
        }
        return server;
    }

    private static Server createNewServe() {
        server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(FileServlet.class, FileServlet.SERVLET_PATH);
        server.setHandler(context);
        return server;
    }

    /**
     * 启动 jetty 服务器
     * @return
     */
    public static boolean runSingleton() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Server singleton = getSingleton();
                try {
                    if (!singleton.isRunning()) {
                        singleton.start();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.setDaemon(false);
        thread.run();
        return true;
    }
    public static boolean stopSingleton() {
        Server singleton = getSingleton();
        try {
            if (singleton.isRunning()) {
                singleton.stop();
                server = null;
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取投屏 uri, 格式为（http://IP:PORT/SERVLET_PATH?QUERY_PARAMETER=PARAMETER_VALUE）, 例如 http://192.168.1.3:9090/cast?file_uri=xxx.mp4
     * @param context
     * @param filePath
     * @return 投屏 uri
     */
    public static String getCastUri(Context context, String filePath) {
        String ipAddress = getIpAddress(context);
        String serverAddress = "http://" + ipAddress + ":" + port;
        String requestPath =  FileServlet.SERVLET_PATH + "?" + FileServlet.PARAMETER_FILE_URL + "=" + Uri.encode(filePath);
        return serverAddress + requestPath;
    }

    /**
     * 从投屏链接中解析出文件路径
     * @param castUri 投屏链接 example: http://192.168.0.105:9090/cast?file_uri=%2Fstorage%2Femulated%2F0%2FBigBuckBunny.mp4
     * @return 文件路径
     */
    public static String parseCastUri(String castUri) {
        String filePathUri = castUri.split("=")[1];
        return Uri.decode(filePathUri);
    }

    public static final String getIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }
        return ipAddressString;
    }

}
