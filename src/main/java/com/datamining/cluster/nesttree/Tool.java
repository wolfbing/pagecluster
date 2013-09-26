package com.datamining.cluster.nesttree;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Tool {
	
	public static List<File> getAllFilesUnderAFolder(String dir)
	{
		List<File> fl = new ArrayList<File>();
		File file = new File(dir);
		if(file.isFile())
		{
			fl.add(file);
		}
		if(file.isDirectory())
		{
			File [] fileArr = file.listFiles();
			for(File f: fileArr)
			{
				fl.addAll(getAllFilesUnderAFolder(f.getAbsolutePath()) );
			}
		}
		
		return fl;
	}
	
	public static String createUrl(String dir,String filename)
	{
		File f = new File(dir);
		return f.toURI().toString()+URLEncoder.encode(filename).replace("+", "%20");
	}
	
	public static void saveAs(File f,String outPath)
	{
		f.renameTo(new File(outPath+f.getName()));
		
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
