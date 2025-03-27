package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "CheckUsernameAvailable", urlPatterns = {"/CheckUsernameAvailable"})
public class CheckUsernameAvailable extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        JsonObject jsonObject = new JsonObject();
        
        String username = req.getParameter("username");

        Criteria criteria1 = session.createCriteria(User.class);
        criteria1.add(Restrictions.eq("username", username));

        if (!criteria1.list().isEmpty()) {

            jsonObject.addProperty("success", true);
            jsonObject.addProperty("message", "Username not available. Please try another one.");

        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(jsonObject));
    }

}
