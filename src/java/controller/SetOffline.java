package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import entity.User_Status;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;

@WebServlet(name = "SetOffline", urlPatterns = {"/SetOffline"})
public class SetOffline extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        String userId = req.getParameter("id");
        
        User user = (User) session.get(User.class, Integer.parseInt(userId));
        
        User_Status user_Status = (User_Status) session.get(User_Status.class, 2);
        
        user.setUser_Status(user_Status);
        session.update(user);
        session.beginTransaction().commit();
        
        session.close();

        responseJson.addProperty("success", true);

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));

    }

}
