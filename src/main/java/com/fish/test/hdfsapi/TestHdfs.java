/**
 * 
 */
package com.fish.test.hdfsapi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * @author wq
 *
 */
public class TestHdfs {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String path = "hdfs://cloud138:8020/user/tescomm/zcq/second_platform/t_user_exp/test.sql";
		String content = "";
		Configuration conf = new Configuration();
		FileSystem hdfs;
		 conf.addResource(new Path("core-site.xml"));
		 conf.addResource(new Path("hdfs-site.xml"));
		System.out.println(conf.get("fs.defaultFS"));
		// conf.addResource(new
		// Path("/Users/wq/opt/hadoop-2.6.0-cdh5.4.0-wq/etc/hadoop/mapred-site.xml"));
		try {
			hdfs = FileSystem.get(conf);
			Path p = new Path(path);
			System.out.println(p.toUri().getAuthority());

			// FSDataInputStream is = hdfs.open(p);
			//
			// // get the file info to create the buffer
			// FileStatus stat = hdfs.getFileStatus(p);
			//
			// // create the buffer
			// byte[] buffer = new
			// byte[Integer.parseInt(String.valueOf(stat.getLen()))];
			// is.readFully(0, buffer);
			// is.close();
			// hdfs.close();
			// content = new String(buffer);
			// System.out.println("content:"+content);
			// ----------------------------------------
			InputStream in = hdfs.open(p);
			BufferedReader buff = null;
			buff = new BufferedReader(new InputStreamReader(in));
			String str = null;
			while ((str = buff.readLine()) != null) {

				System.out.println(str);
			}
			buff.close();
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
