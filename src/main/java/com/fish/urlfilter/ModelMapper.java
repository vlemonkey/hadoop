package com.fish.urlfilter;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ModelMapper extends Mapper<Object, Text, Text, Text>{
	
	private static final Pattern splitPattern = Pattern.compile("\\|");
	private static final Pattern urlSplitPattern = Pattern.compile("/");
	
	Text tk = new Text();
	Text tv = new Text();
	
	@Override
	public void map(Object key, Text value, Context context)
		throws IOException, InterruptedException{
		String[]  strs = splitPattern.split(value.toString(), 28);
		
		if (strs.length == 28) {
			String url = strs[27];
			if (StringUtils.isNotBlank(url) &&
					StringUtils.startsWithIgnoreCase(url, "http")) {
				url = StringUtils.substringBefore(url, "?");
				String[] urls = urlSplitPattern.split(url, 5);
				if (urls.length > 3) {
					tk.set(StringUtils.join(urls, "/", 0, 4));
					context.write(tk, tv);
				}
			}else {
				tk.set("error");
				tv.set(url);
				context.write(tk, tv);
			}
		}else {
			context.getCounter("error","error data").increment(1);
		}
		
	}
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException{
		
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException{
		
	}
}
