package com.fish.test.outputtest;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class OutputTest<K, V> extends FileOutputFormat<K, V> {

	@Override
	public RecordWriter<K, V> getRecordWriter(TaskAttemptContext job)
			throws IOException, InterruptedException {
//		Path file = FileOutputFormat.getOutputPath(job);
//		String xmlPath = job.getConfiguration().get("xmlPath");
//		Path file = new Path(xmlPath);
//		FileSystem fs = file.getFileSystem(job.getConfiguration());
//		return new XmlRecordWriter<K, V>(fs.create(file));
		
		Path file = getDefaultWorkFile(job, ".xml");
		FileSystem fs = file.getFileSystem(job.getConfiguration());
		return new XmlRecordWriter<K, V>(fs.create(file));
	}

}
