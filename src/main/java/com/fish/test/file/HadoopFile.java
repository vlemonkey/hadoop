package com.fish.test.file;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HadoopFile {
	public static void main(String[] args) throws IOException {
		String path = "/user/boco/bao/lq/zxout";
		
		read(path);
	}
	
	public static void read(String path) throws IOException {
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		FileStatus[]  status = fs.listStatus(new Path(path));
		for (FileStatus fileStatus : status) {
			System.out.println(fileStatus.toString());
		}
		
	}
}
