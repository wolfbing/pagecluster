package com.datamining.cluster.nesttree;

/**
 * 页面相似性比较接口
 * 
 * @author Bing Liu
 *
 */
public interface Comparer {
	
	public double getSimilarityScore(Page p1, Page p2);

}
