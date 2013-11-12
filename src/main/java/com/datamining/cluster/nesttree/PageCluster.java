package com.datamining.cluster.nesttree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.xml.sax.SAXException;

import com.datamining.sde.basictype.TagTree;
import com.datamining.sde.tagtreebuilder.DOMParserTagTreeBuilder;
import com.datamining.sde.tagtreebuilder.TagTreeBuilder;

public class PageCluster {
	
	private double similarityThreshold;
	
	private List<PageCategory> siteClasses; // 页面类别
	private Map<Integer,PageCategory> id2class; // id对应类别
	
	private CategoryComparer comparer; //页面比较工具
	
	private double nearestScore; // 相似度最高的类别对的相似度得分
	
	private boolean goon; // 是否继续进行聚类
	
	private Map<SiteClassPair, Double>  closeShipTable; // 页面相似度得分表, 记录了任意两个页面的相似度
	
	
	/**
	 * 页面对
	 *
	 */
	private class SiteClassPair
	{
		int c1Id;
		int c2Id;
		
		public SiteClassPair(int s1,int s2)
		{
			c1Id=s1;
			c2Id=s2;
		}
		
		public int getClass1Id()
		{
			return c1Id;
		}
		
		public int getClass2Id()
		{
			return c2Id;
		}

	}
	
	/**
	 * 初始化页面距离表, 初始化时每个页面单独作为一个类别, 在后期进行聚类
	 */
	public void initCloseShipTable()
	{
		for(int loopOut = 0;loopOut<siteClasses.size();++loopOut)
		{
			for(int loopIn=loopOut+1;loopIn<siteClasses.size();++loopIn)
			{
				Map<SiteClassPair, Double> map = new HashMap<SiteClassPair, Double>();
				int c1 = siteClasses.get(loopOut).getClassId();
				int c2 = siteClasses.get(loopIn).getClassId();
				SiteClassPair pair1=  new SiteClassPair(c1,c2);
				SiteClassPair pair2=  new SiteClassPair(c2,c1);
//				double score = id2class.get(c1).closeShip(id2class.get(c2));
				double score = comparer.getSimilarityScore(
						id2class.get(c1),id2class.get(c2));
				closeShipTable.put(pair1, score);
				closeShipTable.put(pair2, score);
			}
		}
	}
	
	/**
	 * 默认构造函数
	 */
	public PageCluster()
	{
		siteClasses = new ArrayList<PageCategory>();
		id2class = new HashMap<Integer,PageCategory>();
		closeShipTable = new HashMap<SiteClassPair, Double>();
		similarityThreshold = 0.7;
		nearestScore = 0.0;
		goon = true;
		comparer = new CategoryComparer(new ContentComparer());
	}
	
	/**
	 * 设置比较工具
	 * @param c
	 */
	public void setComparer(Comparer c)
	{
		comparer.setComparer(c);
	}
	
	/**
	 * 初始化页面类别, 每个页面将单独作为一个类别
	 * @param dir - 文件所在目录
	 * @throws IOException - 读取失败
	 * @throws SAXException - 解析失败
	 */
	public void initSiteClassesFromDir(String dir) throws IOException, SAXException
	{
		TagTreeBuilder builder = new DOMParserTagTreeBuilder();
		boolean ignoreFormattingTags = false;
		int counter = 0;
		List<File> flist = Tool.getAllFilesUnderAFolder(dir);
		for(File f: flist)
		{
			TagTree tagTree = builder.buildTagTree(
					Tool.createUrl(f.getAbsolutePath().substring(0,f.getAbsolutePath().length()-f.getName().length() ),f.getName()), 
					ignoreFormattingTags);
			PageCategory s = new PageCategory();
			s.addSite(new Page(tagTree,f));
			s.setClassId(counter);
			id2class.put(counter++, s);
			siteClasses.add(s);
		}
		
		initCloseShipTable();
		
	}

	/**
	 * 获取页面类别对
	 * @param cla1Id - 页面之一id
	 * @param cla2Id - 页面之一id
	 * @param set - 页面类别对集合
	 * @return - 页面类别对
	 */
	public SiteClassPair getSiteClassPairFromSet(int cla1Id,int cla2Id,Set<SiteClassPair> set)
	{
		Iterator ite = set.iterator();
		while(ite.hasNext())
		{
			SiteClassPair p = (SiteClassPair) ite.next();
			if(p.getClass2Id()==cla2Id && p.getClass1Id()==cla1Id)
			{
				return p;
			}
		}
		return null;
	}
	
