package com.boco.sort;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.mapreduce.Partitioner;

import com.utils.StringTools;

//自己定义的key类应该实现WritableComparable接口
public class OrderObject implements WritableComparable<OrderObject> {

	private Text first;
	private Text second;

	public OrderObject() {
		set(new Text(), new Text());
	}

	public OrderObject(String first, String second) {
		set(new Text(first), new Text(second));
	}

	public OrderObject(Text first, Text second) {
		set(first, second);
	}

	public void set(Text first, Text second) {
		this.first = first;
		this.second = second;
	}

	public Text getFirst() {
		return first;
	}

	public long getSecond() {
		return Long.parseLong(second.toString());
	}
	
	@Override
	public String toString() {
		return StringTools.join(",", first, second);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof OrderObject) {
			o = (OrderObject)o;
			if (first.equals(((OrderObject) o).first)) {
				return true;
			}
		}
		return false;
	}

	//序列化，将OrderObject转化成使用流传送的二进制 
	@Override
	public void write(DataOutput out) throws IOException {
		first.write(out);
		second.write(out);
	}

	//反序列化，从流中的二进制转换成OrderObject
	@Override
	public void readFields(DataInput in) throws IOException {
		first.readFields(in);
		second.readFields(in);
	}

	//key的比较
	@Override
	public int compareTo(OrderObject o) {
		int cmp = first.compareTo(o.first);
		
		if (cmp != 0) {
			return cmp;
		}
		return getSecond() == o.getSecond() ? 0
				: (getSecond() < o.getSecond() ? 1 : -1);
	}

	/** 
     * 分组函数类。只要first相同就属于同一个组。 
     */  
	@SuppressWarnings("rawtypes")
	public static class FirstComparator extends WritableComparator{

		private static final Text.Comparator TEXT_COMPARATOR = new Text.Comparator();
		
		protected FirstComparator(Class<? extends WritableComparable> keyClass) {
			super(keyClass);
		}
		public FirstComparator(){
			super(OrderObject.class);
		}
		
		//一个字节一个字节的比，直到找到一个不相同的字节，然后比这个字节的大小作为两个字节流的大小比较结果。 
		public int compare(byte[] b1,int s1,int l1,byte[] b2,int s2,int l2){
				int firstL1 = 0;
				int firstL2 = 0;
				try {
					firstL1 = WritableUtils.decodeVIntSize(b1[s1]) + readVInt(b1,s1);
					firstL2 = WritableUtils.decodeVIntSize(b2[s2]) + readVInt(b2,s2);
				} catch (IOException e) {
					e.printStackTrace();
				}	
				return TEXT_COMPARATOR.compare(b1 , s1, firstL1 , b2 , s2 , firstL2);		
		}
		
		public int compare(WritableComparable a, WritableComparable b){
			if(a instanceof OrderObject && b instanceof OrderObject){
				return ((OrderObject)a).first.compareTo(((OrderObject)b).first);
			}
			return super.compare(a, b);
		}
		
	}
	
	/** 
     * 分区函数类。根据first确定Partition。 
     */  
	public static class KeyPartitioner extends Partitioner<OrderObject, Text> {

		@Override
		public int getPartition(OrderObject key, Text value, int numPartitions) {
			return (key.getFirst().hashCode() & Integer.MAX_VALUE)
					% numPartitions;
		}

	}

}
