package chat.db;

public class Log {
	private int dispNo;
	private String sender;
	private String msg;
	private String sayDate;

	public int getDispNo() {
		return dispNo;
	}

	public void setDispNo(int disp_no) {
		this.dispNo = disp_no;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSayDate() {
		return sayDate;
	}

	public void setSayDate(String saydate) {
		this.sayDate = saydate;
	}

	@Override
	public String toString() {
		return "Log [disp_no=" + dispNo + ", sender=" + sender + ", msg=" + msg + ", saydate=" + sayDate + "]";
	}


}
