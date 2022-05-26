import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class http implements HttpHandler{
    String self;
    String basePath;

    public http(String self){
        this.self = self;
        this.basePath = "/_jcache/";
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
}
