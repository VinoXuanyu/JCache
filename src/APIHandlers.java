import byteview.byteview;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class APIHandlers {

}

class nodesHandler implements HttpHandler {
    String self;
    String basePath;
    public static String defaultBasePath = "/api/nodes";
    public geecache cache;
    public nodesHandler(geecache cache,String self) {
        this.cache = cache;
        this.self = self;
        this.basePath = defaultBasePath;

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
        httpExchange.sendResponseHeaders(200, 0);
        os.write(geecache.nodes.toString().getBytes(StandardCharsets.UTF_8));
        os.close();
    }
    public void log(String format, Object... args) {
        System.out.printf("Server[%s] ", this.self);
        System.out.printf(format, args);
        System.out.println();
    }

}

class statusHandler implements HttpHandler {
    String self;
    String basePath;
    public static String defaultBasePath = "/api/status/?";
    public geecache cache;

    public statusHandler(geecache cache,String self){
        this.cache = cache;
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
        String node = path.replace(this.basePath, "");
        List<String> groups = new ArrayList<String>();
        for (String s : geecache.groups.keySet()){
            geecache group = geecache.groups.get(s);
            groups.add(group.name);
        }
        int request_num = geecache.requestReceiveNum;
        int hit_num = geecache.cacheHitNum;
        double hit_rate = 1.0*hit_num;
        hit_rate = hit_rate/request_num;
        httpExchange.sendResponseHeaders(200, 0);
        HashMap<String,String> json = new HashMap<>();
        json.put("node",this.self);
        json.put("groups",String.valueOf(groups));
        json.put("request_num", String.valueOf(request_num));
        json.put("hit_num", String.valueOf(hit_num));
        json.put("hit_rate", String.valueOf(hit_rate));
        os.write(new byteview(json.toString()).bytes());
        os.close();
    }
}
class getHandler implements HttpHandler {
    String self;
    String basePath;
    public static String defaultBasePath = "/api/get";
    public geecache cache;

    public getHandler(geecache cache,String self){
        this.cache = cache;
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
        String[] params0 = path.split("\\?");
        path = params0[1];
        String[] params1 = path.split("&");
        String[] params2 = params1[0].split("=");
        String groupName = params2[1];
        String[] params3 = params1[1].split("=");
        String key = params3[1];

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
        String s = new String(ret.bytes());
        this.log("%s value is %s", key, s);

        httpExchange.sendResponseHeaders(200, 0);
        os.write(ret.bytes());
        os.close();
    }
}
class overviewHandler implements HttpHandler {
    String self;
    String basePath;
    public static String defaultBasePath = "/api/overview";
    public geecache cache;
    public overviewHandler(geecache cache,String self){
        this.cache = cache;
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
        HashMap<String,String> overview = new HashMap<>();
        for (String s : geecache.nodes){
            HttpGetter httpGetter = new HttpGetter(s);
            overview.put(s,httpGetter.getStatus());
        }
        httpExchange.sendResponseHeaders(200, 0);
        os.write(new byteview(overview.toString()).bytes());
        os.close();

    }
}