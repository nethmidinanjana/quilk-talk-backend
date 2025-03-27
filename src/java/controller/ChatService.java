
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.Chat_Status;
import entity.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class ChatService {
    Session session = HibernateUtil.getSessionFactory().openSession();
    
    public void sendChat(String logged_user_id, String other_user_id, String message){
        
        User loggedUser = (User) session.get(User.class, Integer.parseInt(logged_user_id));
        
        User otherUser = (User) session.get(User.class, Integer.parseInt(other_user_id));
        
        Chat_Status chat_Status = (Chat_Status) session.get(Chat_Status.class, 2);
        
        Chat chat = new Chat();
        chat.setChat_Status(chat_Status);
        chat.setDate_time(new Date());
        chat.setFrom_user(loggedUser);
        chat.setTo_user(otherUser);
        chat.setMessage(message);
        
        session.save(chat);
        
        session.beginTransaction().commit();
    }
    
    public JsonObject receiveChats(String loggedUserId, String selectedUserId){
        Gson gson = new Gson();
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        User loggedUser = (User) session.get(User.class, Integer.parseInt(loggedUserId));
        
        User selectedUser = (User) session.get(User.class, Integer.parseInt(selectedUserId));
        
        Criteria criteria1 = session.createCriteria(Chat.class);
        criteria1.add(Restrictions.or(
                Restrictions.and(
                        Restrictions.eq("from_user", loggedUser),
                        Restrictions.eq("to_user", selectedUser)
                ),
                Restrictions.and(
                        Restrictions.eq("from_user", selectedUser),
                        Restrictions.eq("to_user", loggedUser)
                )
        ));
        
        criteria1.addOrder(Order.asc("date_time"));
        
        List<Chat> chatList = criteria1.list();
        
        Chat_Status chat_Status = (Chat_Status) session.get(Chat_Status.class, 1);
        
        JsonObject groupedChats = new JsonObject();
        
        SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        
        for (Chat chat : chatList) {
            
            String date = dateFormat.format(chat.getDate_time()); 
            
            JsonObject chatObject = new JsonObject();
            
            chatObject.addProperty("message", chat.getMessage());
            chatObject.addProperty("time", timeFormat.format(chat.getDate_time()));
            
            //other user chats 
            if(chat.getFrom_user().getId() == selectedUser.getId()){
                
                chatObject.addProperty("side", "left");
                
                if(chat.getChat_Status().getId() == 2){
                    chat.setChat_Status(chat_Status);
                    session.update(chat);
                }
            }else{
                chatObject.addProperty("side", "right");
                chatObject.addProperty("status", chat.getChat_Status().getId()); //1 = seen , 2 = unseen
            }
            
            if(!groupedChats.has(date)){
                groupedChats.add(date, new JsonArray());
            }
            
            groupedChats.getAsJsonArray(date).add(chatObject);
            
        }
        
        session.beginTransaction().commit();
        
        return groupedChats;
    }
    
}
