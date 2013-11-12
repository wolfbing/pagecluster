package com.datamining.cluster.nesttree;

/**
 * @author Bing Liu
 * 
 * @version 1.0
 * 
 * <br>created on 2013-11-12
 * <br>lasted modified on 2013-11-12 by Bing Liu
 * 
 */

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 工具类
 *
 */
public class Tool {
	
	/**
	 * 读取一个目录下的所有文件
	 * @param dir - 目录
	 * @return - 文件列表
	 */
	public static List<File> getAllFilesUnderAFolder(String dir)
	{
		List<File> fl = new ArrayList<File>();
		File file = new File(dir);
		if(file.isFile())
		{
			fl.add(file);
		}
		if(file.isDirectory())
		{
			File [] fileArr = file.listFiles();
			for(File f: fileArr)
			{
				fl.addAll(getAllFilesUnderAFolder(f.getAbsolutePath()) );
			}
		}
		
		return fl;
	}
	
	/**
	 * 将本地路径转化为url
	 * @param dir - 文件路径
	 * @param filename - 文件名
	 * @return - 转化成的url
	 */
	public static String createUrl(String dir,String filename)
	{
		File f = new File(dir);
		return f.toURI().toString()+URLEncoder.encode(filename).replace("+", "%20");
	}
	
	/**
	 * 将文件另存为
	 * @param f - 文件
	 * @param outPath - 另存路径
	 */
	public static void saveAs(File f,String outPath)
	{
		f.renameTo(new File(outPath+f.getName()));
		
	}
	

}
