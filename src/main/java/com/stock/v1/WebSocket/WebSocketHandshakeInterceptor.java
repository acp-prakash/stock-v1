package com.stock.v1.WebSocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Extract information from the HTTP request URI and add it to the WebSocket session attributes
        String query = request.getURI().getQuery();
        String[] queryParams = query.split("&");

        for (String param : queryParams) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                attributes.put(keyValue[0], keyValue[1]);
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // After handshake logic (if needed)
    }
}