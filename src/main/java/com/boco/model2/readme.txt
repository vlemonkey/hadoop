开发规范

1.可以公用的变量，声明成 public static, 并且在setup方法外面  例如
private static Map<String, String> redisMap = RedisUtils.findCode2CodeIDMap("tableName");



Mapper && Reducer

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
	


