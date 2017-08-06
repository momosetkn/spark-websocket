package chat.db;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;

public class StringConv implements Converter<String> {

	@Override
	public String convert(Object val) throws ConverterException {
		// TODO 自動生成されたメソッド・スタブ
		String ret = null;
		if( val instanceof Timestamp ) {
			ret = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(val);
		}else {
			return (String) val;
		}
		return ret;
	}

	@Override
	public Object toDatabaseParam(String val) {
		// TODO 自動生成されたメソッド・スタブ
		try {
			return new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(val);
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			System.out.println("");
			//e.printStackTrace();
			return val;
		}
	}

}
