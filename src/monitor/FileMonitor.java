package monitor;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.text.SimpleDateFormat;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import tools.AnalysisType;
import tools.FileFactory;
import tools.FileType;
import tools.MachineID;
import tools.SampleFiles;

/**
 * 
 * @author liuxing
 * @email liuxing2@genomics.cn
 * @date 2018_10_30
 *
 */

public class FileMonitor {

	private File localfile;
	private File remotefile;
	private File processingfile;
	private File samplelist;
	private long monitortimeout;
	private long fileChangeCheckTimeout;
	private FileFilter processingfilefilter;
	private FileAlterationMonitor filemonitor;
	private AnalysisType analysistype;
	private MachineID machineID;
	private JTextPane logtxt;
	private static final SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
	
	
	public FileMonitor(String localfile, String remotefile, String processingfile, String samplelist, AnalysisType analysistype, MachineID machineID, long monitortimeout, long fileChangeCheckTimeout, JTextPane logtxt) {
		this (new File(localfile), new File(remotefile), new File(processingfile), new File(samplelist), analysistype, machineID, monitortimeout, fileChangeCheckTimeout, logtxt);
	}
	
	public FileMonitor(File localfile, File remotefile, File processingfile, File samplelist, AnalysisType analysistype,MachineID machineID, long monitortimeout, long fileChangeCheckTimeout, JTextPane logtxt){
		this.localfile = localfile;
		this.remotefile = remotefile;
		this.processingfile = processingfile;
		this.samplelist = samplelist;
		this.monitortimeout = monitortimeout;
		this.fileChangeCheckTimeout = fileChangeCheckTimeout;
		this.analysistype = analysistype;
		this.machineID = machineID;
		this.logtxt = logtxt;
		this.processingfilefilter = new RAWDataFileFilter();
		this.filemonitor = new FileAlterationMonitor(this.monitortimeout*1000);
		LocalFileListener filelistener = new LocalFileListener(this.localfile, this.remotefile, this.samplelist, this.analysistype, this.machineID, this.fileChangeCheckTimeout, this.logtxt);
		FileAlterationObserver fileobserver = new FileAlterationObserver(this.localfile);
		fileobserver.addListener(filelistener);
		this.filemonitor.addObserver(fileobserver);
		if (this.processingfile != null) {
			ProcessingFileListener processingfilelistener = new ProcessingFileListener(this.processingfile, this.remotefile, this.logtxt);
			FileAlterationObserver processingfileobserver = new FileAlterationObserver(this.processingfile, this.processingfilefilter);
			processingfileobserver.addListener(processingfilelistener);
			this.filemonitor.addObserver(processingfileobserver);
		}
	}
	
	public class QCfileFilter implements FileFilter {

		@Override
		public boolean accept(File file) {
			String filepath = file.getAbsolutePath();
			return filepath.contains("_QC");
		}		
	}
	
	public class projectFileFilter implements FileFilter {
		@Override
		public boolean accept(File file) {
			String filepath = file.getAbsolutePath();
			return filepath.contains("DATA") || filepath.contains("_QC");
		}
	}
	
	public class projectFileFilter2 implements FileFilter{
		@Override
		public boolean accept(File file) {
			String filepath = file.getAbsolutePath();
			return !filepath.contains("_QC");
		}
		
	}
	
	public class RAWDataFileFilter implements FileFilter {
		
		@Override
		public boolean accept(File file) {
			String filepath = file.getAbsolutePath();
			return !filepath.contains("RAWdata") && !filepath.contains("filter");
		}
	}
	
	public class remoteFileFilter implements FileFilter {

		@Override
		public boolean accept(File file) {
			String filepath = file.getAbsolutePath();
			return filepath.contains("RAWdata") || filepath.contains("_QC");
		}	
	}
		
