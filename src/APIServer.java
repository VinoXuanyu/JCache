import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class APIServer {
    public static void startCacheServer(){

    }
    public static void startMainServer() {
        try {
            HttpServer mainServer = HttpServer.create(new InetSocketAddress(9999), 0);
            mainServer.createContext("/api/status", new statusHandler());
            mainServer.createContext("/api/nodes", new nodesHandler());
            startCacheServer();
        } catch (Exception  e) {
            e.printStackTrace();
        }
    };
    public static void startSubServer() {
        // api 1, 2
        startCacheServer();
    };
}
