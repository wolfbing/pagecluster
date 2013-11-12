package com.datamining.cluster.nesttree;

/**
 * @author Bing Liu
 * 
 * @version 1.0
 * 
 * <br>created on 2013-11-12
 * <br>last modified on 2013-11-12 by Bing Liu
 */

import java.io.File;
import java.io.IOException;

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
import org.xml.sax.SAXException;

import com.datamining.cluster.vision.VectorCreator;
import com.datamining.cluster.vision.VisionVector;
import com.datamining.sde.basictype.TagTree;
import com.datamining.sde.tagtreebuilder.DOMParserTagTreeBuilder;
import com.datamining.sde.tagtreebuilder.TagTreeBuilder;

/**
 * 基于视觉的页面相似度比较
 *
 */
public class VisionComparer implements Comparer {
	
	private double threshold; // 相似度阈值, 这个阈值貌似已经用不到了
	
	/**
	 * 默认构造函数
	 */
	public VisionComparer()
	{
		threshold = 0.6;
		loadConfig();
	}
	
	/**
	 * 载入配置文件
	 */
	private void loadConfig() 
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // never forget this!
        DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse("./config/config.xml");
	        XPathFactory xf = XPathFactory.newInstance();
	        XPath xpath = xf.newXPath();
	        XPathExpression expr1 = xpath.compile("//Config/Vision/Threshold/text()");
	        Object result1 = expr1.evaluate(doc, XPathConstants.NODE);
	        threshold = Double.parseDouble(((Node)result1).getNodeValue());
//	        System.out.println(threshold);
	
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
	 * 计算页面相似度
	 * @param p1 - 页面之一
	 * @param p2 - 页面之一
	 * @return - 页面相似度得分
	 */
	public double getSimilarityScore(Page p1, Page p2) {
		// TODO Auto-generated method stub
		File f1 = p1.getSiteFile();
		File f2 = p2.getSiteFile();
		return getSimilarityScore(f1, f2);
	}
	
	/**
	 * 载入用于比较的文件并计算相似度
	 * @param f1 - 文件之一
	 * @param f2 - 文件之一
	 * @return - 相似度得分
	 */
	public double getSimilarityScore(File f1, File f2)
	{
		VisionVector v1 = new VectorCreator().createVectorFromFile(f1);
		VisionVector v2 = new VectorCreator().createVectorFromFile(f2);
		return v1.similarity(v2);
	}
	
	
	public static void main(String [] args) throws IOException, SAXException
	{
		TagTreeBuilder builder = new DOMParserTagTreeBuilder();
		VisionComparer comparer = new VisionComparer();
//		File f1 = new File("C:/Users/Admin/Desktop/test.html");
//		File f2 = new File("C:/Users/Admin/Desktop/test2.html");
//
//		double s = comparer.getSimilarityScore(f1,f2);
//		System.out.println(s);
		comparer.loadConfig();
		
	}

}
