import byteview.byteview;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import consistenthash.consistenthash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class http implements HttpHandler, IPeerPicker{
    String self;
    String basePath;
    Lock mu;
    consistenthash peers;//新增成员变量 peers，类型是一致性哈希算法的 Map，用来根据具体的 key 选择节点
    HashMap<String, HttpGetter> httpGetters;//新增成员变量 httpGetters，映射远程节点与对应的 httpGetter。每一个远程节点对应一个 httpGetter，
                                            // 因为 httpGetter 与远程节点的地址 baseURL 有关
    public static String defaultBasePath = "/_jcache/";
    public static int defaultReplicas = 50;

    public http(String self){
        this.self = self;
        this.basePath = defaultBasePath;
        mu = new ReentrantLock();
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
            httpExchange.sendResponseHeaders(404, 0);
            String sRet = new String(key +" not found in " + groupName);
            os.write(sRet.getBytes(StandardCharsets.UTF_8));
            this.log("%s not found in %s", key, groupName);
            os.close();
            return;
        }
        httpExchange.sendResponseHeaders(200, 0);
        os.write(ret.bytes());
        os.close();
    }

    @Override
    public IPeerGetter pickPeer(String key) {//包装了一致性哈希算法的 Get() 方法，根据具体的 key，选择节点，返回节点对应的 HTTP 客户端
        this.log("调用Pick peer %s",key);
        this.mu.lock();
        this.log("调用Pick peer2 %s",key);
        String peer = this.peers.get(key);
        this.log("先找到没返回Pick peer %s",peer);
        if (!peer.equals("") && !peer.equals(this.self)){
            this.mu.unlock();
            this.log("Pick peer %s",peer);
            System.out.println();
            return this.httpGetters.get(peer);
        }
        this.mu.unlock();
        return null;
    }

    public void setPeers(String... peers) {//实例化了一致性哈希算法，并且添加了传入的节点。
                                            //并为每一个节点创建了一个 HTTP 客户端 httpGetter
        this.mu.lock();

        this.peers = new consistenthash(defaultReplicas, null);
        this.peers.add(peers);
        this.httpGetters = new HashMap<>();
        for (String peer : peers) {
            this.httpGetters.put(peer, new HttpGetter(peer + this.basePath));
            System.out.println("创建客户端 httpGetter:"+peer + this.basePath);
        }
        for(String peer : httpGetters.keySet()){
            System.out.println("客户端 httpGetter 代号: "+httpGetters.get(peer).toString());
        }
        this.mu.unlock();
    }

}

class HttpGetter implements IPeerGetter {//首先创建具体的 HTTP 客户端类 httpGetter，实现 PeerGetter 接口
    String baseURL;//表示将要访问的远程节点的地址，例如 http://example.com/_geecache/

    public HttpGetter(String baseURL) {
        this.baseURL = baseURL;
    }

    @Override
    public byteview get(String group, String key) {//使用 http.Get() 方式获取返回值，并转换为 []bytes 类型
        System.out.println("调用HttpGetter的baseURL:"+this.baseURL);
        String requestURL = String.format("%s%s/%s",baseURL, group, key);
        System.out.println("requestURL:"+requestURL);
        String result = "";
        String line;
        try {
            URL url = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            int responseCode = con.getResponseCode();//从 HTTP 响应消息获取状态码。如果无法从响应中识别任何代码（即响应不是有效的 HTTP），则返回 -1。
            String responseMsg = con.getResponseMessage();//获取与来自服务器的响应代码一起返回的 HTTP 响应消息（如果有）
            System.out.println(responseMsg);
            if (responseCode != 200) {
                System.out.println("Request failed");
                return null;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while ((line = in.readLine()) != null) {
                    result += line;
            }
            byteview byt = new byteview(result);
            return byt;

        } catch (Exception e) {
                System.out.println("Error happened when getting from peer");
                e.printStackTrace();
                return null;
            }
    }
}
