package com.datamining.cluster.vision;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.datamining.sde.basictype.TagNode;
import com.datamining.sde.basictype.TagTree;
import com.datamining.sde.tagtreebuilder.DOMParserTagTreeBuilder;
import com.datamining.sde.tagtreebuilder.TagTreeBuilder;

public class Test {
	
	
	public static void main(String[] args) throws IOException, SAXException
	{
		DOMParser parser = new DOMParser();
		BufferedReader in = new BufferedReader(
				new FileReader("C:/Users/Admin/Desktop/test.html"));
		parser.parse(new InputSource(in));
		Document doc = parser.getDocument();
		
		NodeList body = doc.getElementsByTagName("body");
		NodeList divs = body.item(0).getChildNodes();

		for (int i=0; i<divs.getLength(); ++i)
		{
			NamedNodeMap map = divs.item(i).getAttributes();
			if(null!=map)
			{
				Node item = map.getNamedItem("class");
				System.out.print(item.getNodeValue()+"\n");
			}
			
		
		}
	
	}

}
