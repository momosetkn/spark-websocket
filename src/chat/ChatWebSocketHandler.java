package chat;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import chat.db.Log;
import chat.util.DateUtils;

@WebSocket
public class ChatWebSocketHandler {

    private String sender, msg;

    static Pattern cmdPattern = Pattern.compile("(^/[a-z,A-Z]+)");
    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        String username = "User" + Chat.nextUserNumber++;
        //Chat.userUsernameMap.put(user, username);
        //Chat.broadcastMessage(sender = "Server", msg = (username + " joined the chat"));
        //ユーザー名は接続時ではなく、クライアントからnickコマンドで作らせるためこめあうと
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Chat.userUsernameMap.get(user);
        Chat.userUsernameMap.remove(user);
        Chat.broadcastMessage("/member", null, null, null);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
	    try {
	    	//String sayDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
	    	String sayDate = DateUtils.getNow4db();
	    	Matcher m = cmdPattern.matcher(message);
	    	m.find();
	    	String cmd = m.group();
	    	String param = message.replaceAll("^.*?\\s", "");
	    	if( "/LOG".equals(cmd.toUpperCase()) ) {
	    		List<Log> logList = Chat.logDao.select(256);
	    		System.out.println("logList="+logList);
	    		for( Log log : logList) {
	    			Chat.sendMessage( user, cmd, log.getSender(), log.getMsg(), log.getSayDate() );
	    		}
	    	}else if( "/MEMBER".equals(cmd.toUpperCase()) ) {
	                Chat.userUsernameMap.put(user, param);
	                Chat.broadcastMessage( cmd, null, null, null );
	    	}else if( "/MSG".equals(cmd.toUpperCase()) ) {

					Chat.logDao.insert(sender = Chat.userUsernameMap.get(user),  msg = sanitize4Html(param), sayDate);

	    		Chat.broadcastMessage( cmd, sender,msg, sayDate );
	    	}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
    }

    private String sanitize4Html(String src){
    	src = src.replaceAll("&" , "&amp;" );
    	src = src.replaceAll("<" , "&lt;"  );
    	src = src.replaceAll(">" , "&gt;"  );
    	src = src.replaceAll("\"", "&quot;");
    	src = src.replaceAll("'" , "&#39;" );
    	return src;
    }
}
