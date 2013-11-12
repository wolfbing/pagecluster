package com.datamining.cluster.nesttree;

import java.io.File;

import com.datamining.sde.basictype.TagTree;

/**
 * 页面类, 包含源文件和解析出来的tagTree
 *
 */
public class Page {
	
	private TagTree siteTree; // 页面的tagTree
	private File siteFile; // 源文件
	
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
	


}
