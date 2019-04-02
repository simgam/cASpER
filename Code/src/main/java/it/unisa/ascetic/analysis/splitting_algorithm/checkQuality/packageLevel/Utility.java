package it.unisa.ascetic.analysis.splitting_algorithm.checkQuality.packageLevel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Vector;
import java.util.regex.Pattern;

public class Utility {

	public static String readFile(String nomeFile) throws IOException {
		InputStream is = null;
		InputStreamReader isr = null;

		StringBuffer sb = new StringBuffer();
		char[] buf = new char[1024];
		int len;

		try {
			is = new FileInputStream(nomeFile);
			isr = new InputStreamReader(is);

			while ((len = isr.read(buf)) > 0)
				sb.append(buf, 0, len);

			return sb.toString();
		} finally {
			if (isr != null)
				isr.close();
		}
	}
	
	
	public static String getClassFromSrcMLstring(String srcMLstring, String start, String end){
		
		int countClass = 0;
		
		String toReturn = "";
		
		Pattern newLine = Pattern.compile("\n");
		String[] lines = newLine.split(srcMLstring);
		
		for(int i=0; i<lines.length; i++){
			if(lines[i].contains(start)){
				countClass++;
				toReturn+=lines[i];
			}
			if(lines[i].contains(end)){
				countClass--;
				toReturn+=lines[i];
				if(countClass == 0)
					return toReturn;
					
			} else {
				toReturn+=lines[i];
			}
		}
		
		return null;
		
	}
	
	
	public void copyDirectory(File srcPath, File dstPath) throws IOException {

		if (srcPath.isDirectory()) {

			if (!dstPath.exists()) {

				dstPath.mkdir();

			}

			String files[] = srcPath.list();

			for (int i = 0; i < files.length; i++) {
				copyDirectory(new File(srcPath, files[i]), new File(dstPath,
						files[i]));

			}

		}

		else {

			if (!srcPath.exists()) {

				System.exit(0);

			}

			else

			{

				InputStream in = new FileInputStream(srcPath);
				OutputStream out = new FileOutputStream(dstPath);
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];

				int len;

				while ((len = in.read(buf)) > 0) {

					out.write(buf, 0, len);

				}

				in.close();

				out.close();

			}

		}

	}
	
	public static boolean DelDir(File dir)
	  {
	    if (dir.isDirectory())
	    {
	      String[] contenuto = dir.list();
	      for (int i=0; i<contenuto.length; i++)
	      {
	        boolean success = DelDir(new File(dir, contenuto[i]));
	        if (!success) { return false; }
	      }
	    }
	    return dir.delete();
	  }

	public static void writeFile(String pContent, String pPath){
        File file=new File(pPath);
        FileWriter fstream;
        try {
            fstream = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(pContent);
            out.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
	
	public Vector<File> listJavaFiles(File pDirectory) {
		Vector<File> javaFiles=new Vector<File>(); 
		File[] fList = pDirectory.listFiles();

		if(fList != null) {
			for (File file : fList) {
				if (file.isFile()) {
					if(file.getName().contains(".java")) {
						javaFiles.add(file);
					}
				} else if (file.isDirectory()) {
					File directory = new File(file.getAbsolutePath());
					javaFiles.addAll(listJavaFiles(directory));
				}
			}
		}
		return javaFiles;
	}
	
	public Vector<File> searchForStringsFile(File pDirectory) {
		Vector<File> javaFiles=new Vector<File>(); 
		File[] fList = pDirectory.listFiles();

		if(fList != null) {
			for (File file : fList) {
				if (file.isFile()) {
					if(file.getName().equals("strings.xml")) {
						javaFiles.add(file);
					}
				} else if (file.isDirectory()) {
					File directory = new File(file.getAbsolutePath());
					javaFiles.addAll(searchForStringsFile(directory));
				}
			}
		}
		return javaFiles;
	}
}
