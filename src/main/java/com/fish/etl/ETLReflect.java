package com.fish.etl;

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

import com.fish.etl.tools.Param;
import com.utils.ConfigUtils;
import com.utils.RedisUtils;

public class ETLReflect {

	private static Properties prop;
	private static Pattern delimiterIn;
	private static String VERIFY = "verify"; // 验证反射类中的方法名
	private static String PROCESS = "process"; // 处理反射类中的方法名
	private static Map<String, Integer> fieldIndexMap;
	private static Map<String, Map<String, String>> redisMaps;
	private static Param param;
	private static Map<String, Method> classMap = new HashMap<String, Method>();
	private static Map<String, Method> verifyClassMap = new HashMap<String, Method>();

	public static Pattern split = Pattern.compile(",");
	private static String[] fields;
	// 验证字段可能为空 可能为null
	private static String[] verifyFields = null;
	
	private String delimiterOut;
	
	public ETLReflect(String propPath) {
		prop = ConfigUtils.getConfig(propPath);
		delimiterIn = Pattern.compile(prop.getProperty("DELIMITER_IN", ","));
		delimiterOut = prop.getProperty("DELIMITER_OUT", ",");
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
		initFieldIndexMap();  // 初始化字段索引Map
		initRedisMaps(); // 初始化redis table Map
		initMethod(); // 初始化class的method
		fields = split.split(prop.getProperty("DATA_OUT", EMPTY), 999); // 输出数组
		String verifyFiled = prop.getProperty("VERIFY_FIELD");
		if (isNotBlank(verifyFiled)) {
			verifyFields = split.split(verifyFiled);
		}
	}

	
	/**
	 * map循环调用
	 * @param value
	 * @return
	 */
	public String[] map(String value) {
		String[] originalValues = delimiterIn.split(value, 999); // 原始数据切分
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
		} else {
			System.err.println("verify error");
		}
		return null;
	}

	// 处理一个字段
	private static String process(String field) {
		if (isBlank(field)) {
			return EMPTY;
		}
		int index = fieldIndexMap.get(getRealField(field));
		param.setIndex(index);
		
		if (isBlank(getConfigClass(field))) {
			return param.getValue();
		} else {
			param.setParameter(getConfigParam(field));
			Method method = classMap.get(getConfigClassKey(field));
			try {
				return String.valueOf(method.invoke(null, param));
			} catch (Exception e) {
				System.err.printf("invoke method error:%s.%s\n", getConfigClass(field), PROCESS);
				e.printStackTrace();
				return EMPTY;
			}
		}
	}

	// 验证
	private static boolean verify(String[] verifyFields) {
		for (String verifyField : verifyFields) {
			String verifyClassName = getConfigVerifyClass(verifyField);
			if (isBlank(verifyClassName)) {
				continue;
			}
			param.setIndex(fieldIndexMap.get(getRealField(verifyField)));
			param.setParameter(getConfigVerifyParam(verifyField));
			try {
				Method method = verifyClassMap.get(getConfigVerifyClassKey(verifyField));
				String booleanResult = String.valueOf(method.invoke(null, param));
				boolean b = Boolean.parseBoolean(booleanResult);
				if (!b) {
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
		redisMaps.clear();
	}

	// init fieldIndexMap
	private static void initFieldIndexMap() {
		String[] dataIns = split.split(prop.getProperty("DATA_IN", EMPTY));
		fieldIndexMap = new HashMap<String, Integer>();
		for (int i = 0, n = dataIns.length; i < n; i++) {
			fieldIndexMap.put(dataIns[i].trim(), i);
		}
	}

	// init redisMaps
	private static void initRedisMaps() {
		String strRedisMaps = prop.getProperty("INIT_REDISMAP");
		redisMaps = new HashMap<String, Map<String, String>>();
		if (isNotBlank(strRedisMaps)) {
			String[] strs = split.split(strRedisMaps);
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
		for (Entry<Object, Object> entry : set) {
			if (StringUtils.endsWithIgnoreCase(String.valueOf(entry.getKey()), ".CLASS")
					&& isNotBlank(String.valueOf(entry.getValue()))) {
				try {
					Class<?> c = Class.forName(String.valueOf(entry.getValue()));
					try {
						Method method = c.getDeclaredMethod(PROCESS, Param.class);
						classMap.put(String.valueOf(entry.getKey()), method);
					} catch (SecurityException e) {
						System.err.printf("security error:%s.%s\n", entry.getValue(), PROCESS);
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						System.err.println("no such method error:" + PROCESS);
						e.printStackTrace();
					}
				} catch (ClassNotFoundException e) {
					System.err.println("init class error:" + entry.getValue());
					e.printStackTrace();
				}
			}else if (StringUtils.endsWithIgnoreCase(String.valueOf(entry.getKey()), ".VERIFYCLASS")
					&& isNotBlank(String.valueOf(entry.getValue()))) {
				try {
					Class<?> c = Class.forName(String.valueOf(entry.getValue()));
					try {
						Method method = c.getDeclaredMethod(VERIFY, Param.class);
						verifyClassMap.put(String.valueOf(entry.getKey()), method);
					} catch (SecurityException e) {
						System.err.printf("security error:%s.%s\n", entry.getValue(), VERIFY);
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						System.err.println("no such method error:" + VERIFY);
						e.printStackTrace();
					}
				} catch (ClassNotFoundException e) {
					System.err.println("init class error:" + entry.getValue());
					e.printStackTrace();
				}
			}
		}
	}

	// 输入字段名 返回配置文件中字段名.CLASS对应的value
	private static String getConfigClass(String key) {
		return prop.getProperty(key.concat(".CLASS"), EMPTY);
	}
	
	// 输入字段名 返回配置文件中字段名.CLASS对应的value
	private static String getConfigClassKey(String key) {
		return key.concat(".CLASS");
	}

	// 输入字段名 返回配置文件中字段名.CLASS对应的value
	private static String getConfigParam(String key) {
		return prop.getProperty(key.concat(".PARAMETER"), EMPTY);
	}

	// 输入字段名 返回配置文件中字段名.CLASS对应的value
	private static String getConfigVerifyClass(String key) {
		return prop.getProperty(key.concat(".VERIFYCLASS"), EMPTY);
	}
	
	// 输入字段名 返回配置文件中字段名.CLASS对应的value
	private static String getConfigVerifyClassKey(String key) {
		return key.concat(".VERIFYCLASS");
	}

	// 输入字段名 返回配置文件中字段名.CLASS对应的value
	private static String getConfigVerifyParam(String key) {
		return prop.getProperty(key.concat(".VERIFYPARAMETER"), EMPTY);
	}

	// 返回'前的真实字段明
	private static String getRealField(String field) {
		return StringUtils.substringBefore(field, "'");
	}
	
	public String getDelimiterOut() {
		return delimiterOut;
	}

	public void setDelimiterOut(String delimiterOut) {
		this.delimiterOut = delimiterOut;
	}
	public String getDefaultOuts(int len){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<len;i++){
			sb.append(delimiterOut);				
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		ETLReflect r = new ETLReflect("/config/reflect-test.properties");
		r.setup(null);
		String value = "a|245|vvv|ttp://www.baidu.com/wd?=|e|f|g|wwwwww";
		r.cleanup();
		System.out.println(StringUtils.join(r.map(value), ","));
	}
}
