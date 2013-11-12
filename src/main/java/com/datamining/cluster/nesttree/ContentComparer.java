package com.datamining.cluster.nesttree;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.datamining.sde.basictype.TagNode;
import com.datamining.sde.basictype.TagTree;
import com.datamining.sde.tagtreebuilder.DOMParserTagTreeBuilder;
import com.datamining.sde.tagtreebuilder.TagTreeBuilder;
import com.datamining.sde.treematcher.SimpleTreeMatching;
import com.datamining.sde.treematcher.TreeMatcher;

public class ContentComparer implements Comparer {
	
	private double nestedThreshold = 0.8; // 折叠时的阈值
	private double siteSimilarityThreshold = 0.7; // 页面相似度阈值
	
//	String input="file:///C:/Users/Admin/Desktop/1.html";
	private boolean ignoreFormattingTags = false;
	private TagTreeBuilder builder ;
	private TreeMatcher matcher;
	
	/**
	 * 默认构造函数
	 */
	public ContentComparer()
	{
		builder = new DOMParserTagTreeBuilder();
		ignoreFormattingTags = false;
		
		nestedThreshold = 0.8;
		siteSimilarityThreshold = 0.7;
		loadConfig();
		
		matcher = new SimpleTreeMatching();
	}
	
	/**
	 * 载入配置文件
	 */
	public void loadConfig() 
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // never forget this!
        DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse("./config/config.xml");
	        XPathFactory xf = XPathFactory.newInstance();
	        XPath xpath = xf.newXPath();
	        XPathExpression expr1 = xpath.compile("//Config/Content/NestThreshold/text()");
	        XPathExpression expr2 = xpath.compile("//Config/Content/PageSimilarityThreshold/text()");
	        Object result1 = expr1.evaluate(doc, XPathConstants.NODE);
	        Object result2 = expr2.evaluate(doc, XPathConstants.NODE);
	        nestedThreshold = Double.parseDouble(((Node)result1).getNodeValue());
	        siteSimilarityThreshold = Double.parseDouble(((Node)result2).getNodeValue());
	
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	/**
	 * 判断两个url对应的页面是否相似
	 * @param url1 - 其中一个url
	 * @param url2 - 其中一个url
	 * @return - true, 如果相似度大于阈值
	 * 		<br> - false, 如果相似度小于阈值
	 */
	public boolean comparePage(String url1,String url2)
	{
		try {
			TagTree tagTree1 = builder.buildTagTree(url1, ignoreFormattingTags);
			
			TagTree tagTree2 = builder.buildTagTree(url2, ignoreFormattingTags);
			
			return comparePage(tagTree1,tagTree2);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
			
	}
	
	/**
	 * 判断tagTree是否相似
	 * @param tagTree1 - 其中一个tagTree
	 * @param tagTree2 - 其中一个tagTree
	 * @return - true, 如果相似度得分大于阈值
	 * 		<br> - false, 如果相似度得分小于阈值
	 */
	private boolean comparePage(TagTree tagTree1, TagTree tagTree2)
	{
		TagNode root1 = tagTree1.getRoot();
		TagNode root2 = tagTree2.getRoot();
		
		CollapseTree collapser = new CollapseTree();
		root1 = collapser.traverseAndCollapse( root1 , nestedThreshold);
		root2 = collapser.traverseAndCollapse( root2 , nestedThreshold);
		
		double score = matcher.normalizedMatchScore(root1, root2);
		System.out.println(score);
		if( score >= siteSimilarityThreshold )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 计算两个url对应的页面的相似度
	 * @param url1 - 其中一个url
	 * @param url2 - 其中一个url
	 * @return - 相似度得分
	 */
	public double getSimilarityScore(String url1,String url2)
	{

		try {
			TagTree tagTree1 = builder.buildTagTree(url1, ignoreFormattingTags);
			TagTree tagTree2 = builder.buildTagTree(url2, ignoreFormattingTags);
			
			return getSimilarityScore(tagTree1, tagTree2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0.0;
	}
	
	
	/**
	 * 比较两个tagTree的相似度
	 * @param tagTree1 - tagTree之一
	 * @param tagTree2 - tagTree之一
	 * @return - 相似度得分
	 */
	private double getSimilarityScore(TagTree tagTree1, TagTree tagTree2)
	{
		TagNode root1 = tagTree1.getRoot();
		TagNode root2 = tagTree2.getRoot();
		
		CollapseTree collapser = new CollapseTree();
		root1 = collapser.traverseAndCollapse( root1 , nestedThreshold);
		root2 = collapser.traverseAndCollapse( root2 , nestedThreshold);
		
		double score = matcher.normalizedMatchScore(root1, root2);
//		System.out.println(score);
		return score;
	}
	

	/**
	 * 计算页面相似度得分
	 * @param p1 - 页面之一
	 * @param p2 - 页面之一
	 * @return - 相似度得分
	 */
	public double getSimilarityScore(Page p1, Page p2) {
		// TODO Auto-generated method stub
		return this.getSimilarityScore(p1.getSiteTagTree(), p2.getSiteTagTree());
		
	}

}
