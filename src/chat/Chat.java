package chat;

import static j2html.TagCreator.*;
import static spark.Spark.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import chat.db.LogDao;

public class Chat {

    // this map is shared between sessions and threads, so it needs to be thread-safe (http://stackoverflow.com/a/2688817)
	static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();

    static int nextUserNumber = 1; //Assign to username for next connecting user
    static LogDao logDao;

    public static void main(String[] args) {
    	try {
    		port(4568);
    		logDao = new LogDao();
	        staticFiles.location("/webresources"); //index.html is served at localhost:4567 (default port)
	        staticFiles.expireTime(600);
	        webSocket("/chat", ChatWebSocketHandler.class);
	        init();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}



    }

    //Sends a message from one user to all users, along with a list of current usernames
    public static void broadcastMessage( String cmd, String sender, String message, String sayDate) {

    	Set<String> nickSet = new HashSet<>();
    	userUsernameMap.values().stream().forEach(nick -> {
    		nickSet.add(nick);
    	});
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                sendMessage( session, cmd, sender, message, sayDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    //Sends a message from one user to all users, along with a list of current usernames
    public static void sendMessage( Session session, String cmd, String sender, String message, String sayDate) {
    	System.out.println(sender+ ":"+message);
        try {
        	String sendStr = String.valueOf(new JSONObject()
                	.put("cmd", cmd)
                    //.put("userMessage", createHtmlMessageFromSender(sender, message, sayDate))
                	.put("sender", sender)
                	.put("saydate", sayDate)
                	.put("message", message)
                    .put("userlist", userUsernameMap.values())
                );
            session.getRemote().sendString( sendStr );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Builds a HTML element with a sender-name, a message, and a timestamp,
    private static String createHtmlMessageFromSender(String sender, String message, String sayDate) {
    	System.out.println("メッセージ＝"+sender+ ":"+message+":"+sayDate);
    	if( null==sender||null== message||null== sayDate ){
    		System.out.println("null desu");
    		return null;
    	}
        return article(
            b(sender + " says:"),
            span(attrs(".timestamp"), sayDate ),
            //new SimpleDateFormat("HH:mm:ss").format(
            div(message)
        ).render();
    }

}
