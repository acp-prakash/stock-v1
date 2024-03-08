package com.stock.v1.WebSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.v1.vo.Options;
import com.stock.v1.vo.Stock;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyWebSocketHandler extends TextWebSocketHandler {
	
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	// Handle connection established event
    	String page = (String) session.getAttributes().get("page");
    	String user = (String) session.getAttributes().get("user");
    	System.out.println("PAGE ==>" + page + ", USER ==>" + user);
    	sessions.add(session);
    	sendMessage(new ArrayList<Options>());
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Stock stockDetails = objectMapper.readValue(payload, Stock.class);

        // Handle the received StockDetails object as needed
        System.out.println("Received StockDetails: " + stockDetails);

        // You can also send a response back to the client if needed
        sendMessage(new ArrayList<Options>());
    }

    public void sendMessage(List<Options> list){
    	try {
    		String jsonMessage = objectMapper.writeValueAsString(list);
    		for (WebSocketSession session : sessions) {
    			if(session.isOpen())
    				session.sendMessage(new TextMessage(jsonMessage));
    			else
    				sessions.remove(session);
    		}    	
    	}
    	catch (Exception e) {
    		System.err.println("EXCEPTION IN - sendMessage::" + e);
    	}
    }
}