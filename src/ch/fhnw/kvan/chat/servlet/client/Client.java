package ch.fhnw.kvan.chat.servlet.client;
import ch.fhnw.kvan.chat.gui.ClientGUI;
import ch.fhnw.kvan.chat.interfaces.IChatRoom;
import ch.fhnw.kvan.chat.utils.In;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by btemperli on 24.03.15.
 */
public class Client implements IChatRoom {

    private static Logger log;
    private static ClientGUI gui;
    private String serverUrl;
    private String name;
    private HashSet<String> participantList = new HashSet<String>();
    private HashSet<String> topicsList = new HashSet<String>();
    private HashMap<String, List<String>> messagesMap = new HashMap<String, List<String>>();

    public static void main(String[] args) throws IOException {
        log = Logger.getLogger(Client.class);

        Client client = new Client(args[0], args[1]);
        client.addParticipant(client.name);
    }

    public Client(String name, String url) {
        log.info("create client " + name);

        this.name = name;
        this.serverUrl = "http://" + url;

        gui = new ClientGUI(this, name);
    }

    @Override
    public boolean addParticipant(String name) throws IOException {
        In server = null;
        try {
            String queryParams = "?action=addParticipant&name=" + name;
            server = new In(new URL(serverUrl + queryParams));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        while (server != null && server.hasNextLine()) {
            log.error(server.readLine());
        }
        return true;
    }

    @Override
    public boolean removeParticipant(String name) throws IOException {
        In server = null;
        try {
            String queryParams = "?action=removeParticipant&name=" + name;
            server = new In(new URL(serverUrl + queryParams));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        while (server != null && server.hasNextLine()) {
            log.error(server.readLine());
        }
        return true;
    }

    @Override
    public boolean addTopic(String topic) throws IOException {
        if (!topicsList.contains(topic)) {
            topicsList.add(topic);
            messagesMap.put(topic, new ArrayList<String>());
            gui.addTopic(topic);

            In server = null;
            try {
                String queryParams = "?action=addTopic&topic=" + URLEncoder.encode(topic, "UTF-8");
                server = new In(new URL(serverUrl + queryParams));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            while (server != null && server.hasNextLine()) {
                log.error(server.readLine());
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean removeTopic(String topic) throws IOException {

        if (topic == null ||topic.equals("")) {
            return false;
        }

        topicsList.remove(topic);
        gui.removeTopic(topic);
        In server = null;

        try {
            String queryParams = "?action=removeTopic&topic=" + URLEncoder.encode(topic, "UTF-8");
            server = new In(new URL(serverUrl + queryParams));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        while (server != null && server.hasNextLine()) {
            log.error(server.readLine());
        }

        return true;
    }

    @Override
    public boolean addMessage(String topic, String message) throws IOException {

        if (topicsList.contains(topic)) {
            gui.addMessage(message);
            messagesMap.get(topic).add(message);
            In server = null;

            try {
                String queryParams = "?action=postMessage&message=" + URLEncoder.encode(message, "UTF-8") + "&topic=" + URLEncoder.encode(topic, "UTF-8");
                server = new In(new URL(serverUrl + queryParams));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            while (server != null && server.hasNextLine()) {
                log.error(server.readLine());
            }

            return true;
        }
        return false;
    }

    @Override
    public String getMessages(String topic) throws IOException {

        In server = null;

        try {
            String queryParams = "?action=getMessages&topic=" + URLEncoder.encode(topic, "UTF-8");
            server = new In(new URL(serverUrl + queryParams));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        while (server != null && server.hasNextLine()) {
            String input = server.readLine();
            Matcher matcher = Pattern.compile("\\=(.*)").matcher(input);

            if (matcher.find()) {
                handleMessages(matcher.group(1), topic);
            }
        }

        return "";
    }

    @Override
    public String refresh(String topic) throws IOException {

        In server = null;

        try {
            String queryParams = "?action=refresh&topic=" + URLEncoder.encode(topic, "UTF-8");
            server = new In(new URL(serverUrl + queryParams));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        while (server != null && server.hasNextLine()) {
            String input = server.readLine();
            Matcher matcher = Pattern.compile("\\=(.*)").matcher(input);
            if (matcher.find()) {
                if (input.startsWith("messages")) {
                    handleMessages(matcher.group(1), topic);
                } else if (input.startsWith("topics")) {
                    handleTopics(matcher.group(1));
                } else if (input.startsWith("participants")) {
                    handleParticipants(matcher.group(1));
                }
            }
        }

        return "";
    }

    private void handleMessages(String messages, String topic) {

        String[] messagesArray = messages.split(";;");

        for (String s : messagesArray) {
            if (topicsList.contains(topic)) {
                messagesMap.get(topic).add(s);
            }
        }

        if (messagesArray.length > 0) {
            gui.updateMessages(messagesArray);
        } else {
            gui.updateMessages(new String[]{""});
        }
    }

    private void handleTopics(String topics) {

        String[] topicsArray = topics.split(";");
        HashSet<String> tmp = new HashSet<String>();

        for (String s : topicsList) {
            tmp.add(s);
        }

        for (String s : topicsArray) {
            if (tmp.contains(s)) {
                tmp.remove(s);
            }

            if (topicsList.add(s)) {
                gui.addTopic(s);
                messagesMap.put(s, new ArrayList<String>());
            }
        }

        for(String s : tmp) {
            gui.removeTopic(s);
        }
    }

    private void handleParticipants(String participants) {

        String[] participantsArray = participants.split(";");
        HashSet<String> tmp = new HashSet<String>();

        for (String s : participantList) {
            tmp.add(s);
        }

        for (String s : participantsArray) {

            if(tmp.contains(s)) {
                tmp.remove(s);
            }

            if (!s.equals(this.name) && participantList.add(s)) {
                gui.addParticipant(s);
            }
        }

        for(String s : tmp) {
            gui.removeParticipant(s);
        }
    }
}
