package com.boco.global;

import java.util.Map;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class Param {

	private String[] originalValues; // 原始数据切分后的数组
	private Map<String, Map<String, String>> map; // redis的tableMap数组
	private int index; // 当前处理的字段索引
	private String parameter; // 传入参数
	private MultipleOutputs<NullWritable, Text> mos;
	
	public Param(Map<String, Map<String, String>> map) {
		this.map = map;
	}
	
	public Param(Map<String, Map<String, String>> map, MultipleOutputs<NullWritable, Text> mos) {
		this.map = map;
		this.mos = mos;
	}
	
	// 根据索引返回对应value值
	public String getValue() {
		return this.originalValues[this.index];
	}

	public String[] getOriginalValues() {
		return originalValues;
	}

	public void setOriginalValues(String[] originalValues) {
		this.originalValues = originalValues;
	}

	public Map<String, Map<String, String>> getMap() {
		return map;
	}

	public void setMap(Map<String, Map<String, String>> map) {
		this.map = map;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public MultipleOutputs<NullWritable, Text> getMos() {
		return mos;
	}

	public void setMos(MultipleOutputs<NullWritable, Text> mos) {
		this.mos = mos;
	}
}
