package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "UpdateProfile", urlPatterns = {"/UpdateProfile"})
public class UpdateProfile extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        Session session = HibernateUtil.getSessionFactory().openSession();

        String username = req.getParameter("username");
        String userId = req.getParameter("userId");
        Part profileImg = req.getPart("profilePic");

        System.out.println(username);
        System.out.println(userId);
        System.out.println(profileImg.getSubmittedFileName());

        User user = (User) session.get(User.class, Integer.parseInt(userId));

        if (username != null && !username.isEmpty()) {

            System.out.println(user.getUsername());
            System.out.println(username);
            
            if (!user.getUsername().equals(username)) {

                Criteria criteria1 = session.createCriteria(User.class);
                criteria1.add(Restrictions.eq("username", username));
                //the current user's username is not equal to the username that gets through the url 
                if (!criteria1.list().isEmpty()) {
                    //username already used
                    responseJson.addProperty("success", false);
                    responseJson.addProperty("message", "This username is not available. Please try another one.");

                    resp.setContentType("application/json");
                    resp.getWriter().write(gson.toJson(responseJson));
                    return;
                } else {
                    user.setUsername(username);
                    session.save(user);
                    session.beginTransaction().commit();
                    responseJson.addProperty("success", true);
                }
            }

        }

        if (profileImg != null) {
            String applicationPath = req.getServletContext().getRealPath("");
            String newApplicationPath = applicationPath.replace("build" + File.separator + "web", "web");

            String profilepicPath = newApplicationPath + File.separator + "profile-images" + File.separator + user.getMobile() + ".png";
            File file = new File(profilepicPath);
            Files.copy(profileImg.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

            responseJson.addProperty("success", true);
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));

    }

}
