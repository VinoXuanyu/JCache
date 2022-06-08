import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class APIServer {

    public static String[] addrs; //需要注入的链接
    public static void startCacheServer(int port,geecache cache, HttpServer server){
        http peers = new http("localhost:"+port);
        for (String s :addrs){
            peers.setPeers(s);
            geecache.nodes.add(s);
        }
        cache.registerPeers(peers);
        System.out.println("structure.geecache is running at"+"localhost:"+port);
        try {
            server.createContext("/_jcache", peers);
            server.setExecutor(null);
            server.start();
            System.out.println("Cache is running at localhost:"+port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void startMainServer(int port, geecache cache, String[] addrs){
        APIServer.addrs = addrs;
        //port在这里没有到，目前先将主服务的端口绑在8888
        if (port == 0){
            port = 8888;
        }
        try {
            HttpServer mainServer = HttpServer.create(new InetSocketAddress(port), 0);
            mainServer.createContext("/api/status", new statusHandler(cache,"localhost:" + port));
            mainServer.createContext("/api/get",new getHandler(cache,"localhost:" + port));
            mainServer.createContext("/api/nodes", new nodesHandler(cache,"localhost:" + port));
            mainServer.createContext("/api/overview",new overviewHandler(cache,"localhost:" + port));
            mainServer.setExecutor(null);
            System.out.println("Main Server is running at localhost:" + port);
            startCacheServer(port, cache, mainServer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void startSubServer(String addr, geecache cache) {
        String[] temp = addr.split(":");
        String port = temp[2];
        try {
            HttpServer subServer = HttpServer.create(new InetSocketAddress(Integer.parseInt(port)), 0);
            subServer.createContext("/api/status", new statusHandler(cache,"localhost:"+port));
            subServer.createContext("/api/get",new getHandler(cache,"localhost:"+port));
            subServer.setExecutor(null);
            System.out.println("Sub Server is running at "+addr);
            startCacheServer(Integer.parseInt(port), cache, subServer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
