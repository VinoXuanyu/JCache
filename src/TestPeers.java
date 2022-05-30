import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;

public class TestPeers {

    public geecache gee;
    public geecache creatGroup(){
        gee = geecache.newGroup("scores");
        return gee;
    }
    public void startCacheServer(int port, String addr, String[] addrs,geecache gee){
        http peers = new http(addr);
        peers.setPeers(addrs);
        geecache.mainCache.put(String.format("%s%d", port, 4), Integer.toString(port)); // {80011:8001 ... }
        geecache.mainCache.put(String.format("%s%d", port, 5), Integer.toString(port));
        geecache.mainCache.put(String.format("%s%d", port, 6), Integer.toString(port));
        gee.registerPeers(peers);
        System.out.println("geecache is running at"+addr);
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
    public void startAPIServer(String apiAddr, geecache gee){
        try {
            HttpServer server4 = HttpServer.create(new InetSocketAddress(9999), 0);
            server4.createContext("/api",new APIhttp("localhost:9999"));
            server4.setExecutor(null);
            server4.start();
            System.out.println("API Server is running at localhost:9999");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        TestPeers test = new TestPeers();
        Scanner in =new Scanner(System.in);
        String argStr=in.nextLine();
        String[] ll = argStr.split(" ");


        int port = Integer.parseInt(ll[0]);
        int api = Integer.parseInt(ll[1]);
        String apiAddr = "";
        apiAddr = "http://localhost:9999";
        HashMap<Integer,String> addrMap = new HashMap<Integer,String>();
        addrMap.put(8001,"http://localhost:8001");
        addrMap.put(8002,"http://localhost:8002");
        addrMap.put(8003,"http://localhost:8003");
        String[] addrs = new String[]{"http://localhost:8001","http://localhost:8002","http://localhost:8003"};
        geecache gee = test.creatGroup();
        if(api == 1){
            test.startAPIServer(apiAddr,gee);
        }
        test.startCacheServer(port,addrMap.get(port),addrs,gee);
    }
}
class APIhttp implements HttpHandler {
    String self;
    String basePath;
    Lock mu;
    public static String defaultBasePath = "/api";


    public APIhttp(String self){
        this.self = self;
        this.basePath = defaultBasePath;
    }
    public void log(String format, Object... args) {
        System.out.printf("Server[%s] ", this.self);
        System.out.printf(format, args);
        System.out.println();
    }
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getRawPath();
        OutputStream os = httpExchange.getResponseBody();
        Headers headers = httpExchange.getResponseHeaders();
        headers.set("Content-Type", "application/octet-stream");
        this.log("[%s] %s", method, path);
        if (!path.contains(this.basePath)) {
            this.log("HTTPPool serving unexpected path: %s", path);
            httpExchange.sendResponseHeaders(404, 0);
            os.close();
            return;
        }
        path = path.replace(this.basePath, "");
        String[] params = path.split("/");
        if (params.length != 3) {
            this.log("Bad Request: %s", path);
            httpExchange.sendResponseHeaders(400, 0);
            os.close();
            return;
        }
        String groupName = params[1];
        this.log( groupName);
        String key = params[2];
        this.log( key);
        geecache group = geecache.getGroup(groupName);

        if (group == null) {
            this.log("No such group %s", groupName);
            httpExchange.sendResponseHeaders(404, 0);
            os.close();
            return;
        }
        httpExchange.sendResponseHeaders(200, 0);
        byteview ret = group.get(key);
        if (ret == null) {
            this.log("%s not found in %s", key, groupName);
            httpExchange.sendResponseHeaders(404, 0);
            os.close();
            return;
        }
        this.log("%s found in %s", key, groupName);
        String s = new String(ret.bytes);
        this.log("%s value is %s", key, s);

        httpExchange.sendResponseHeaders(200, 0);
        os.write(ret.bytes());
        os.close();
    }
}