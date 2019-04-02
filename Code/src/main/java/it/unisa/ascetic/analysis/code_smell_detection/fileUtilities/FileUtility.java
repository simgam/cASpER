package it.unisa.ascetic.analysis.code_smell_detection.fileUtilities;

import org.jsoup.Jsoup;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Pattern;

public class FileUtility {
	
	public static String[] readFileLineByLine(File fin) throws IOException {
		ArrayList<String> arrayLines = new ArrayList<String>();
		FileInputStream fis = new FileInputStream(fin);

		//Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;
		while ((line = br.readLine()) != null) {

			arrayLines.add(line);
		}

		br.close();

		String[] lines = new String[arrayLines.size()];
		
		int index = 0;
		
		for(String row: arrayLines) {
			lines[index] = row;
			index++;
		}
		return lines;
	}

	public static String[] readDirectories(String rootDirectory) {
		File file = new File(rootDirectory);
		String[] directories = file.list(new FilenameFilter() {
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		Arrays.sort(directories, Collator.getInstance());
		return directories;
	}

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
			else {

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

	public static boolean DelDir(File dir) {
		if (dir.isDirectory()) {
			String[] contenuto = dir.list();
			for (int i=0; i<contenuto.length; i++){
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

	public static Vector<File> listJavaFiles(File pDirectory) {
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

	public static Vector<File> listRepositoryDataFiles(File pDirectory) {
		Vector<File> gitRepoDataFiles=new Vector<File>(); 
		File[] fList = pDirectory.listFiles();

		if(fList != null) {
			for (File file : fList) {
				if (file.isFile()) {
					if(file.getName().contains(".data")) {
						gitRepoDataFiles.add(file);
					}
				} else if (file.isDirectory()) {
					File directory = new File(file.getAbsolutePath());
					gitRepoDataFiles.addAll(listRepositoryDataFiles(directory));
				}
			}
		}
		return gitRepoDataFiles;
	}

	public static Vector<File> listIssueFiles(File pDirectory) {
		Vector<File> gitRepoDataFiles=new Vector<File>(); 
		File[] fList = pDirectory.listFiles();

		if(fList != null) {
			for (File file : fList) {
				if (file.isFile()) {
					if(file.getName().contains("_issues")) {
						gitRepoDataFiles.add(file);
					}
				} else if (file.isDirectory()) {
					File directory = new File(file.getAbsolutePath());
					gitRepoDataFiles.addAll(listIssueFiles(directory));
				}
			}
		}
		return gitRepoDataFiles;
	}

	@SuppressWarnings("resource")
	public static void copyFile(File sourceFile, File destFile)
			throws IOException {
		if (!sourceFile.exists()) {
			return;
		}
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		FileChannel source = null;
		FileChannel destination = null;
		source = new FileInputStream(sourceFile).getChannel();
		destination = new FileOutputStream(destFile).getChannel();
		if (destination != null && source != null) {
			destination.transferFrom(source, 0, source.size());
		}
		if (source != null) {
			source.close();
		}
		if (destination != null) {
			destination.close();
		}

	}

	public static String convertMouthFromString(String pMounth) {

		if(pMounth.equals("Jan"))
			return "01";
		else if(pMounth.equals("Feb"))
			return "02";
		else if(pMounth.equals("Mar"))
			return "03";
		else if(pMounth.equals("Apr"))
			return "04";
		else if(pMounth.equals("May"))
			return "05";
		else if(pMounth.equals("Jun"))
			return "06";
		else if(pMounth.equals("Jul"))
			return "07";
		else if(pMounth.equals("Aug"))
			return "08";
		else if(pMounth.equals("Sep"))
			return "09";
		else if(pMounth.equals("Oct"))
			return "10";
		else if(pMounth.equals("Nov"))
			return "11";
		else return "12";
	}
	
	public static String getText(String url) throws Exception {
		URL website = new URL(url);
		URLConnection connection = website.openConnection();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						connection.getInputStream()));

		StringBuilder response = new StringBuilder();
		String inputLine;

		while ((inputLine = in.readLine()) != null) 
			response.append(inputLine);

		in.close();

		String textOnly = Jsoup.parse(response.toString()).text();

		return textOnly;
	}

	public static int countLines(String str){
		String[] lines = str.split("\r\n|\r|\n");
		return  lines.length;
	}

}
