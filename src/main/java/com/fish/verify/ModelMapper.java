package com.fish.verify;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.fish.global.COUNTER;
import com.fish.global.CounterUtils;

public class ModelMapper extends Mapper<Object, Text, Text, Text>{
	
	Text tk = new Text();
	Text tv = new Text();
	private static String verifyA = null, verifyB = null;
	
	private static MultipleOutputs<NullWritable, Text> mos;
	
	@Override
	public void map(Object key, Text value, Context context)
		throws IOException, InterruptedException{
		context.getCounter(COUNTER.MapperInput).increment(1); // 记录处理总条数
		
		// 不符合值域检查
		if (verifyA == verifyB) {
			context.getCounter(COUNTER.RangCheck).increment(1); // 记录该错误类型总条数
			mos.write(NullWritable.get(), value, CounterUtils.getErrorDirectory(COUNTER.RangCheck));
		}
		
		// 不符合编码规范检查
		if (verifyA == verifyB) {
			context.getCounter(COUNTER.CodingStandards).increment(1); // 记录该错误类型总条数
			mos.write(NullWritable.get(), value, CounterUtils.getErrorDirectory(COUNTER.CodingStandards));
		}
		
		// 不符合实体关键属性的完整率
		if (verifyA == verifyB) {
			context.getCounter(COUNTER.Null).increment(1); // 记录该错误类型总条数
			mos.write(NullWritable.get(), value, CounterUtils.getErrorDirectory(COUNTER.Null));
		}
		
		// 不符合属性合法性
		if (verifyA == verifyB) {
			context.getCounter(COUNTER.Illegal).increment(1); // 记录该错误类型总条数
			mos.write(NullWritable.get(), value, CounterUtils.getErrorDirectory(COUNTER.Illegal));
		}
		
		// 具体处理流程
//		context.getCounter(COUNTER.MapperOutput).increment(1); // 记录成功处理条数
		
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setup(Context context) throws IOException, InterruptedException{
		mos = new MultipleOutputs(context);
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		mos.close();
	}
	
}
