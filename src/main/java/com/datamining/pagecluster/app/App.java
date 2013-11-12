package com.datamining.pagecluster.app;

/**
 * 
 * @author Bing Liu
 * 
 * @version 1.0
 * 
 * <br>created on 2013-11-12
 * <br>last modified on 2013-11-12 by Bing Liu
 * 
 */

import java.io.IOException;

import org.xml.sax.SAXException;

import com.datamining.cluster.nesttree.PageCluster;
import com.datamining.cluster.nesttree.VisionComparer;


public class App {
	
	final int VISIONBASE = 2;
	final int CONTENTBASE = 1;
	
	private String inputDir; //待归类的文件所在目录
	private String outputDir; //  文件输出目录
	private int method; // 文件归类方法, 有基于视觉方法和基于内容折叠的方法
	private PageCluster cluster; // 聚类工具, 这将取决于设定的页面相似性比较方法
	
	/**
	 * 构造函数
	 * @param ind - 原始文件所在目录
	 * @param oud - 输出文件的保存目录
	 * @param m - 页面相似性的比较方法, 1:内容折叠, 2:视觉
	 */
	public App(String ind, String oud, int m)
	{
		inputDir = ind;
		outputDir = oud;
		cluster = new PageCluster();
		if (m==VISIONBASE)
		{
			method = VISIONBASE;
			cluster.setComparer(new VisionComparer());
		}
		else
		{
			method=CONTENTBASE;
		}
		
	}
	
	/**
	 * 设置判断页面相似的方法(基于折叠或视觉)
	 * @param m - 方法, 1:折叠, 2:视觉
	 */
	public void setMethod(int m)
	{
		if (2==m)
		{
			this.method = m;
			cluster.setComparer(new VisionComparer());
		}
	}
	
	/**
	 * 构造函数, 页面相似性比较默认是基于内容折叠
	 * @param ind - 原始文件所在目录
	 * @param oud - 输出文件的保存目录
	 */
	public App(String ind, String oud)
	{
		this(ind, oud, 1);
	}
	
	/**
	 * 聚类
	 */
	public void cluster()
	{
		try {
			cluster.initSiteClassesFromDir(this.inputDir);
			cluster.clusterQuick();
			cluster.save(this.outputDir);
			System.out.println("========== SUCCESS ===========");
			return ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("========== FAILED ===========");
	}
	
	public static void main(String [] args)
	{
		App app = new App("C:/Users/wolf/Desktop/douban/",
				"C:/Users/wolf/Desktop/douban-out/",1);
		app.setMethod(1);
		app.cluster();
	}
	
}
