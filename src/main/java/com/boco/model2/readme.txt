开发规范

1.可以公用的变量，声明成 private static, 并且在setup方法外面  例如
private static Map<String, String> redisMap = RedisUtils.findCode2CodeIDMap("tableName");

2.获取配置文件方法 
ConfigUtils.getConfig(propPath);
此方法是先通过hdfs的distributedcache获取
如果返回null，则读取本地/config下的配置文件

3.properties配置文件的写法
config/global.properties 设置了采用哪个全局配置文件
develop.properties表示开发环境时才用的全局配置文件
product.properties标识生产环境下用的全局配置文件
例如:redis的host和redis的port；mysql的host，mysql的port等
# REDIS 
REDIS.HOST=10.0.7.239
REDIS.PORT=6379

#mysql
MYSQL.HOST=10.0.7.216
MYSQL.PORT=3306
建议全局变量统一写在相应的.properties里面--两份

使用：
在某个配置文件中使用时，如jdbc.properties中使用，使用如下方法配置
#mysql
mysql.driverClassName=com.mysql.jdbc.Driver
mysql.url=jdbc:mysql://${MYSQL.HOST}:${MYSQL.PORT}/test
mysql.username=hive
mysql.password=hive



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
	
4.GlobalTools.isDebug() 标识是否开启调试模式

5.容量调度器--似乎yarn框架已经废弃这个 暂时不配置

