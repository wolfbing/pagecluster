package com.datamining.pagecluster.app;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.datamining.cluster.nesttree.PageCluster;
import com.datamining.cluster.nesttree.VisionComparer;

public class App {
	
	final int VISIONBASE = 2;
	final int CONTENTBASE = 1;
	
	private String inputDir;
	private String outputDir;
	private int method;
	private PageCluster cluster;
	
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
	
	public void setMethod(int m)
	{
		if (2==m)
		{
			this.method = m;
			cluster.setComparer(new VisionComparer());
		}
	}
	
	public App(String ind, String oud)
	{
		this(ind, oud, 1);
	}
	
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
		App app = new App("C:/Users/Admin/Desktop/douban/",
				"C:/Users/Admin/Desktop/douban-out/",1);
		app.setMethod(2);
		app.cluster();
	}
	
}
