import com.sun.net.httpserver.HttpServer;
import getters.IGetter;

import java.io.IOException;
import java.net.InetSocketAddress;

public class TestHTTP {
    public static void main(String[] args) {
        try {
            geecache.newGroup("tom", (IGetter) new testGetter());
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/_jcache", new http("localhost:8000"));
            server.setExecutor(null);
            server.start();
            System.out.println("Cache is running at localhost:8000");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
