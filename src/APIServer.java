import com.sun.net.httpserver.HttpServer;
import handlers.nodesHandler;

import java.net.InetSocketAddress;

public class APIServer {
    public static void startCacheServer(){

    }
    public static void startMainServer() {
        HttpServer mainServer = HttpServer.create(new InetSocketAddress(9999), 0);
        mainServer.createContext("/api/status", new statusHandler());
//        mainServer.createContext("/api/get",new );
        mainServer.createContext("/api/nodes", new nodesHandler());
//        mainServer.createContext("/api/overview",new APIhttp("localhost:9999"));
         startCacheServer();
    };
    public static void startSubServer() {
        // api 1, 2
        startCacheServer();
    };
}
