package chat;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.vladsch.flexmark.ast.Document;
import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;

import chat.db.Log;
import chat.util.DateUtils;
import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.textile.TextileDialect;

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
					Chat.logDao.insert(sender = Chat.userUsernameMap.get(user),  msg = textToHtml(param), sayDate);

	    		Chat.broadcastMessage( cmd, sender,msg, sayDate );
	    	}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
    }

    private String mrkDownToHtml(String src){
    	MutableDataHolder options =
    			new MutableDataSet()
    			.setFrom(ParserEmulationProfile.GITHUB_DOC)
    			.set(Parser.EXTENSIONS, Arrays.asList(
                        AbbreviationExtension.create(),
                        DefinitionExtension.create(),
                        FootnoteExtension.create(),
                        TablesExtension.create(),
                        TypographicExtension.create()
                        ));;
    	Parser parser = Parser.builder(options).build();
    	Document doc = parser.parse(src);
    	HtmlRenderer renderer = HtmlRenderer.builder(options).build();
    	String html = renderer.render(doc);
    	return html;
    }

	private String textToHtml(String textile){
		StringWriter sw = new StringWriter();

		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(sw);
		builder.setEmitAsDocument(false);

		MarkupParser parser = new MarkupParser(new TextileDialect());
		parser.setBuilder(builder);
		parser.parse(textile);

		return sw.toString();
	}
}
