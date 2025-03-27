package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import entity.User_Status;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);

        JsonObject requestJson = gson.fromJson(req.getReader(), JsonObject.class);
        String mobile = requestJson.get("mobile").getAsString();
        String username = requestJson.get("username").getAsString();
        String password = requestJson.get("password").getAsString();

        System.out.println(mobile);
        System.out.println(username);
        System.out.println(password);

        Session session = HibernateUtil.getSessionFactory().openSession();

        //search mobile number
        Criteria criteria1 = session.createCriteria(User.class);
        criteria1.add(Restrictions.eq("mobile", mobile));
        
        if(!criteria1.list().isEmpty()){
            responseJson.addProperty("message", "Mobile number already registered");
        }else{
            
            //get user status
            User_Status user_Status = (User_Status) session.get(User_Status.class, 2);
            
            //add user
            User user = new User();
            user.setMobile(mobile);
            user.setPassword(password);
            user.setRegistered_datetime(new Date());
            user.setUser_Status(user_Status);
            user.setUsername(username);
            
            session.save(user);
            session.beginTransaction().commit();
            
            responseJson.addProperty("success", true);
            responseJson.addProperty("message", "Registration Completed");
            
        }
        
        session.close();
        
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));
    }

}
