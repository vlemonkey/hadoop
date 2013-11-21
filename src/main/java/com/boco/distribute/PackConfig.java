package com.boco.distribute;

import java.io.IOException;

public class PackConfig {
	
	private static final String COMMAND = "hdfs dfs -rm -r #1/distributedcache/ " +
			"&& hdfs dfs -mkdir #1/distributedcache/ && hdfs dfs -put config #1/distributedcache/";
	
	/**
	 * 执行打包上传
	 * 1.删除hdfs上的
	 * 2.创建文加件
	 * 3.上传config
	 * @param args
	 * @throws Exception
	 */
	// java -cp /home/boco/bao/hadoop.jar com.boco.distribute.PackConfig hadoop
	public static void main(String[] args) throws Exception {
		String str = COMMAND.replaceAll("#1", args[0]);
		String[] cmds = str.split("&&");
		CommandResult result = null;
		
		try {
      	int timeout = 10 * 60 * 1000;
          CommandHelper.DEFAULT_TIMEOUT = timeout;
          for (String cmd : cmds) {
        	  System.out.println("execute:" + cmd);
        	  result = CommandHelper.exec(cmd);
              if (result != null) {
            	  if (null != result.getOutput()) {
            		  System.out.println("Output:" + result.getOutput());
            	  }
            	  if (null != result.getError()) {
            		  System.out.println("Error:" + result.getError());
				}
              }
              System.out.println("-----------------------------------");
		}
          
        System.out.println("执行成功!");
      } catch (IOException ex) {
          System.out.println("IOException:" + ex.getLocalizedMessage());
      } catch (InterruptedException ex) {
          System.out.println("InterruptedException:" + ex.getLocalizedMessage());
      }
	}
}
