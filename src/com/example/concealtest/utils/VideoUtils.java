package com.example.concealtest.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class VideoUtils {

	public static boolean downloadVideoFromUrl(String videoUrl,String path,String fileName,DownloadProgressCallback callback) throws IOException{
			if(fileName==null||path==null) return false;
			
			try {
		        URL url = new URL(videoUrl);
		        URLConnection connection = url.openConnection();
		        connection.connect();
		        // this will be useful so that you can show a typical 0-100% progress bar
		        int fileLength = connection.getContentLength();

		        // download the file
		        File dir = new File (path);
				if (dir.exists() == false) {
					dir.mkdirs();
				}
				
				File file = new File(dir, fileName);
		        InputStream input = new BufferedInputStream(url.openStream());
		        OutputStream output = new FileOutputStream(file);

		        byte data[] = new byte[1024];
		        long total = 0;
		        int count;
	            int currentProgress=0,lastProgress=0;
		        while ((count = input.read(data)) != -1) {
		            total += count;
		            output.write(data, 0, count);
		            // publishing the progress....
		            if (fileLength > 0&&callback!=null){
		            	  currentProgress=(int) (total * 100 / fileLength);
		            	  if(currentProgress!=lastProgress){
		            		  //Only update when neccessary
		            		  callback.onDownloaded(currentProgress);
		            		  lastProgress=currentProgress;
		            	  }
		              }
		        }

		        output.flush();
		        output.close();
		        input.close();
		        return true;
		    } catch (IOException e) {
		        e.printStackTrace();
		        return false;
		    }
	}
	
	public interface DownloadProgressCallback{
		public void onDownloaded(int progress);
	}
}
