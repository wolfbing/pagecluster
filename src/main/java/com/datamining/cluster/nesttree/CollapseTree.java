package com.datamining.cluster.nesttree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import com.datamining.sde.application.ShowTagTree;
import com.datamining.sde.basictype.DataRecord;
import com.datamining.sde.basictype.TagNode;
import com.datamining.sde.basictype.TagTree;
import com.datamining.sde.columnaligner.PartialTreeAligner;
import com.datamining.sde.net.Net;
import com.datamining.sde.tagtreebuilder.DOMParserTagTreeBuilder;
import com.datamining.sde.tagtreebuilder.TagTreeBuilder;
import com.datamining.sde.treematcher.SimpleTreeMatching;
import com.datamining.sde.treematcher.TreeMatcher;

public class CollapseTree {
	
	private double simiThreshold = 0.9;
	private SimpleTreeMatching matcher;
	
	private Map<String,String> labelmap;
	
	private List<String> forceLabelList;
	
	private boolean forceCollapse = false;
	
	public void setForceCollpase(boolean f)
	{
		forceCollapse = f ;
	}
	
	public CollapseTree()
	{
		matcher = new SimpleTreeMatching();
		labelmap = new HashMap<String,String>();
		labelmap.put("select", "optgroup");
		labelmap.put("ul", "li");
		
		forceLabelList = new ArrayList<String>();
		forceLabelList.add("li");
		forceLabelList.add("select");
	}
	
	public TagNode traverseAndCollapse(TagNode node, double threshold)
	{
		if( node.getDepth() >= 3)
		{
			for(TagNode n: node.getChildren())
			{
				traverseAndCollapse(n,threshold);
			}
			collapse(node,threshold);
			
		}
		else
		{
			if(this.forceCollapse)
			{
				forceCollapse(node);
			}
		}
		
		return node;

	}
	
	public void forceCollapse(TagNode node)
	{
		for(String label:forceLabelList)
		{
			if(labelmap.get(label)!=null)
			{
				List<TagNode> children = node.getChildren();
				List<TagNode> deleteNodes = new ArrayList<TagNode>();
				for(TagNode n: children)
				{
					if(n.toString().toLowerCase().equals(labelmap.get(label)))
					{
						deleteNodes.add(n);
//						children.remove(n);
					}

				}
				if(deleteNodes.size()>1)
				{
					for(int i=1;i<deleteNodes.size();++i)
					{
						children.remove(deleteNodes.get(i));
					}
				}
			}
		}
		

	}
	
	public TagNode collapse(TagNode node, double threshold)
	{
		List<TagNode> children = node.getChildren();
		List<TagNode> firstChildren = new ArrayList<TagNode>();
		List<TagNode> deleteChildren = new ArrayList<TagNode>();
		List<TagNode> patternNode = new ArrayList<TagNode>();
		while(children.size() != 0)
		{
			TagNode firstChild = children.remove(0);
			firstChildren.add(firstChild);
			deleteChildren.clear();
			for(TagNode childR: children)
			{
				double score = matcher.normalizedMatchScore(firstChild, childR);
//				System.out.println(score);
				if( score > threshold )
				{
//					children.remove(childR);
					deleteChildren.add(childR);
				}		
			}
			if(deleteChildren.size()==0)
			{
				patternNode.add(firstChild);
			}
			else
			{
				deleteChildren.add(firstChild);
				patternNode.add( getPatternNode(deleteChildren) );
				for(TagNode dn: deleteChildren)
				{
					if( !dn.equals(firstChild) )
					{
						children.remove(dn);
					}
				}
			}
			
					
			
		}

//		for(TagNode n: firstChildren)
		for(TagNode n: patternNode)
		{
			node.addChild(n);
		}
		
		return node;
		
		
	}
	
	public TagNode getPatternNode(List<TagNode> nodeList)
	{
		TreeMatcher matcher = new SimpleTreeMatching();
		PartialTreeAligner aligner = new PartialTreeAligner(matcher);
		
		DataRecord seedDataRecord = new DataRecord(new TagNode[]{});
		List<DataRecord> dataRecords = new ArrayList<DataRecord>();
		Map<DataRecord, Map<TagNode, TagNode>> mapping = new HashMap<DataRecord,Map<TagNode,TagNode>>();
		
		DataRecord seed = aligner.getFinalSeedNode(nodeList, seedDataRecord, dataRecords, mapping);
		TagNode root  = seed.getRecordRoot();
		
		
		return root;
	}
	
	
	

	/**
	 * @param args
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, SAXException {
		// TODO Auto-generated method stub
		File f = new File("C:/Users/Admin/Desktop/4.html");
		TagTreeBuilder builder = new DOMParserTagTreeBuilder();
		boolean ignoreFormattingTags = false;
		TagTree tagTree = builder.buildTagTree(f.toURI().toString(), ignoreFormattingTags);
		CollapseTree collapse = new CollapseTree();
		collapse.setForceCollpase(true);
//		TagNode node = collapse.traverseAndCollapse(tagTree.getRoot(), 0.8);
		ShowTagTree show = new ShowTagTree();
//		OutputStream out = new FileOutputStream("C:/Users/Admin/Desktop/2.html");
		Formatter output = new Formatter(System.out);
		show.printTreeByElement(output, tagTree.getRoot(), " ");

	}

}
