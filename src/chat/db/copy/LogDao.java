package chat.db.copy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class LogDao {

Connection conn;
Statement stmt;
	public LogDao() throws SQLException, ClassNotFoundException{
		connect();
	}
	public void createTable() throws SQLException {
		stmt.execute("drop SEQUENCE if exists disp_no");
		stmt.execute("create sequence disp_no");
		stmt.execute("drop table if exists log");
		//stmt.execute("create table log(id identity default disp_no.nextval not null, msg varchar(1024),pk(id) )");
		stmt.execute("create table log(disp_no identity default disp_no.nextval not null, msg varchar(1024) )");
	}
	public int insert(String msg) throws SQLException {
		stmt.execute( String.format("insert into log (value) values ( '%s' )", msg) );
		return 0;
	}

	private void connect() throws SQLException, ClassNotFoundException {
		 Class.forName("org.h2.Driver");
		 conn = DriverManager.getConnection("jdbc:h2:~/sample", "aa", "");
		 stmt = conn.createStatement();
	}

	public void close()  throws SQLException{
		conn.close();
	}
}
