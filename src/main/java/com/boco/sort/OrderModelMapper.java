package com.boco.sort;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class OrderModelMapper extends Mapper<Object, Text, OrderObject, Text>{
	
	@Override
	public void map(Object key, Text value, Context context)
		throws IOException, InterruptedException{
		String[] strs = value.toString().split(",", 3);
		OrderObject o = new OrderObject(strs[0], strs[1]);
		context.write(o, new Text(strs[2]));
	}
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException{
		
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		
	}
	
	
	/*
	// 不符合值域检查
	context.getCounter(COUNTER.RangCheck).increment(1); // 记录该错误类型总条数
	mos.write(NullWritable.get(), value, CounterUtils.getErrorDirectory(COUNTER.RangCheck));
	// 不符合编码规范检查
	context.getCounter(COUNTER.CodingStandards).increment(1); // 记录该错误类型总条数
	mos.write(NullWritable.get(), value, CounterUtils.getErrorDirectory(COUNTER.CodingStandards));
	// 不符合实体关键属性的完整率
	context.getCounter(COUNTER.Null).increment(1); // 记录该错误类型总条数
	mos.write(NullWritable.get(), value, CounterUtils.getErrorDirectory(COUNTER.Null));
	// 不符合属性合法性
	context.getCounter(COUNTER.Illegal).increment(1); // 记录该错误类型总条数
	mos.write(NullWritable.get(), value, CounterUtils.getErrorDirectory(COUNTER.Illegal));
	
	context.getCounter(COUNTER.MapperInput).increment(1);   // Mapper输入条数
	context.getCounter(COUNTER.MapperOutput).increment(1);  // Mapper输出条数
	context.getCounter(COUNTER.ReducerOutput).increment(1); // Reducer输出条数
	*/
}
