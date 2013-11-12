package com.datamining.cluster.vision;

/**
 * 
 * @author  Bing Liu
 * 
 * @version  1.0
 * 
 * <br>created on  2013-11-12
 * <br>last modified on 2013-11-12 by Bing Liu
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * <br>向量生成器类
 * <br>用于从html中解析视觉向量
 *
 */
public class VectorCreator {
	
	/**
	 * 从路径中读取文件并生成dom树
	 * @param path - 文件路径
	 * @return - dom树, 如果载入并生成成功
	 * 		<br> - null, otherwise
	 */
	private Document loadDomFromPath(String path)
	{
		File f = new File(path);
		return loadDomFromFile(f);
		
	}
	
	/**
	 * 载入html文件生成dom树
	 * @param f - File类型, 用于解析的文件
	 * @return - dom树, Document类型, 如果载入成功
	 * 		<br>- null, otherwise
	 */
	private Document loadDomFromFile(File f)
	{
		DOMParser parser = new DOMParser();
		BufferedReader in;
		try {
			in = new BufferedReader(
					new FileReader(f));
			parser.parse(new InputSource(in));
			return parser.getDocument();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 从dom树中获取所有节点
	 * @param doc - dom树
	 * @return - 所有节点列表
	 */
	private List<Node> getNodesFromDoc(Document doc)
	{
		return getDescendant(doc.getChildNodes());	
	}
	
	/**
	 * 获取一组节点的所有后代
	 * @param nl - 节点列表
	 * @return - 所有的后代节点列表
	 */
	private List<Node> getDescendant(NodeList nl)
	{
		List<Node> nodeList = new ArrayList<Node>();
		for (int i=0; i<nl.getLength();++i)
		{
			nodeList.addAll(getDescendant(nl.item(i)));
		}
		return nodeList;
	}
	
	/**
	 * 获取一个节点的所有后代
	 * @param root - 节点
	 * @return - 后代节点列表
	 */
	private List<Node> getDescendant(Node root)
	{
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(root);
		if (root.hasChildNodes())
		{
			NodeList nl = root.getChildNodes();
			nodes.addAll(getDescendant(nl));
//			for (int i=0;i<nl.getLength();++i)
//			{
//				nodes.addAll(getDescendant(nl.item(i)));
//			}
		}
		
		return nodes;
	}
	
	/**
	 * 解析节点列表生成视觉向量
	 * @param nl - 节点列表
	 * @return - 视觉向量
	 */
	private VisionVector createVectorFromNodeList(List<Node> nl)
	{
		VisionVector vector = new VisionVector();
		for (Node node: nl)
		{
			NamedNodeMap attrMap = node.getAttributes();
			if(attrMap!=null)
			{
				if(null!=attrMap.getNamedItem("class"))
				{
					vector.addClass(attrMap.getNamedItem("class").getNodeValue());
				}
				
			}
		}
		return vector;
	}

	
	/**
	 * 从路径中读取文件并解析成视觉向量
	 * @param path - 路径
	 * @return - 视觉向量
	 */
	public VisionVector createVectorFromPath(String path)
	{
		Document doc = this.loadDomFromPath(path);
		List<Node> nl = this.getNodesFromDoc(doc);
		
		return createVectorFromNodeList(nl);
	}
	
	/**
	 * 从文件中解析视觉向量
	 * @param f - 文件
	 * @return - 视觉向量
	 */
	public VisionVector createVectorFromFile(File f)
	{
		Document doc = this.loadDomFromFile(f);
		List<Node> nl = this.getNodesFromDoc(doc);
		
		return createVectorFromNodeList(nl);
	}
	
	
	public static void main(String args[])
	{
		VectorCreator creator = new VectorCreator();
		VisionVector vector = creator.createVectorFromPath("C:/Users/Admin/Desktop/test.html");
		System.out.print(vector);
	
	}
	

}
