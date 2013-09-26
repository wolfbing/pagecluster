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
	
	private List<PageCategory> siteClasses;
	private Map<Integer,PageCategory> id2class;
	
	private CategoryComparer comparer;
	
	private double nearestScore;
	
	private boolean goon;
	
	private Map<SiteClassPair, Double>  closeShipTable;
	
	
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
	
	public void setComparer(Comparer c)
	{
		comparer.setComparer(c);
	}
	
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

	public void addClass(PageCategory c)
	{
		siteClasses.add(c);
	}
	
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
		
		if(maxScore < similarityThreshold)
		{
			goon = false;
		}
		System.out.println(maxScore);
		return pair;

	}
	
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
	
	
	public void clusterStep()
	{
		PageCategory c1 = null;
		PageCategory c2 = null;
		double closeScore = 0.0;
		for(int loopOut = 0;loopOut<siteClasses.size();++loopOut)
		{
			for(int loopIn = loopOut+1;loopIn<siteClasses.size();++loopIn)
			{
				double score = siteClasses.get(loopOut).closeShip(siteClasses.get(loopIn));
				if(score>closeScore)
				{
					closeScore = score;
					c1 = siteClasses.get(loopOut);
					c2 = siteClasses.get(loopIn);
				}
			}
		}
		if(c1!=null && c2!=null && closeScore>= similarityThreshold)
		{
			mergeClass(c1,c2);
			nearestScore = closeScore;
			
		}
		if(closeScore < similarityThreshold)
		{
			goon = false;
		}
		System.out.println(nearestScore);
	}
	
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
	
	public void cluster()
	{
		do
		{
			if(siteClasses.size()<2)
			{
				goon = false;
				break;
			}
			clusterStep();
			
		}while(goon );
	}
	
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
