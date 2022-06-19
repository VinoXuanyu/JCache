import byteview.byteview;
import com.alibaba.fastjson2.JSONObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class APIHandlers {

}

class nodesHandler implements HttpHandler {
    public static String defaultBasePath = "/api/nodes";
    public geecache cache;
    String self;
    String basePath;

    public nodesHandler(geecache cache, String self) {
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
        headers.set("Content-Type", "application/json");
        this.log("[%s] %s", method, path);
        if (!path.contains(this.basePath)) {
            this.log("HTTPPool serving unexpected path: %s", path);
            JSONObject Json = new JSONObject();
            Json.put("code", 404);
            Json.put("status", "HTTPPool serving unexpected path");
            httpExchange.sendResponseHeaders(404, 0);
            os.write(Json.toJSONString().getBytes(StandardCharsets.UTF_8));
            os.close();
            return;
        }
        JSONObject json = new JSONObject();
        json.put("code", 200);
        json.put("data", geecache.nodes.toString());
        json.put("status", "success");
        httpExchange.sendResponseHeaders(200, 0);
        os.write(json.toJSONString().getBytes(StandardCharsets.UTF_8));
        os.close();
    }

    public void log(String format, Object... args) {
        System.out.printf("[APIServer] [%s] ", this.self);
        System.out.printf(format, args);
        System.out.println();
    }

}

class statusHandler implements HttpHandler {
    public static String defaultBasePath = "/api/status";
    public geecache cache;
    String self;
    String basePath;

    public statusHandler(geecache cache, String self) {
        this.cache = cache;
        this.self = self;
        this.basePath = defaultBasePath;
    }

    public void log(String format, Object... args) {
        System.out.printf("[APIServer] [%s] ", this.self);
        System.out.printf(format, args);
        System.out.println();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getRawPath();
        OutputStream os = httpExchange.getResponseBody();
        Headers headers = httpExchange.getResponseHeaders();
        headers.set("Content-Type", "application/json");
        this.log("[%s] %s", method, path);
        if (!path.contains(this.basePath)) {
            this.log("HTTPPool serving unexpected path: %s", path);
            JSONObject Json = new JSONObject();
            Json.put("code", 404);
            Json.put("status", "HTTPPool serving unexpected path");
            httpExchange.sendResponseHeaders(404, 0);
            os.write(Json.toJSONString().getBytes(StandardCharsets.UTF_8));
            os.close();
            return;
        }

        List<String> groups = new ArrayList<>();
        for (String s : geecache.groups.keySet()) {
            geecache group = geecache.groups.get(s);
            groups.add(group.name);
        }
        int request_num = geecache.requestReceiveNum;
        int hit_num = geecache.cacheHitNum;
        double hit_rate = 1.0 * hit_num;
        hit_rate = hit_rate / request_num;
        JSONObject json = new JSONObject();
        json.put("node", this.self);
        json.put("groups", String.valueOf(groups));
        json.put("request_num", String.valueOf(request_num));
        json.put("hit_num", String.valueOf(hit_num));
        json.put("hit_rate", String.valueOf(hit_rate));

        JSONObject Json = new JSONObject();
        Json.put("code", 200);
        Json.put("data", json);
        Json.put("status", "success");
        httpExchange.sendResponseHeaders(200, 0);
        os.write(Json.toJSONString().getBytes(StandardCharsets.UTF_8));
        os.close();

    }
}

class getHandler implements HttpHandler {
    public static String defaultBasePath = "/api/get";
    public geecache cache;
    String self;
    String basePath;

    public getHandler(geecache cache, String self) {
        this.cache = cache;
        this.self = self;
        this.basePath = defaultBasePath;
    }

