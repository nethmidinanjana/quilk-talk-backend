
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
@WebServlet(name = "UploadProfilePic", urlPatterns = {"/UploadProfilePic"})
public class UploadProfilePic extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        String mobile = req.getParameter("mobile");
        Part profileImage = req.getPart("profileImage");
        
        System.out.println(mobile);
        System.out.println(profileImage.getSubmittedFileName());
        
        Criteria criteria1 = session.createCriteria(User.class);
        criteria1.add(Restrictions.eq("mobile", mobile));
        
        if(criteria1.list().isEmpty()){
            responseJson.addProperty("message", "Unable to get the user");
        }else{
            // upload image
            if(profileImage != null){
                String applicationPath = req.getServletContext().getRealPath("");
                String newApplicationPath = applicationPath.replace("build"+File.separator+"web", "web");
               
                String profilepicPath = newApplicationPath+ File.separator +"profile-images"+ File.separator + mobile+".png";
                File file = new File(profilepicPath);
                Files.copy(profileImage.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            
            responseJson.addProperty("success", true);
            
        }
        
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJson));
        
        
    }

    
}
