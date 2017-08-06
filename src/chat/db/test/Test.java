package chat.db.test;

import java.util.List;

import chat.db.Log;
import chat.db.LogDao;

public class Test {

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		LogDao logDao;
		try {
			logDao = new LogDao();
			//logDao.createTable();
			logDao.insert("us","aaaa", "2017/08/05 12:01");
			logDao.insert("u","bbbb", null);
			List<Log> logList = logDao.select(100);
			System.out.println(logList);

		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}
}
