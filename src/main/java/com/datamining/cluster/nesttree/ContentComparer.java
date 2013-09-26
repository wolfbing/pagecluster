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
	
	private double nestedThreshold = 0.8;
	private double siteSimilarityThreshold = 0.7;
	
//	String input="file:///C:/Users/Admin/Desktop/1.html";
	private boolean ignoreFormattingTags = false;
	private TagTreeBuilder builder ;
	private TreeMatcher matcher;
	
	public ContentComparer()
	{
		builder = new DOMParserTagTreeBuilder();
		ignoreFormattingTags = false;
		
		nestedThreshold = 0.8;
		siteSimilarityThreshold = 0.7;
		loadConfig();
		
		matcher = new SimpleTreeMatching();
	}
	
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
	
	
	public boolean comparePage(String url1,String url2)
	{
		TagTree tagTree1;
		try {
			tagTree1 = builder.buildTagTree(url1, ignoreFormattingTags);
			
			TagTree tagTree2 = builder.buildTagTree(url2, ignoreFormattingTags);
			
			comparePage(tagTree1,tagTree2);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
			
	}
	
	
	public boolean comparePage(TagTree tagTree1, TagTree tagTree2)
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
	
	
	public double getSimilarityScore(String url1,String url2)
	{
		TagTree tagTree1;
		try {
			tagTree1 = builder.buildTagTree(url1, ignoreFormattingTags);
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
	
	
	
	public double getSimilarityScore(TagTree tagTree1, TagTree tagTree2)
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
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {
		// TODO Auto-generated method stub

		ContentComparer comparer = new ContentComparer();
		comparer.loadConfig();
		
	}


	public double getSimilarityScore(Page p1, Page p2) {
		// TODO Auto-generated method stub
		return this.getSimilarityScore(p1.getSiteTagTree(), p2.getSiteTagTree());
		
	}

}
