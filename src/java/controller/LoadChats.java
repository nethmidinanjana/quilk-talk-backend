package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "LoadChats", urlPatterns = {"/LoadChats"})
public class LoadChats extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();

        String loggedUserId = req.getParameter("logged_user_id");
        String selectedUserId = req.getParameter("selected_user_id");

        JsonObject groupedChats = new ChatService().receiveChats(loggedUserId, selectedUserId);

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(groupedChats));
    }

}
