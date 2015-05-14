package ch.fhnw.kvan.chat.servlet.server;

import ch.fhnw.kvan.chat.general.ChatRoom;
import ch.fhnw.kvan.chat.general.ChatRoomDriver;

import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by btemperli on 17.03.15.
 */
public class Server extends HttpServlet {

    private ChatRoomDriver chatRoomDriver;
    private ConnectionHandler connectionHandler;

    @Override
    public void init(ServletConfig servletconfig) throws ServletException {
        super.init(servletconfig);

        BasicConfigurator.configure();

        chatRoomDriver = new ChatRoomDriver();
        chatRoomDriver.connect("localhost", 1235);

        connectionHandler = new ConnectionHandler((ChatRoom)chatRoomDriver.getChatRoom());
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse) throws ServletException, IOException {

        httpServletResponse.setContentType("text/html");

        String action = httpServletRequest.getParameter("action");
        if (action == null) {
            action = "";
        }


        if (action.equals("addParticipant")) {
            connectionHandler.setParticipant(httpServletResponse, httpServletRequest.getParameter("name"));
        } else if (action.equals("removeParticipant")) {
            connectionHandler.removeParticipant(httpServletResponse, httpServletRequest.getParameter("name"));
        } else if (action.equals("addTopic")) {
            connectionHandler.addTopic(httpServletResponse, httpServletRequest.getParameter("topic"));
        } else if (action.equals("removeTopic")) {
            connectionHandler.removeTopic(httpServletResponse, httpServletRequest.getParameter("topic"));
        } else if (action.equals("postMessage")) {
            connectionHandler.addMessage(httpServletResponse, httpServletRequest.getParameter("topic"), httpServletRequest.getParameter("message"));
        } else if (action.equals("getMessages")) {
            connectionHandler.getMessages(httpServletResponse, httpServletRequest.getParameter("topic"));
        } else if (action.equals("refresh")) {
            connectionHandler.refresh(httpServletResponse, httpServletRequest.getParameter("topic"));
        } else {
            PrintWriter out = httpServletResponse.getWriter();
            out.println("<h1>Chatroom</h1>");
        }
    }
}