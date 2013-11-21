package com.fish.global.reduce;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.mapreduce.Reducer;

public class IdentityReducer<K, V> extends Reducer<K, V, K, V> {

	/** Writes all keys and values directly to output. */
	public void reduce(K key, Iterator<V> values, Context context)
			throws IOException, InterruptedException {
		while (values.hasNext()) {
			context.write(key, values.next());
		}
	}
}