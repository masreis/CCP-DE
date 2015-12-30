package com.example.hive;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

public class CurrentTime extends UDF {
	
	private static Date dt = new Date();
	Text _now = new Text();
	public Text evaluate(){
		
		_now.set(DateFormatUtils.format(dt, "HH:mm:ss"));
		
		return _now;
	}
}
