package com.boco.global;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.utils.ConfigUtils;
import com.utils.RedisUtils;

public class MapperReflect {

	private static final int MAX_SPLIT = -1; // split切分 最大数组长度
	private static final String SUFFIX_CLASS = ".CLASS"; // .CLASS 后缀
	private static final String SUFFIX_VERIFY_CLASS = ".VERIFYCLASS"; // .VERIFYCLASS后缀

	private static Properties prop;
	private static Pattern delimiterIn;
	private static Pattern splitOut;
	private static String VERIFY = "verify"; // 验证反射类中的方法名
	private static String PROCESS = "process"; // 处理反射类中的方法名
	private static Map<String, Integer> fieldIndexMap; // 输入文件字段索引
	private static Map<String, Map<String, String>> redisMaps = null; // redis
																		// Map集合
	private static Map<String, Method> classMap = new HashMap<String, Method>(); // 反射类方法集合
	private static Map<String, Method> verifyClassMap = new HashMap<String, Method>(); // 验证反射类方法集合
	private static Param param;

	private static String[] fields; // 输出数组
	// 验证字段可能为空 可能为null
	private static String[] verifyFields = null;

	public String DELIMITER_OUT;

	// 构造函数
	public MapperReflect(String propPath) {
		prop = ConfigUtils.getConfig(propPath);
		delimiterIn = Pattern.compile(prop.getProperty("DELIMITER_IN", ","));
		DELIMITER_OUT = prop.getProperty("DELIMITER_OUT", ",");
		splitOut = Pattern.compile(DELIMITER_OUT);
	}
	
	// 构造函数
	public MapperReflect(Properties properties) {
		prop = properties;
		delimiterIn = Pattern.compile(prop.getProperty("DELIMITER_IN", ","));
		DELIMITER_OUT = prop.getProperty("DELIMITER_OUT", ",");
		splitOut = Pattern.compile(DELIMITER_OUT);
	}

	/**
	 * 初始化调用
	 */
	public void setup() {
		initAll();
		param = new Param(redisMaps); // 初始化参数类
	}

	/**
	 * 初始化调用
	 * 
	 * @param mos
	 */
	public void setup(MultipleOutputs<NullWritable, Text> mos) {
		initAll();
		param = new Param(redisMaps, mos); // 初始化参数类
	}

	/**
	 * 初始化所有公用变量
	 */
	private static void initAll() {
		initFieldIndexMap(); // 初始化输入文件字段索引Map
		initRedisMaps(); // 初始化redis table Map
		initMethod(); // 初始化class的method
		fields = Constants.configSplit.split(prop.getProperty("DATA_OUT", EMPTY), MAX_SPLIT); // 输出数组
		String verifyFiled = prop.getProperty("VERIFY_FIELD");
		if (isNotBlank(verifyFiled)) {
			verifyFields = Constants.configSplit.split(verifyFiled); // 需要验证的数组
		}
		verifyFiled = null;
	}

	/**
	 * map循环调用
	 * 
	 * @param value
	 * @return
	 */
	public String[] map(String value) {
		String[] originalValues = delimiterIn.split(value, MAX_SPLIT); // 原始数据切分
		param.setOriginalValues(originalValues); // 设置变量

		String[] rets = null; // 保存结果数据
		// 验证
		if (verifyFields == null || verify(verifyFields)) {
			rets = new String[fields.length];

			// field-对应字段名称
			int i = 0;
			for (String field : fields) {
				rets[i++] = process(field);
			}

			return rets;
		} 
		
		System.err.println("verify error");
		return null;
	}

	// 处理一个字段
	private static String process(String field) {
		if (isBlank(field)) {
			return EMPTY;
		}
		int index = fieldIndexMap.get(getRealField(field));
		param.setIndex(index);

		String configClassKey = getConfigClassKey(field);
		if (null == classMap || !classMap.containsKey(configClassKey)) {
			configClassKey = null;
			return param.getValue();
		}
		
		Method method = classMap.get(configClassKey);
		param.setParameter(getConfigParam(field));
		try {
			return String.valueOf(method.invoke(null, param));
		} catch (Exception e) {
			System.err.printf("invoke method error:%s.%s\n",
					configClassKey, PROCESS);
			e.printStackTrace();
			return EMPTY;
		} finally {
			configClassKey = null;
		}
	}

	// 验证
	private static boolean verify(String[] verifyFields) {
		for (String verifyField : verifyFields) {
			Method method = verifyClassMap.get(getConfigVerifyClassKey(verifyField));
			if (null == method) {
				continue;
			}

			param.setIndex(fieldIndexMap.get(getRealField(verifyField)));
			param.setParameter(getConfigVerifyParam(verifyField));
			try {
				String booleanResult = String.valueOf(method.invoke(null, param));
				if (StringUtils.equals(booleanResult, "false")) {
					return false;
				}
			} catch (Exception e) {
				System.err.printf("invoke method error:%s.%s\n", getConfigVerifyClassKey(verifyField), VERIFY);
				e.printStackTrace();
				return false;
			}
		}
 
		return true;
	}

