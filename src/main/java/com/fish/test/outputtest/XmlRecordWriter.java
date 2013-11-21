package com.fish.test.outputtest;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class XmlRecordWriter<K, V> extends RecordWriter<K, V> {

	private static final String utf8 = "UTF-8";

	private DataOutputStream out;
	
	private int count = 0;

	public XmlRecordWriter(DataOutputStream out) throws IOException {
		this.out = out;
		out.writeBytes("<results>\n");
	}

	/**
	 * Write the object to the byte stream, handling Text as a special case.
	 * 
	 * @param o
	 *            the object to print
	 * @throws IOException
	 *             if the write throws, we pass it on
	 */
	private void writeObject(Object o) throws IOException {
		if (o instanceof Text) {
			Text to = (Text) o;
			out.write(to.getBytes(), 0, to.getLength());
		} else {
			out.write(o.toString().getBytes(utf8));
		}
	}

	private void writeKey(Object o, boolean closing) throws IOException {
		out.writeBytes("<");
		if (closing) {
			out.writeBytes("/");
		}
		writeObject(o);
		out.writeBytes(">");
		if (closing) {
			out.writeBytes("\n");
		}
	}

	public synchronized void write(K key, V value) throws IOException {

		boolean nullKey = key == null || key instanceof NullWritable;
		boolean nullValue = value == null || value instanceof NullWritable;

		if (nullKey && nullValue) {
			return;
		}

		Object keyObj = key;

		if (nullKey) {
			keyObj = "value";
		}

		writeKey(keyObj, false);

		if (!nullValue) {
			String s = count++ + value.toString();
			writeObject(s);
		}

		writeKey(keyObj, true);
	}

	@Override
	public synchronized void close(TaskAttemptContext context)
			throws IOException, InterruptedException {
		try {
			out.writeBytes("</results>\n");
		} finally {
			// even if writeBytes() fails, make sure we close the stream
			out.close();
		}
	}
}
