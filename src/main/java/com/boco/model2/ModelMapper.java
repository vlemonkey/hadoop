package com.boco.model2;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.boco.global.COUNTER;
import com.boco.global.MapperReflect;

public class ModelMapper extends Mapper<Object, Text, Text, Text>{
	
	private static final String PROPERTIES_PATH = "/config/model.properties";
	private static MapperReflect reflect = new MapperReflect(PROPERTIES_PATH, true);
	
	Text tk = new Text();
	Text tv = new Text();
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException{
		if (reflect.isNotInitParamObject()) { // 如果没有初始化param
			reflect = new MapperReflect(PROPERTIES_PATH, true);
		}
	}
	
	@Override
	public void map(Object key, Text value, Context context)
		throws IOException, InterruptedException{
		context.getCounter(COUNTER.MapperInput).increment(1);   // Mapper输入条数
		String[] rets = reflect.map(value.toString());
		System.out.println(Arrays.toString(rets));
		
		context.getCounter(COUNTER.MapperOutput).increment(1);  // Mapper输出条数
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		
	}
	
}
