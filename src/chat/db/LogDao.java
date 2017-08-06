package chat.db;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.converters.Converter;
import org.sql2o.quirks.NoQuirks;

public class LogDao {
	Statement stmt;
	org.sql2o.Sql2o sql2o;
	public LogDao() throws SQLException, ClassNotFoundException{
		connect();
	}
	public void createTable() throws SQLException {
		try( Connection conn = sql2o.open() ){
			conn.createQuery("drop SEQUENCE if exists disp_no").executeUpdate();;
			//conn.createQuery("create sequence disp_no_seq").executeUpdate();;
			conn.createQuery("drop table if exists log").executeUpdate();;
			//conn.createQuery("create table log(id identity default disp_no.nextval not null, msg varchar(1024),pk(id) )").executeUpdate();;
			conn.createQuery("create table log(disp_no bigint auto_increment, sender varchar(128), msg varchar(1024), saydate varchar(19) )").executeUpdate();;
			//conn.createQuery("create table log(disp_no int , msg varchar(1024) )");
		}
	}
	public int insert(String sender,String msg,String saydate) throws SQLException {
		System.out.println("insertData="+sender+":"+msg+":"+saydate);
		try( Connection conn = sql2o.open() ){
			conn.createQuery( "insert into log (sender, msg, saydate) values ( :sender, :msg, :saydate )")
			.addParameter("sender", sender)
			.addParameter("msg", msg)
			.addParameter("saydate", saydate )
			.executeUpdate();
			//conn.commit();
			return conn.getResult();
		}
	}

	public List<Log> select(int limit) throws SQLException {
		try( Connection conn = sql2o.open() ){
			//List<Log> logList = conn.createQuery( String.format("select * from log order by desc disp_no limit %d", limit) )
			List<Log> logList = conn.createQuery( String.format("select * from log ") )
			.setAutoDeriveColumnNames(true)
			.executeAndFetch(Log.class);
			return logList;
		}
	}

	private void connect() throws SQLException, ClassNotFoundException {
		 //Class.forName("org.h2.Driver");
		Map<Class, Converter> converterMap = new HashMap<>();
		converterMap.put( String.class, new StringConv() );
		 sql2o = new Sql2o("jdbc:h2:./db/chat", "aa", "", new NoQuirks(converterMap) );
	}

	public void close()  throws SQLException{
	}

	private Date convDate(String saydate) {
		if( null==saydate ) {
			return null;
		}
		Date date = null;
		try {
		date = new SimpleDateFormat("yyyy-MM-dd hh:mm" ).parse(saydate);
		}catch(ParseException e) {
			return null;
		}
		return date;
	}
}
