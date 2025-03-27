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
import org.hibernate.Session;

@WebServlet(name = "LoadUser", urlPatterns = {"/LoadUser"})
public class LoadUser extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        Session session = HibernateUtil.getSessionFactory().openSession();

        String uid = req.getParameter("id");

        if (uid.isEmpty()) {
            responseJson.addProperty("message", "Something went wrong.");
        } else {
            User user = (User) session.get(User.class, Integer.parseInt(uid));

            responseJson.addProperty("mobile", user.getMobile());
            responseJson.addProperty("userName", user.getUsername());
        }

        responseJson.addProperty("success", true);

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));

    }

}
