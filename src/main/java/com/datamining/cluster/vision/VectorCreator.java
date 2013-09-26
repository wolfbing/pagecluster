package com.datamining.cluster.vision;

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

public class VectorCreator {
	
	
	public Document loadDomFromPath(String path)
	{
		File f = new File(path);
		return loadDomFromFile(f);
		
	}
	
	public Document loadDomFromFile(File f)
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
	
	public List<Node> getNodesFromDoc(Document doc)
	{
		return getDescendant(doc.getChildNodes());	
	}
	
	public List<Node> getDescendant(NodeList nl)
	{
		List<Node> nodeList = new ArrayList<Node>();
		for (int i=0; i<nl.getLength();++i)
		{
			nodeList.addAll(getDescendant(nl.item(i)));
		}
		return nodeList;
	}
	
	public List<Node> getDescendant(Node root)
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
	
	public VisionVector createVectorFromNodeList(List<Node> nl)
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
	
	public String extractClass()
	{
		return null;
	}
	
	
	
	
	public VisionVector createVectorFromPath(String path)
	{
		Document doc = this.loadDomFromPath(path);
		List<Node> nl = this.getNodesFromDoc(doc);
		
		return createVectorFromNodeList(nl);
	}
	
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
