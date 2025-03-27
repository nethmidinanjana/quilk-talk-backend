package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Stories;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

@WebServlet(name = "LoadStories", urlPatterns = {"/LoadStories"})
public class LoadStories extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();

        JsonArray storiesArray = new JsonArray();

        Calendar startOfToday = Calendar.getInstance();
        startOfToday.set(Calendar.HOUR_OF_DAY, 0);
        startOfToday.set(Calendar.MINUTE, 0);
        startOfToday.set(Calendar.SECOND, 0);
        startOfToday.set(Calendar.MILLISECOND, 0);
        Date startDate = startOfToday.getTime();

        Calendar endOfToday = Calendar.getInstance();
        endOfToday.set(Calendar.HOUR_OF_DAY, 23);
        endOfToday.set(Calendar.MINUTE, 59);
        endOfToday.set(Calendar.SECOND, 59);
        endOfToday.set(Calendar.MILLISECOND, 999);
        Date endDate = endOfToday.getTime();

        Criteria criteria1 = session.createCriteria(Stories.class);
        criteria1.add(Restrictions.ge("date_time", startDate));
        criteria1.add(Restrictions.le("date_time", endDate));
        criteria1.addOrder(Order.desc("date_time"));

        List<Stories> storiesToday = criteria1.list();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

        for (Stories stories : storiesToday) {
            JsonObject storiesObject = new JsonObject();

            String serverPath = req.getServletContext().getRealPath("");
            String newApplicationPath = serverPath.replace("build" + File.separator + "web", "web");

            String otherUserAvatarImagePath = newApplicationPath + File.separator + "profile-images" + File.separator + stories.getUser().getMobile() + ".png";
            File otherUserAvatarImageFile = new File(otherUserAvatarImagePath);
            System.out.println(otherUserAvatarImagePath);

            if (otherUserAvatarImageFile.exists()) {
                storiesObject.addProperty("profile_image_found", true);
            } else {
                storiesObject.addProperty("profile_image_found", false);
                char firstLetter = Character.toUpperCase(stories.getUser().getUsername().charAt(0));
                storiesObject.addProperty("other_username_letter", firstLetter);
            }

            storiesObject.addProperty("id", stories.getId());
            storiesObject.addProperty("message", stories.getMessage());
            storiesObject.addProperty("date_time", dateFormat.format(stories.getDate_time()));
            storiesObject.addProperty("userMobile", stories.getUser().getMobile());

            storiesArray.add(storiesObject);
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(storiesArray));

    }

}