	public void cleanup() {
		fieldIndexMap = null;
		redisMaps = null;
		classMap = verifyClassMap = null;
	}

	// init fieldIndexMap
	private static void initFieldIndexMap() {
		String[] dataIns = Constants.configSplit.split(prop.getProperty("DATA_IN", EMPTY));
		fieldIndexMap = new HashMap<String, Integer>();
		for (int i = 0, n = dataIns.length; i < n; i++) {
			fieldIndexMap.put(dataIns[i].trim(), i);
		}
	}

	// init redisMaps
	private static void initRedisMaps() {
		String strRedisMaps = prop.getProperty("INIT_REDISMAP");
		if (isNotBlank(strRedisMaps)) {
			redisMaps = new HashMap<String, Map<String, String>>();
			String[] strs = Constants.configSplit.split(strRedisMaps);
			for (String tableName : strs) {
				redisMaps.put(tableName, RedisUtils.findCode2CodeIDMap(tableName));
			}
		}
	}

	/**
	 * 初始化class的method和verfiyclass的method
	 */
	private static void initMethod() {
		Set<Entry<Object, Object>> set = prop.entrySet();
		String key = null;
		String value = null;
		for (Entry<Object, Object> entry : set) {
			key = String.valueOf(entry.getKey());
			value = null != entry.getValue() ? String.valueOf(entry.getValue()) : null;
			
			if (endWithString(key, value, SUFFIX_CLASS)) {
				Class<?> c;
				Method method = null;
				try {
					c = Class.forName(value);
					method = c.getDeclaredMethod(PROCESS, Param.class);
				} catch (Exception e) {
					System.err.println("init class error:" + value);
					e.printStackTrace();
				}
				classMap.put(key, method);
			} else if (endWithString(key, value, SUFFIX_VERIFY_CLASS)) {
				Class<?> c;
				Method method = null;
				try {
					c = Class.forName(value);
					method = c.getDeclaredMethod(VERIFY, Param.class);
				} catch (Exception e) {
					System.err.println("init class error:" + value);
					e.printStackTrace();
				}
				verifyClassMap.put(key, method);
			}
		}
		set = null; key = value = null;
	}

	/**
	 * 判断key是否以strEnd结尾 并且value不为空
	 * 
	 * @param key
	 * @param value
	 * @param strEnd
	 * @return
	 */
	 private static boolean endWithString(String key, String value, String strEnd) { 
		 return StringUtils.endsWithIgnoreCase(key, strEnd) && 
				 null != value; 
	 }

	// 输入字段名 返回配置文件中字段名.CLASS对应的value
	private static String getConfigClassKey(String key) {
		return key.concat(SUFFIX_CLASS);
	}

	// 输入字段名 返回配置文件中字段名.CLASS对应的value
	private static String getConfigVerifyClassKey(String key) {
		return key.concat(SUFFIX_VERIFY_CLASS);
	}

	// 输入字段名 返回配置文件中字段名.CLASS对应的value
	private static String getConfigParam(String key) {
		return prop.getProperty(key.concat(".PARAMETER"), EMPTY);
	}

	// 输入字段名 返回配置文件中字段名.CLASS对应的value
	private static String getConfigVerifyParam(String key) {
		return prop.getProperty(key.concat(".VERIFYPARAMETER"), EMPTY);
	}

	// 返回'前的真实字段明
	private static String getRealField(String field) {
		return StringUtils.substringBefore(field, "'");
	}

	public Pattern getSplitOut() {
		return splitOut;
	}

	public void setSplitOut(Pattern splitOut) {
		MapperReflect.splitOut = splitOut;
	}

	public String getProperty(String key) {
		return prop.getProperty(key);
	}

	public String getProperty(String key, String defaultValue) {
		return prop.getProperty(key, defaultValue);
	}

	public static void main(String[] args) {
		long l = System.currentTimeMillis();
		MapperReflect r = new MapperReflect("/config/hour/TwSeHostOrderH.properties");
		r.setup();
		String value = "2013-10-18 10:10	2013-10-18 10:12	11111	15045782541	33333333	34567	1	1	1	1	1	http://www.baidu.com	1	2	2	111	11	22	CMWAP	1	22	22	22	22	22	22	22	22	2013-10-18 20:21";
		long l2 = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			StringUtils.join(r.map(value), ",");
		}
		long l3 = System.currentTimeMillis();
		System.out.println(l3 - l2);
		System.out.println(l2 - l);

		System.out.println(StringUtils.join(r.map(value), ","));
		r.cleanup();
	}
}
