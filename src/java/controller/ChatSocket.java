package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/chatSocket")
public class ChatSocket {

    private static Map<String, Session> userSessions = new ConcurrentHashMap<>();

    Gson gson = new Gson();

    @OnOpen
    public void open(Session session) throws IOException {

        System.out.println("New Connection: " + session.getId());
//        session.getBasicRemote().sendText("Welcome to the chat!");
    }

    @OnMessage
    public void handleMessage(String message, Session session) {

        System.out.println("Received message: " + message);

        String loggedUserId = null;
        String recipientUserId = null;

        if (message.startsWith("USER_ID:")) {
            String[] userIds = message.split(":");
            loggedUserId = userIds[1];  // Sender (logged user)
            recipientUserId = userIds[2];  // Recipient user

            // Register both users in session
            if (!loggedUserId.isEmpty()) {
                userSessions.put(loggedUserId, session);
                System.out.println("Logged User ID registered: " + loggedUserId);
            }

            if (!recipientUserId.isEmpty()) {
                // Optionally register recipient if they're connected
                if (userSessions.get(recipientUserId) == null) {
                    userSessions.put(recipientUserId, session);
                    System.out.println("Recipient User ID registered: " + recipientUserId);
                }
            }
            return;
        }

        String[] parts = message.split(":");
        if (parts.length < 3) {
            System.out.println("Invalid message format");
            return;
        }

        String senderId = parts[0]; //logged user
        String recipientId = parts[1]; //selected user
        String messageContent = parts[2];

        System.out.println("Received sender: " + senderId);
        System.out.println("Received reciever: " + recipientId);

        //send chat message
        ChatService chatService = new ChatService();
        chatService.sendChat(senderId, recipientId, messageContent);

        //Fetching updated chats
        JsonObject groupedChats = chatService.receiveChats(senderId, recipientId);

        try {
            // Notify the sender
            Session senderSession = userSessions.get(senderId);
            if (senderSession != null) {
                senderSession.getBasicRemote().sendText(gson.toJson(groupedChats));
            }

            // Notify the recipient
            Session recipientSession = userSessions.get(recipientId);
            if (recipientSession != null) {
                recipientSession.getBasicRemote().sendText(gson.toJson(groupedChats));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void close(Session session) {
        userSessions.values().remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error on session: " + session.getId() + " - " + throwable.getMessage());
    }
}
