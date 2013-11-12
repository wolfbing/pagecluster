package com.datamining.cluster.vision;

/**
 * 
 * @author Bing Liu
 * 
 * @version 1.0.1112
 * 
 * <br>created on 2013-11-12
 * <br>last modified on 2013-11-12 by Bing Liu
 * 
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 
 * <br>视觉向量类
 * <br>视觉向量是指, html页面中的每一种class属性节点作为视觉向量的一个维度, 分量的值是class出现的次数;
 *
 */
public class VisionVector {
	/* 
	 * 以字典的方式来存储向量, key表示分量类型, value表示分量值; 
	 * 这样可以降低存储维度
	 */
	private Map<String, Integer> map;
	
	/**
	 * 默认构造函数
	 */
	public VisionVector()
	{
		map = new HashMap<String, Integer>();
	}
	
	/**
	 * 对遇到一个新的class属性节点进行存储
	 * @param c - class属性节点的属性值
	 */
	public void addClass(String c)
	{
		int num = map.containsKey(c)?map.get(c):0;
		map.put(c, num+1);
	}
	
	/**
	 * 获取视觉向量
	 * @return 视觉向量
	 */
	public Map<String, Integer> getVector()
	{
		return map;
	}
	
	/**
	 * 获取分量值
	 * @param key - 某个class属性节点的属性值
	 * @return - 对应的class分量值
	 */
	public int getVar(String key)
	{
		Integer num = map.get(key);
		if (null==num)
		{
			return 0;
		}
		else
		{
			return num;
		}
	}
	
	/**
	 * 计算与另一个视觉向量的内积
	 * @param vector - 与之相乘的视觉向量
	 * @return - 内积
	 */
	public int multiply(VisionVector vector)
	{
		Set keys = map.keySet();
		Iterator ite = keys.iterator();
		int sum = 0;
		while(ite.hasNext())
		{
			String tmp = (String) ite.next();
			sum += (map.get(tmp)* vector.getVar(tmp));
		}
		return sum;
	}
	
	/**
	 * 计算两个视觉向量的内积
	 * @param vector1 - 向量之一
	 * @param vector2 - 向量之一
	 * @return - 内积
	 */
	public int multiply(VisionVector vector1, VisionVector vector2)
	{
		Set keys = vector1.getVector().keySet();
		Iterator ite = keys.iterator();
		int sum = 0;
		while(ite.hasNext())
		{
			String tmp = (String) ite.next();
			sum += (vector1.getVector().get(tmp)* vector2.getVar(tmp));
		}
		return sum;
	}
	
	/**
	 * 计算两个向量的相似度
	 * @param vector1 - 向量之一
	 * @param vector2 - 向量之一
	 * @return - 相似度(0-1之间的double型)
	 */
	public double similarity(VisionVector vector1,VisionVector vector2)
	{
		double s1 = multiply(vector1, vector2);
		double s2 = multiply(vector1, vector1);
		double s3 = multiply(vector2, vector2);
		return s1/(Math.sqrt(s2)*Math.sqrt(s3));
	}
	
	/**
	 * 计算其他向量与之的相似度
	 * @param v - 待比较向量
	 * @return - 相似度(0-1之间的double型)
	 */
	public double similarity(VisionVector v)
	{
		return this.similarity(this, v);
	}
	
	@Override
	public String toString()
	{
		String str = ""+map.size()+"\n";
		
		Set<String> keys = map.keySet();
//		System.out.print(keys);
		Iterator ite = keys.iterator();
		while(ite.hasNext())
		{
			String tmp = (String) ite.next();
			str += (tmp+":"+map.get(tmp)+"\n");
		}
		return str;
		
	}
	
	

}
