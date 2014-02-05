package com.example.concealtest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.StatFs;

public class FileUtils {
	public static boolean isSDcardAvaiable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	@SuppressWarnings("deprecation")
	public static long getAvailableSpaceInMB() {
		final long SIZE_KB = 1024L;
		final long SIZE_MB = SIZE_KB * SIZE_KB;
		long availableSpace = -1L;
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		availableSpace = (long) stat.getAvailableBlocks()
				* (long) stat.getBlockSize();
		return availableSpace / SIZE_MB;
	}
	
	public static boolean isFileAvaiable(String path){
		try{
			File file=new File(path);
			if(file.exists()&&file.isFile()){
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public static String loadJsonFromAsset(Context c,String fileName){
		AssetManager assetManager=c.getAssets();
		try {
			InputStream input=assetManager.open(fileName);
			int size=input.available();
			byte[] buffer=new byte[size];
			input.read(buffer);
			input.close();
			return new String(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void copyFile(String src, String dst)  {
		File source=new File(src);
		File dest=new File(dst);
		if(!source.exists()||!source.isFile()) return;
		
	    InputStream in;
	    OutputStream out ;
		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);

		    // Transfer bytes from in to out
		    byte[] buf = new byte[1024];
		    int len;
		    while ((len = in.read(buf)) > 0) {
		        out.write(buf, 0, len);
		    }
		    in.close();
		    out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	}
	
	/**
	 * Delete one file
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String path){
		File file=new File(path);
		if(file.exists()&&file.isFile()){
			return file.delete();
		}
		return false;
	}
	
	/**
	 * Delete both folder and its content, 
	 * @param pathToFolder
	 * @param shouldDeleteFolder	: true if you want to delete the root folder too
	 */
	public static boolean deleteFiles(String pathToFolder,boolean shouldDeleteFolder){
		boolean res=true;
		File dir=new File(pathToFolder);
		if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	        	File f=new File(dir,children[i]);
	        	res=res&&deleteFiles(f.getPath(),true);
	        }
	        if(shouldDeleteFolder){
	        	dir.delete();
	        }
	    }else {
	    	res=res&&dir.delete();
	    }
		return res;
	}
	
	
}
