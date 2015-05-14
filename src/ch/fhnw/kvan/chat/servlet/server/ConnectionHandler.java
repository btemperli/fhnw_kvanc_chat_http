package ch.fhnw.kvan.chat.servlet.server;

import ch.fhnw.kvan.chat.general.ChatRoom;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by btemperli on 24.03.15.
 */
public class ConnectionHandler {

    private final Pattern PATTERN = Pattern.compile("(.*?)");
    private static Logger log;
    private ChatRoom chatRoom;


    public ConnectionHandler(ChatRoom cr) {
        log = Logger.getLogger(ConnectionHandler.class);
        chatRoom = cr;
    }

    public void setParticipant(HttpServletResponse resp, String name) throws IOException {
        log.info("Add participant " + name);
        Matcher matcher = PATTERN.matcher(name);
        if (matcher.find()) {
            chatRoom.addParticipant(matcher.group(1));
        } else {
            resp.getWriter().println(name + " could not be added.");
        }
    }

    public void removeParticipant(HttpServletResponse resp, String name) throws IOException {
        log.info("Remove participant " + name);
        Matcher matcher = PATTERN.matcher(name);
        if (matcher.find()) {
            chatRoom.removeParticipant(matcher.group(1));
        } else {
            resp.getWriter().println(name + " could not be removed.");
        }
    }

    public void addTopic(HttpServletResponse resp, String topic) throws IOException {
        log.info("Add topic " + topic);
        Matcher matcher = PATTERN.matcher(topic);
        if (matcher.find()) {
            chatRoom.addTopic(matcher.group(1));
        } else {
            resp.getWriter().println(topic + " could not be added.");
        }
    }

    public void removeTopic(HttpServletResponse resp, String topic) throws IOException {
        log.info("Remove topic " + topic);
        Matcher matcher = PATTERN.matcher(topic);
        if (matcher.find()) {
            chatRoom.removeTopic(matcher.group(1));
        } else {
            resp.getWriter().println(topic + " could not be removed.");
        }
    }

    public void addMessage(HttpServletResponse resp, String topic, String message) throws IOException {
        log.info("Add message " + message);
        Matcher topicMatcher = PATTERN.matcher(topic);
        Matcher messageMatcher = PATTERN.matcher(message);
        if (topicMatcher.find() && messageMatcher.find()) {
            chatRoom.addMessage(topicMatcher.group(1), messageMatcher.group(1));
        } else {
            resp.getWriter().println(message + " could not be addedd to " + topic + ".");
        }
    }

    public void getMessages(HttpServletResponse resp, String topic) throws IOException {
        log.info("Get messages for topic " + topic);
        Matcher matcher = PATTERN.matcher(topic);
        if (matcher.find()) {
            resp.getWriter().println(chatRoom.getMessages(matcher.group(1)));
        } else {
            resp.getWriter().println("Messages not available");
        }
    }

    public void refresh(HttpServletResponse resp, String topic) throws IOException {
        log.info("Refresh for topic " + topic);
        resp.getWriter().println(chatRoom.getTopics());
        resp.getWriter().println(chatRoom.getParticipants());
        getMessages(resp, topic);
    }
}