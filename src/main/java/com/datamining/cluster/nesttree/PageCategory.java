package com.datamining.cluster.nesttree;

import java.util.ArrayList;
import java.util.List;

import com.datamining.sde.basictype.TagTree;


public class PageCategory {
	
	private List<Page> siteList;
	
	private int classId;
	
	private Comparer comparer;
	
	
	public PageCategory()
	{
		siteList = new ArrayList<Page>();
		comparer = new ContentComparer();
	}
	
	public PageCategory(Comparer c)
	{
		siteList = new ArrayList<Page>();
		comparer = c;
	}
	
	public void setComparer(Comparer c)
	{
		comparer = c;
	}
	
	public PageCategory(Page site)
	{
		siteList = new ArrayList<Page>();
		siteList.add(site);
	}
	
	public PageCategory(int id, Page site)
	{
		siteList = new ArrayList<Page>();
		siteList.add(site);
		classId = id;
	}
	
	public void setClassId(int id)
	{
		classId = id;
	}
	
	
	public void addSite(Page site)
	{
		siteList.add(site);
	}
	
	public void addSites(List<Page> sites)
	{
		siteList.addAll(sites);
	}
	
	public List<Page> getSites()
	{
		return siteList;
	}
	
	public Page getFirstSite()
	{
		return siteList.get(0);
	}
	
	public int getClassId()
	{
		return classId;
	}
	
	public double closeShip(PageCategory siteClass)
	{
		double sum = 0.0;
//		ContentComparer comparer = new ContentComparer();
		for(Page s1: siteList)
		{
			for(Page s2: siteClass.getSites())
			{
				sum += comparer.getSimilarityScore(s1, s2);
			}
		}
		return sum/(double)(this.siteList.size() * siteClass.getSites().size());
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