    public void log(String format, Object... args) {
        System.out.printf("[APIServer] [%s] ", this.self);
        System.out.printf(format, args);
        System.out.println();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getRawPath();
        String params =httpExchange.getRequestURI().getQuery();
        OutputStream os = httpExchange.getResponseBody();
        Headers headers = httpExchange.getResponseHeaders();
        headers.set("Content-Type", "application/json");
        this.log("[%s] %s", method, path);
        if (!path.contains(this.basePath)) {
            this.log("HTTPPool serving unexpected path: %s", path);
            JSONObject Json = new JSONObject();
            Json.put("code", 404);
            Json.put("status", "HTTPPool serving unexpected path");
            httpExchange.sendResponseHeaders(404, 0);
            os.write(Json.toJSONString().getBytes(StandardCharsets.UTF_8));
            os.close();
            return;
        }
      //  this.log(" params %s", params);
        String[] params1 = params.split("&");
        String[] params2 = params1[0].split("=");
        String groupName = params2[1];
        String[] params3 = params1[1].split("=");
        String key = params3[1];
        this.log("group: %s , key: %s", groupName, key);
        //this.log("key: %s", key);
        geecache group = geecache.getGroup(groupName);

        if (group == null) {
            this.log("No such group %s", groupName);
            JSONObject Json = new JSONObject();
            Json.put("code", 404);
            Json.put("status", "No such group");
            httpExchange.sendResponseHeaders(404, 0);
            os.write(Json.toJSONString().getBytes(StandardCharsets.UTF_8));
            os.close();
            return;
        }

        //httpExchange.sendResponseHeaders(200, 0);
        byteview ret = group.get(key);
        if (ret == null) {
            this.log("%s not found in %s", key, groupName);
            JSONObject Json = new JSONObject();
            Json.put("code", 404);
            Json.put("status", "key not found in group");
            httpExchange.sendResponseHeaders(404, 0);
            os.write(Json.toJSONString().getBytes(StandardCharsets.UTF_8));
            os.close();
            return;
        }
        String s = new String(ret.bytes());
        this.log("%s found in %s , %s value is %s", key, groupName, key, s);

        //this.log("%s value is %s", key, s);


        JSONObject Json = new JSONObject();
        Json.put("code", 200);
        Json.put("data", ret.string());
        Json.put("status", "success");
        httpExchange.sendResponseHeaders(200, 0);
        os.write(Json.toJSONString().getBytes(StandardCharsets.UTF_8));
        os.close();
    }
}

class overviewHandler implements HttpHandler {
    public static String defaultBasePath = "/api/overview";
    public geecache cache;
    String self;
    String basePath;

    public overviewHandler(geecache cache, String self) {
        this.cache = cache;
        this.self = self;
        this.basePath = defaultBasePath;
    }

    public void log(String format, Object... args) {
        System.out.printf("[APIServer] [%s] ", this.self);
        System.out.printf(format, args);
        System.out.println();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getRawPath();

        OutputStream os = httpExchange.getResponseBody();
        Headers headers = httpExchange.getResponseHeaders();
        headers.set("Content-Type", "application/json");
        this.log("[%s] %s", method, path);
        if (!path.contains(this.basePath)) {
            this.log("HTTPPool serving unexpected path: %s", path);
            JSONObject Json = new JSONObject();
            Json.put("code", 404);
            Json.put("status", "HTTPPool serving unexpected path");
            httpExchange.sendResponseHeaders(404, 0);
            os.write(Json.toJSONString().getBytes(StandardCharsets.UTF_8));
            os.close();
            return;
        }
        JSONObject overview = new JSONObject();
        for (String s : geecache.nodes) {
            HttpGetter httpGetter = new HttpGetter(s);
            overview.put(s, httpGetter.getStatus());
        }
        JSONObject Json = new JSONObject();
        Json.put("code", 200);
        Json.put("data", overview);
        Json.put("status", "success");
        httpExchange.sendResponseHeaders(200, 0);
        os.write(Json.toJSONString().getBytes(StandardCharsets.UTF_8));
        os.close();

    }
}