	public void proteinCheckLocal() {
		List<File> qcfiles = new ArrayList<File>();
		qcfiles = FileFactory.listfiles(this.localfile, qcfiles, "_QC", true);
		List<File> localprojectfiles = new ArrayList<File>();			
		localprojectfiles = FileFactory.listfiles(this.localfile, localprojectfiles, "_QC", false);
		SampleFiles samplefiles ;
		HashMap<String, SampleFiles> samplehash = FileFactory.readProteinSampleList(this.samplelist);
		String rfilename;
		for (File lfile: localprojectfiles) {
			if (!FileFactory.isTransfer(lfile, machineID)) continue; 
			samplefiles = FileFactory.ProteinSampleFilesFind(lfile, samplehash, logtxt);
			if (samplefiles == null) {
				continue;
			}else {
				rfilename = FileFactory.getProteinRemoteFile(lfile, this.localfile, this.remotefile, samplefiles.getProjectname(), samplefiles.getGroupNumber(), samplefiles.getFileType());
			}
			File rfile = new File(rfilename);
			if (!FileFactory.fileEqual(lfile, rfile)) {
				if (rfile.exists() && (lfile.lastModified() - rfile.lastModified()) > 6*60*60*1000L) {
					FileFactory.removeExsitsFile(rfile, logtxt);
				}
				FileFactory.copyFile(lfile, rfile, logtxt);
			}
		}
		
		String remoteQCfilename;
		for (File qcfile : qcfiles) {
			samplefiles = FileFactory.ProteinSampleFilesFind(qcfile, samplehash, logtxt);
			if (samplefiles == null) {
				continue;
			}else {
				String projectid = samplefiles.getProjectname();
				remoteQCfilename = FileFactory.getProteinRemoteFile(qcfile, this.localfile, this.remotefile, projectid, FileType.QCFile);
			}
			File remoteQCfile = new File(remoteQCfilename);
			if (!FileFactory.fileEqual(qcfile, remoteQCfile)) {
				if (remoteQCfile.exists()) {
					FileFactory.removeExsitsFile(remoteQCfile, logtxt);
				}
				FileFactory.copyFile(qcfile, remoteQCfile, logtxt);
			}
		}
	}
	
	public void otherCheckLocal() {
		List<File> localprojectfiles = new ArrayList<File>();
		localprojectfiles = FileFactory.listfiles(this.localfile, localprojectfiles);
		HashMap<String, String> samplehash = FileFactory.readOtherSampleList(this.samplelist);
		String rfilename;
		String remoteParentDir = null;
		for (File lfile: localprojectfiles) {
			if (!FileFactory.isTransfer(lfile, machineID)) continue;
			remoteParentDir = FileFactory.otherTransPathFind(lfile, samplehash, logtxt);
			if (remoteParentDir == null) {
				continue;
			}else {
				rfilename = FileFactory.getOtherRemoteFile(lfile, this.localfile, this.remotefile, remoteParentDir);
			}
			File rfile = new File(rfilename);
			if (!FileFactory.fileEqual(lfile, rfile)) {
				if (rfile.exists() && (lfile.lastModified() - rfile.lastModified()) > 6*60*60*1000L) {
					FileFactory.removeExsitsFile(rfile, logtxt);
				}
				FileFactory.copyFile(lfile, rfile, logtxt);
			}
		}
	}
	
	public void checkProcess() {
		List<File> processfiles = new ArrayList<File>();
		processfiles = FileFactory.listfiles(this.processingfile, processfiles, "RAWdata", "filter", false);
		for (File processfile : processfiles) {
			File rfile = FileFactory.getProcessRemoteFile(processfile, this.processingfile, this.remotefile);
			if (!FileFactory.fileEqual(processfile, rfile)) {
				FileFactory.copyFile(processfile, rfile, logtxt);
			}
		}
	}
	
	public void start(){
		String loginfo = "";
		loginfo = df.format(new Date()) + " Check the local directory and copy the new generated files ! \n";
		try {
			logtxt.getDocument().insertString(0, loginfo, logtxt.getStyle("blue"));
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		AnalysisType proteinAnalysisType = AnalysisType.getAnalysisType("Protein");
		Runnable checkLocalRunnable = new Runnable() {
			@Override
			public void run() {
				if (analysistype.compareTo(proteinAnalysisType) == 0) {
					proteinCheckLocal();
				}else {
					otherCheckLocal();
				}		
			}			
		};
		Thread checkLocalThread = new Thread(checkLocalRunnable);
		checkLocalThread.start();
		
		loginfo = df.format(new Date()) + " Check the processing directory and copy the new generated files ! \n";
		try {
			logtxt.getDocument().insertString(0, loginfo, logtxt.getStyle("blue"));
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		if (this.processingfile != null) {
			Runnable checkProcessRunnable = new Runnable() {
				@Override
				public void run() {
					checkProcess();
				}
			};
			Thread checkProcessThread = new Thread(checkProcessRunnable);
			checkProcessThread.start();
		}
		
		try {
			this.filemonitor.start();
			loginfo = df.format(new Date()) +" File monitor start !\n";
			logtxt.getDocument().insertString(0, loginfo, logtxt.getStyle("blue"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stop(){		
		try {
			this.filemonitor.stop();
			String loginfo = df.format(new Date()) +" File monitor stop !\n";
			logtxt.getDocument().insertString(0, loginfo, logtxt.getStyle("blue"));
		} catch (Exception e ) {
			e.printStackTrace();
		}
	}

}
