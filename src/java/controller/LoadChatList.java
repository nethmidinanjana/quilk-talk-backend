package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.Chat_Status;
import entity.User;
import entity.User_Status;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadChatList", urlPatterns = {"/LoadChatList"})
public class LoadChatList extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        responseJson.addProperty("message", "Unable to proceed.");

        Session session = HibernateUtil.getSessionFactory().openSession();

        String uid = req.getParameter("id");
        System.out.println(uid);
        String query = req.getParameter("query");

        try {

            if (uid.isEmpty()) {
                responseJson.addProperty("message", "Invalid user");
            } else {
                User user = (User) session.get(User.class, Integer.parseInt(uid));

                User_Status user_Status = (User_Status) session.get(User_Status.class, 1);

                user.setUser_Status(user_Status);

                session.update(user);

                //loading other users' data
                Criteria criteria1 = session.createCriteria(User.class);
                criteria1.add(Restrictions.ne("id", user.getId()));

                if (query != null && !query.isEmpty()) {
                    criteria1.add(Restrictions.like("username", "%" + query + "%"));
                }

                List<User> otherUserList = criteria1.list();
                List<JsonObject> chatItemList = new ArrayList<>();

                SimpleDateFormat dateTimeSortFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                //getting last message: begins
                for (User otheruser : otherUserList) {
                    Criteria criteria2 = session.createCriteria(Chat.class);
                    criteria2.add(
                            Restrictions.or(
                                    Restrictions.and(
                                            Restrictions.eq("from_user", user),
                                            Restrictions.eq("to_user", otheruser)
                                    ),
                                    Restrictions.and(
                                            Restrictions.eq("from_user", otheruser),
                                            Restrictions.eq("to_user", user)
                                    )
                            )
                    );

                    criteria2.addOrder(Order.desc("id"));
                    criteria2.setMaxResults(1);

                    JsonObject chatItemObj = new JsonObject();
                    chatItemObj.addProperty("other_user_id", otheruser.getId());
                    chatItemObj.addProperty("other_user_mobile", otheruser.getMobile());
                    chatItemObj.addProperty("other_user_name", otheruser.getUsername());
                    chatItemObj.addProperty("other_user_status", otheruser.getUser_Status().getId()); //1-> online , 2-> offline

                    String serverPath = req.getServletContext().getRealPath("");
                    String newApplicationPath = serverPath.replace("build" + File.separator + "web", "web");

                    String otherUserAvatarImagePath = newApplicationPath + File.separator + "profile-images" + File.separator + otheruser.getMobile() + ".png";
                    File otherUserAvatarImageFile = new File(otherUserAvatarImagePath);
                    System.out.println(otherUserAvatarImagePath);

                    if (otherUserAvatarImageFile.exists()) {
                        chatItemObj.addProperty("profile_image_found", true);
                    } else {
                        chatItemObj.addProperty("profile_image_found", false);
                        char firstLetter = Character.toUpperCase(otheruser.getUsername().charAt(0));
                        chatItemObj.addProperty("other_username_letter", firstLetter);
                    }

                    List<Chat> chatList = criteria2.list();

                    if (chatList.isEmpty()) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        chatItemObj.addProperty("message", "Say Hi! 👋🏻");
                        chatItemObj.addProperty("dateTime", dateFormat.format(user.getRegistered_datetime()));
                        chatItemObj.addProperty("chat_status_id", 1);

                        // Use sorting format for comparison
                        chatItemObj.addProperty("sortDateTime", dateTimeSortFormat.format(user.getRegistered_datetime()));
                    } else {
                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                        chatItemObj.addProperty("message", chatList.get(0).getMessage());
                        chatItemObj.addProperty("dateTime", timeFormat.format(chatList.get(0).getDate_time()));
                        chatItemObj.addProperty("chat_status_id", chatList.get(0).getChat_Status().getId());  // 1 = seen, 2 = unseen

                        // Use sorting format for comparison
                        chatItemObj.addProperty("sortDateTime", dateTimeSortFormat.format(chatList.get(0).getDate_time()));
                    }

                    //getting unseen message count: Begins              
                    Criteria criteria3 = session.createCriteria(Chat.class);
                    criteria3.add(Restrictions.and(
                            Restrictions.eq("from_user", otheruser),
                            Restrictions.eq("to_user", user)
                    ));

                    Chat_Status chat_Status = (Chat_Status) session.get(Chat_Status.class, 2);

                    criteria3.add(Restrictions.eq("chat_Status", chat_Status));

                    List<Chat> unseenChatList = criteria3.list();

                    int unseenChatCount = unseenChatList.size();

                    // Add the unseen chat count to the JSON object
                    chatItemObj.addProperty("unseen_chat_count", unseenChatCount);
                    //getting unseen message count: Ends

                    chatItemList.add(chatItemObj);

                }

                // Sorting chatItemList based on "sortDateTime" (newest first)
                Collections.sort(chatItemList, new Comparator<JsonObject>() {
                    @Override
                    public int compare(JsonObject o1, JsonObject o2) {
                        String dateTime1 = o1.get("sortDateTime").getAsString();
                        String dateTime2 = o2.get("sortDateTime").getAsString();
                        return dateTime2.compareTo(dateTime1);  // Sort in descending order
                    }
                });

                // Converting sorted list to JSON array
                JsonArray jsonChatArray = new JsonArray();
                for (JsonObject chatItem : chatItemList) {
                    jsonChatArray.add(chatItem);
                }

                responseJson.addProperty("success", true);
                responseJson.addProperty("message", "success");
                responseJson.addProperty("jsonChatArray", gson.toJson(jsonChatArray));

                session.beginTransaction().commit();
                session.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));

    }

}
