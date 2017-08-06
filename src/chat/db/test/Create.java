package chat.db.test;

import chat.db.LogDao;

public class Create {

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		LogDao logDao;
		try {
			logDao = new LogDao();
			logDao.createTable();
			//logDao.insert("user","aaaa");


		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}


	}

}
