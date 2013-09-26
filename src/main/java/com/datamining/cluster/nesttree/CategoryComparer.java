package com.datamining.cluster.nesttree;

public class CategoryComparer{
	
	private Comparer comparer;
	
	public CategoryComparer()
	{
		comparer = new ContentComparer();
	}
	
	public CategoryComparer(Comparer c)
	{
		comparer = c;
	}
	
	public void setComparer(Comparer c)
	{
		comparer = c;
	}

	public double getSimilarityScore(PageCategory p1, PageCategory p2) {
		// TODO Auto-generated method stub
		p1.setComparer(comparer);
		return p1.closeShip(p2);
	}

}
