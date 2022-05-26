import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;

public class http implements HttpHandler, IPeerPicker{
    String self;
    String basePath;
    Lock mu;
    consistenthash.consistenthash peers;
    HashMap<String, HttpGetter> httpGetters;

    public static String defaultBasePath = "/_jcache/";
    public static int defaultReplicas = 50;

    public http(String self){
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
        if (params.length != 2) {
            this.log("Bad Request: %s", path);
            httpExchange.sendResponseHeaders(400, 0);
            os.close();
            return;
        }
        String groupName = params[0];
        String key = params[1];
        geecache group = geecache.getGroup(groupName);

        if (group == null) {
            this.log("No such group %s", groupName);
            httpExchange.sendResponseHeaders(404, 0);
            os.close();
            return;
        }

        byteview ret = group.get(key);
        if (ret == null) {
            this.log("%s not found in %s", key, groupName);
            os.close();
            return;
        }
        httpExchange.sendResponseHeaders(200, 0);
        os.write(ret.bytes());
        os.close();
    }

    @Override
    public IPeerGetter pickPeer(String key) {
        this.mu.lock();
        String peer = this.peers.get(key);
        if (!peer.equals("") && !peer.equals(this.self)){
            this.mu.unlock();
            return this.httpGetters.get(peer);
        }
        this.mu.unlock();
        return null;
    }

    public void setPeers(String... peers) {
        this.mu.lock();
        this.peers = new consistenthash.consistenthash(defaultReplicas, null);
        this.peers.add(peers);
        this.httpGetters = new HashMap<>();
        for (String peer : peers) {
            this.httpGetters.put(peer, new HttpGetter(peer + this.basePath));
        }
    }
}

class HttpGetter implements IPeerGetter {
    String baseURL;

    public HttpGetter(String baseURL) {
        baseURL = baseURL;
    }

    @Override
    public byteview get(String group, String key) {
        String requestURL = String.format("%s%s/%s",baseURL, group, key);
        String result = "";
        String line;
        try {
            URL url = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            int responseCode = con.getResponseCode();
            String responseMsg = con.getResponseMessage();
            System.out.println(responseMsg);
            if (responseCode != 200) {
                System.out.println("Request failed");
                return null;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while ((line = in.readLine()) != null) {
                    result += line;
            }
            return new byteview(result.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
                System.out.println("Error happened when getting from peer");
                e.printStackTrace();
                return null;
            }
    }
}