	/**
	 * 类别合并
	 * @param cla1Id - 类别之一id
	 * @param cla2Id - 类别之一id
	 */
	public void mergeClass(int cla1Id, int cla2Id)
	{
		for(int i=0;i<siteClasses.size();++i)
		{
			if(siteClasses.get(i).getClassId()!= cla1Id && siteClasses.get(i).getClassId()!=cla2Id )
			{
				int currentClass = siteClasses.get(i).getClassId();
				SiteClassPair pair1 = getSiteClassPairFromSet(cla1Id,currentClass,closeShipTable.keySet());
				SiteClassPair pair3 = getSiteClassPairFromSet(currentClass,cla1Id,closeShipTable.keySet());
				SiteClassPair pair2 = getSiteClassPairFromSet(cla2Id,currentClass,closeShipTable.keySet());
				SiteClassPair pair4 = getSiteClassPairFromSet(currentClass, cla2Id,closeShipTable.keySet());
				double closeShip1 = closeShipTable.get(pair1);
				double closeShip2 = closeShipTable.get(pair2);
				int num1 = id2class.get(currentClass).getSites().size()*id2class.get(cla1Id).getSites().size() ;
				int num2 = id2class.get(currentClass).getSites().size()*id2class.get(cla2Id).getSites().size();
				double newScore = ( closeShip1*num1 + closeShip2*num2 )/(num1+num2);
				closeShipTable.put(pair1, newScore);
				closeShipTable.put(pair3, newScore);
				closeShipTable.remove(pair2);
				closeShipTable.remove(pair4);
				
			}
		}
		SiteClassPair p1 = getSiteClassPairFromSet(cla1Id,cla2Id,closeShipTable.keySet());
		SiteClassPair p2 = getSiteClassPairFromSet(cla2Id,cla1Id,closeShipTable.keySet());
		closeShipTable.remove(p2);
		closeShipTable.remove(p1);
		
		id2class.get(cla1Id).addSites(id2class.get(cla2Id).getSites());
		siteClasses.remove(id2class.get(cla2Id));
	}
	
	
	public void mergeClass(PageCategory cla1, PageCategory cla2)
	{
		cla1.addSites(cla2.getSites());
		siteClasses.remove(cla2);
	}
	
	/**
	 * 搜索最相似的类别对
	 * @return - 类别对
	 */
	public SiteClassPair searchClosestClasses()
	{
		double maxScore = 0.0;
		SiteClassPair pair = null;
		for(SiteClassPair p: closeShipTable.keySet())
		{
			if( closeShipTable.get(p)>maxScore )
			{
				pair = p;
				maxScore = closeShipTable.get(p);
			}
		}
		this.nearestScore = maxScore;
		// 如果页面的相似度低于阈值, 则停止聚类
		if(maxScore < similarityThreshold)
		{
			goon = false;
		}
		System.out.println(maxScore);
		return pair;

	}
	
	/**
	 * 一次聚类
	 */
	public void clusterStepQuick()
	{
		SiteClassPair pair = searchClosestClasses();
		if(pair!=null)
		{
			if(goon)
			{
				mergeClass( pair.getClass1Id(), pair.getClass2Id() );
			}
		}
	}
	
	
//	public void clusterStep()
//	{
//		PageCategory c1 = null;
//		PageCategory c2 = null;
//		double closeScore = 0.0;
//		for(int loopOut = 0;loopOut<siteClasses.size();++loopOut)
//		{
//			for(int loopIn = loopOut+1;loopIn<siteClasses.size();++loopIn)
//			{
//				double score = siteClasses.get(loopOut).closeShip(siteClasses.get(loopIn));
//				if(score>closeScore)
//				{
//					closeScore = score;
//					c1 = siteClasses.get(loopOut);
//					c2 = siteClasses.get(loopIn);
//				}
//			}
//		}
//		if(c1!=null && c2!=null && closeScore>= similarityThreshold)
//		{
//			mergeClass(c1,c2);
//			nearestScore = closeScore;
//		}
//		
//		if(closeScore < similarityThreshold)
//		{
//			goon = false;
//		}
//		
//		System.out.println(nearestScore);
//	}
	
	/**
	 * 聚类, 直到最大相似度低于阈值停止
	 */
	public void clusterQuick()
	{
		while(goon)
		{
			if(siteClasses.size()<2)
			{
				goon = false;
				break;
			}
			clusterStepQuick();
		}
	}
	
//	public void cluster()
//	{
//		do
//		{
//			if(siteClasses.size()<2)
//			{
//				goon = false;
//				break;
//			}
//			clusterStep();
//			
//		}while(goon );
//	}
	
	/**
	 * 保存分类的结果
	 * @param outDir - 输出目录
	 */
	public void save(String outDir)
	{
		File saveDirFile = new File(outDir);
		if( !saveDirFile.isDirectory() )
		{
			if(!saveDirFile.mkdirs())
			{
				System.err.println("make directory '"+outDir+"' failed");
				System.exit(-1);
			}
		}
		for(int i=0;i<siteClasses.size();++i)
		{
			String newClassDir = saveDirFile.getAbsolutePath()+"\\"+i+"\\";
			File newDirFile = new File(newClassDir);
			if(!newDirFile.isDirectory())
			{
				if(!newDirFile.mkdirs())
				{
					System.out.println("failed to make new class output dir");
					System.exit(-1);
				}
			}
			List<Page> sites = siteClasses.get(i).getSites();
			for(int j=0;j<sites.size();++j)
			{
				Tool.saveAs(sites.get(j).getSiteFile(), newClassDir);
			}
			
		}
		
	}
	
	

	/**
	 * @param args
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, SAXException {
		// TODO Auto-generated method stub
		
		PageCluster cluster = new PageCluster();
		cluster.setComparer(new VisionComparer());
		cluster.initSiteClassesFromDir("C:/Users/Admin/Desktop/douban/");
//		cluster.cluster();
		cluster.clusterQuick();
		cluster.save("C:/Users/Admin/Desktop/douban-out/");
		
		System.out.println("========== END ================");

	}

}
