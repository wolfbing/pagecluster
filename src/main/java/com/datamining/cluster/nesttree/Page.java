package com.datamining.cluster.nesttree;

import java.io.File;

import com.datamining.sde.basictype.TagTree;

public class Page {
	
	private TagTree siteTree;
	private File siteFile;
	
	public Page(TagTree tree, File f)
	{
		siteTree = tree;
		siteFile = f;
	}
	
	public TagTree getSiteTagTree()
	{
		return siteTree;
	}
	
	public File getSiteFile()
	{
		return siteFile;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
