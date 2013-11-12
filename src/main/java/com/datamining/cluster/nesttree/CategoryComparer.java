package com.datamining.cluster.nesttree;

/**
 * 类别比较类
 *
 */
public class CategoryComparer{
	
	private Comparer comparer; // 页面相似度比较工具
	
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
