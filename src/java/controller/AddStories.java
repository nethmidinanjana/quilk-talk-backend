package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Stories;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import org.hibernate.Session;

@MultipartConfig
@WebServlet(name = "AddStories", urlPatterns = {"/AddStories"})
public class AddStories extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        Session session = HibernateUtil.getSessionFactory().openSession();

        String uid = req.getParameter("uid");
        String message = req.getParameter("message");
        Part storyImg = req.getPart("storyImage");

        System.out.println(uid);
        System.out.println(message);
        System.out.println(storyImg.getSubmittedFileName());

        if (uid.isEmpty()) {
            responseJson.addProperty("message", "Something went wrong.");
            System.out.println("empty");
        } else if (storyImg == null || storyImg.getSize() == 0) {
            responseJson.addProperty("message", "Please select an image.");
            System.out.println("empty img");
        } else {
            User user = (User) session.get(User.class, Integer.parseInt(uid));
            Stories stories = new Stories();
            System.out.println(user.getId());
            if (message.isEmpty()) {
                //no message
                stories.setUser(user);
                stories.setDate_time(new Date());
            } else {
                //has message
                stories.setUser(user);
                stories.setDate_time(new Date());
                stories.setMessage(message);
            }

            int id = (int) session.save(stories);
            session.beginTransaction().commit();
            System.out.println(id);
            String applicationPath = req.getServletContext().getRealPath("");
            String newApplicationPath = applicationPath.replace("build" + File.separator + "web", "web");
            System.out.println(newApplicationPath);
            String storyImagePath = newApplicationPath + File.separator + "story-images" + File.separator + id + ".png";
            System.out.println(storyImagePath);

            File file = new File(storyImagePath);
            Files.copy(storyImg.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

            responseJson.addProperty("success", true);
            responseJson.addProperty("message", "Successfully uploaded.");

        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));

    }

}
