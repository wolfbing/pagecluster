package com.datamining.cluster.nesttree;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SimilarityDistribution {
	
	ContentComparer comparer;
	
	public SimilarityDistribution()
	{
		comparer = new ContentComparer();
	}
	
	public List<Double> SimilarityUnderAFolder(String dir)
	{
		File dirFile = new File(dir);
		String baseUrl = dirFile.toURI().toString();
		List<Double> scoreList = new ArrayList<Double>();
		if( dirFile.isDirectory() )
		{
			File[] files = dirFile.listFiles();
			for(int loopOut=0;loopOut<files.length;++loopOut)
			{
				if(files[loopOut].isFile())
				{
					for(int loopIn=loopOut+1;loopIn<files.length;++loopIn)
					{
						if(files[loopIn].isFile())
						{
							scoreList.add(comparer.getSimilarityScore(
									baseUrl+URLEncoder.encode(files[loopOut].getName()).replace("+", "%20"),
									baseUrl+URLEncoder.encode(files[loopIn].getName()).replace("+", "%20") ) );
						}
						
					}
				}
			}
		}
		else
		{
			System.err.println("Your input is not a directory");
		}
		return scoreList;
	}
	
	
	public List<Double> SimilarityBetweenTwoFolder(String dir1, String dir2)
	{
		File dirFile1 = new File(dir1);
		File dirFile2 = new File(dir2);
		String baseUrl1 = dirFile1.toURI().toString();
		String baseUrl2 = dirFile2.toURI().toString();
		List<Double> scoreList = new ArrayList<Double>();
		if(dirFile1.isDirectory() && dirFile2.isDirectory())
		{
			File[] fileArr1 = dirFile1.listFiles();
			File[] fileArr2 = dirFile2.listFiles();
			for(File f1: fileArr1)
			{
				if(f1.isFile())
				{
					for(File f2: fileArr2)
					{
						if(f2.isFile())
						{
							double score = comparer.getSimilarityScore(
									baseUrl1+URLEncoder.encode(f1.getName()).replace("+", "%20"),
									baseUrl2+URLEncoder.encode(f2.getName()).replace("+", "%20") ) ;
							scoreList.add(score);
						}
					}
				}
			}
		}
		else
		{
			System.err.println("your input directory is not correct!");
		}
		return scoreList;
	}
	
	
	public List<Double> statisticsAFolder(String dir)
	{
		File dirFile = new File(dir);
		if(!dirFile.isDirectory())
		{
			System.err.println("your input is not a directory");
			System.exit(-1);
		}
		
		File [] dirArr = dirFile.listFiles();
		List<Double> scoreOfSameClass  = new ArrayList<Double>();
		List<Double> scoreOfDiffClass = new ArrayList<Double>();
		for(int loopOut = 0; loopOut < dirArr.length; ++ loopOut)
		{
			if(dirArr[loopOut].isDirectory())
			{
				scoreOfSameClass.addAll(SimilarityUnderAFolder(dirArr[loopOut].getAbsolutePath()));
				for(int loopIn = loopOut+1; loopIn<dirArr.length;++loopIn)
				{
					if(dirArr[loopIn].isDirectory())
					{
						scoreOfDiffClass.addAll(SimilarityBetweenTwoFolder(dirArr[loopOut].getAbsolutePath(),
								dirArr[loopIn].getAbsolutePath()));
					}
				}
			}
		}
		distribute(scoreOfSameClass, "same class score");
		distribute(scoreOfDiffClass, "diff class score");
		
		List<Double> allScore = new ArrayList<Double>();
		allScore.addAll(scoreOfDiffClass);
		allScore.addAll(scoreOfSameClass);
		distribute(allScore,"all score");
		
		return allScore;
		
		
	}
	
	public void distribute(List<Double> scores, String name)
	{
		System.out.println("#########  "+name+" -- statistics  #########");
		
		double minScore = 1.0;
		double maxScore = 0.0;
		for(double s: scores)
		{
			if(s<minScore)
			{
				minScore = s;
			}
			if(s>maxScore)
			{
				maxScore = s;
			}
			System.out.println(s);
		}
		System.out.println("max score: "+maxScore);
		System.out.println("min score: "+minScore);
		
		int totalNum = scores.size();
		int [] nums = new int[10];
		for(double score: scores)
		{
			if(score>=1.0)
			{
				nums[9]++;
			}
			else
			{
				nums[(int) (score/0.1)]++;
			}
//			score/0.1;
		}
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		
		for(int i=0;i<nums.length;++i)
		{
			System.out.println("0."+i+"-0."+(i+1)+": "+ df.format( ((double)nums[i]/(double)totalNum)*100 )+"%");
		}
		
		System.out.println("####### "+name+"  statistics END   ###########");
		
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimilarityDistribution distributionAnalysiser = new SimilarityDistribution();
		List<Double> scores1 = distributionAnalysiser.SimilarityUnderAFolder("C:/Users/Admin/Desktop/douban-out1/3/");
//		List<Double> scores2 = distributionAnalysiser.SimilarityUnderAFolder("C:/Users/Admin/Desktop/ques/20/");
//		List<Double> scores3 = distributionAnalysiser.SimilarityBetweenTwoFolder("C:/Users/Admin/Desktop/ques/19/", "C:/Users/Admin/Desktop/ques/20/");
//		for(double score:scores)
//		{
//			System.out.println(score);
//		}
		
//		List<Double> allScore = distributionAnalysiser.statisticsAFolder("C:/Users/Admin/Desktop/douban-out/");
		
		distributionAnalysiser.distribute(scores1,"19");
//		distributionAnalysiser.distribute(scores2,"20");
//		distributionAnalysiser.distribute(scores3,"all");
		
		System.out.println("=========end==========");
		
	}

}
