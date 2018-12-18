package CommandLine;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import org.apache.commons.io.FileUtils;

/**
 * 
 * @author liuxing
 * @email liuxing2@genomics.cn
 * @date 2018_10_30
 *
 */

public class FileFactory {
	
	private FileFactory() {
		
	}

	public static boolean fileEqual(File file1, File file2) {
		boolean equal = true;
		if (!file1.exists() || !file2.exists()) {
			equal = false;
		}else if (file1.getName().equals(file2.getName()) && file1.lastModified() == file2.lastModified() &&
				file1.length() == file2.length()) {
			equal = true;
		}else {
			equal = false;
		}
		return equal;
	}
	
	public static void copyFile(File localfile, File remotefile, Logger log) {
		copyFile(localfile, remotefile, 0, log);
	}
	
	private static void copyFile(File localfile, File remotefile, int times, Logger log) {
		String loginfo = "";
		try {
			FileUtils.copyFile(localfile, remotefile);
			loginfo = "Copy succeessfully! Copy the new file: " + localfile.getAbsolutePath();
			log.info(loginfo);
		} catch(IOException e) {
			if (times <3) {
				times++;
				loginfo = "Copy failed ! Try copy again for " + String.valueOf(times) + " times " + localfile.getAbsoluteFile();
				log.warn(loginfo);
				remotefile.delete();
				copyFile(localfile, remotefile, times, log);
			}else if (times==3) {
				loginfo = "Copy failed for 3 times!!! Please check it by hand.";
				log.error(loginfo);
			}
		}
	}
	
	public static void removeExsitsFile(File remotefile, Logger log) {
		
		SimpleDateFormat fdf = new SimpleDateFormat("YYYYMMdd");
		String loginfo="";
		File tmpdir = remotefile.getParentFile();
		String projectDirName = tmpdir.getParent();
		while (true) {
			String tmpfilename = tmpdir.getName();
			if (tmpfilename.equals("RAWdata")) {
				projectDirName = tmpdir.getParent();
				break;
			}else {
				tmpdir = tmpdir.getParentFile();
			}
		}
		String filterdirname = projectDirName + File.separator +"filter";
		File filterdir = new File(filterdirname);
		File filterfile = new File(filterdirname + File.separator + remotefile.getName());
		if (filterfile.exists()) {
			long time = filterfile.lastModified();
			String childrendirname = filterdirname + File.separator + fdf.format(new Date(time));
			loginfo = "Move exists filter file to new directory: " + filterfile.getAbsolutePath() ;
			log.info(loginfo);
			File childrendir = new File(childrendirname);
			try {
				FileUtils.moveFileToDirectory(filterfile, childrendir, true);
			} catch (IOException e) {
				e.printStackTrace();
			}				
		}

		loginfo = "Move the exists file to filter directory: " + remotefile.getAbsolutePath();
		log.info(loginfo);
		try {
			FileUtils.moveFileToDirectory(remotefile, filterdir, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
							
	}
	
	public static String getNewFilePath(File file) {
		String filename = file.getName();
		File[] filelist = file.getParentFile().listFiles(new FileFilter() {
			public boolean accept(File listfile) {
				return listfile.getName().indexOf(filename) != -1;
			}			
		});
		int subfilenumber = filelist.length + 1;
		String[] filenamesplit = filename.split("\\.", 2);
		String newfilename = filenamesplit[0] + "_sub" + String.valueOf(subfilenumber) + "." + filenamesplit[1];
		return file.getParent() + File.separator + newfilename;
	}
	
	public static File getDatabaseFile(File lfile, File localDir, File remoteDir) {
		String lfilepath = lfile.getAbsolutePath();
		String rfilepath = lfilepath.replace(localDir.getAbsolutePath(), remoteDir.getAbsolutePath());
		
		return new File(rfilepath);
	}
	
	public static List<File> listfiles(File file, List<File> filelists, FileFilter filefilter) {
		if (file.isDirectory()) {
			for (File scanfile: file.listFiles(filefilter)) {
				if (scanfile.isFile()) {
					filelists.add(scanfile);
				}else {
					listfiles(scanfile, filelists, filefilter);
				}
			}
		}
		return filelists;
	}
	
	public static List<File> listfiles(File file, List<File> filelists) {
		if (file.isDirectory()) {
			for (File scanfile: file.listFiles()) {
				if (scanfile.isFile()) {
					filelists.add(scanfile);
				}else {
					listfiles(scanfile, filelists);
				}
			}
		}
		return filelists;
	}
}
