import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Vector;

public class APIServer {

    public static Vector<String> addrs = new Vector<String>(); //需要注入的链接

    public static void startCacheServer(int port,geecache cache){
        http peers = new http("localhost:"+port);
        for (String s :addrs){
            peers.setPeers(s);
            geecache.nodes.add(s);
        }
        cache.registerPeers(peers);
        System.out.println("structure.geecache is running at"+"localhost:"+port);
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/_jcache", peers);
            server.setExecutor(null);
            server.start();
            System.out.println("Cache is running at localhost:"+port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void startMainServer(int port,geecache cache){    //port在这里没有到，目前先将主服务的端口绑在8888
        try {
            HttpServer mainServer = HttpServer.create(new InetSocketAddress(8888), 0);
            mainServer.createContext("/api/status", new statusHandler(cache,"localhost:8888"));
            mainServer.createContext("/api/get",new getHandler(cache,"localhost:8888"));
            mainServer.createContext("/api/nodes", new nodesHandler(cache,"localhost:8888"));
            mainServer.createContext("/api/overview",new overviewHandler(cache,"localhost:8888"));
            mainServer.setExecutor(null);
            mainServer.start();
            System.out.println("Main Server is running at localhost:9999");
            addrs.add("localhost:8888");
            startCacheServer(8888,cache);
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
    public static void startSubServer(int port,geecache cache) {
        try {
            HttpServer subServer = HttpServer.create(new InetSocketAddress(port), 0);
            subServer.createContext("/api/status", new statusHandler(cache,"localhost:"+port));
            subServer.createContext("/api/get",new getHandler(cache,"localhost:"+port));
            subServer.setExecutor(null);
            subServer.start();
            System.out.println("Sub Server is running at localhost:9999");
            addrs.add("localhost:"+port);
            startCacheServer(port,cache);
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
}
