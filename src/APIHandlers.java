import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class APIHandlers {
}

class nodesHandler implements HttpHandler {
    public geecache cache;
    public nodesHandler(geecache cache) {
        this.cache = cache;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}

class statusHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}