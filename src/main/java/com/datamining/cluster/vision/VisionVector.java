package com.datamining.cluster.vision;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class VisionVector {
	
	private Map<String, Integer> map;
	
	public VisionVector()
	{
		map = new HashMap<String, Integer>();
	}
	
	public void addClass(String c)
	{
		int num = map.containsKey(c)?map.get(c):0;
		map.put(c, num+1);
	}
	
	public Map<String, Integer> getMap()
	{
		return map;
	}
	
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
	
	public int multiply(VisionVector vector1, VisionVector vector2)
	{
		Set keys = vector1.getMap().keySet();
		Iterator ite = keys.iterator();
		int sum = 0;
		while(ite.hasNext())
		{
			String tmp = (String) ite.next();
			sum += (vector1.getMap().get(tmp)* vector2.getVar(tmp));
		}
		return sum;
	}
	
	public double similarity(VisionVector vector1,VisionVector vector2)
	{
		double s1 = multiply(vector1, vector2);
		double s2 = multiply(vector1, vector1);
		double s3 = multiply(vector2, vector2);
		return s1/(Math.sqrt(s2)*Math.sqrt(s3));
	}
	
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
	
	
	public static void main(String args[])
	{
		int n = true?10:9;
		System.out.print(n);
	}

}
