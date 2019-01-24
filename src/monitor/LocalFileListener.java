package monitor;

import java.io.File;
import java.util.HashMap;

import javax.swing.JTextPane;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import tools.AnalysisType;
import tools.FileFactory;
import tools.FileType;
import tools.MachineID;
import tools.SampleFiles;

/**
 * 
 * @author liuxing2
 * @email liuxing2@genomics.cn
 * @date 2018_10_30
 *
 */

public class LocalFileListener implements FileAlterationListener{
	
	private File localFile;
	private File remoteFile;
	private File samplelist;
	private AnalysisType analysistype;
	private MachineID machineID;
	private long timeout;
	private JTextPane logtxt;
	
	public LocalFileListener (String localFile, String remoteFile, String samplelist, AnalysisType analysistype, MachineID machineID, long timeout, JTextPane logtxt) {
		this(new File(localFile), new File(remoteFile), new File(samplelist), analysistype, machineID, timeout, logtxt);
	}
	public LocalFileListener (File localFile, File remoteFile, File samplelist, AnalysisType analysistype, MachineID machineID, long timeout, JTextPane logtxt) {
		this.localFile = localFile;
		this.remoteFile = remoteFile;
		this.samplelist = samplelist;
		this.analysistype = analysistype;
		this.machineID = machineID;
		this.timeout = timeout;
		this.logtxt = logtxt;
	}
	
	@Override
	public void onDirectoryChange(File file) {			
	}

	@Override
	public void onDirectoryCreate(File file) {
	}

	@Override
	public void onDirectoryDelete(File file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFileChange(File file) {
		String filepath = file.getAbsolutePath();
		if (FileFactory.isTransfer(file, machineID)) {
			String remotefilename = null;
			if (analysistype.compareTo(AnalysisType.Protein)==0) {
				HashMap<String, SampleFiles> samplehash = FileFactory.readProteinSampleList(samplelist);
				SampleFiles samplefiles = FileFactory.ProteinSampleFilesFind(file, samplehash, logtxt);
				FileType filetype = filepath.contains("_QC")? FileType.QCFile: samplefiles.getFileType();
				remotefilename = FileFactory.getProteinRemoteFile(file, localFile, remoteFile, samplefiles.getProjectname(), samplefiles.getGroupNumber(), filetype); 
			}else {
				HashMap<String, String> othersamplehash = FileFactory.readOtherSampleList(samplelist);
				String RemoteParentDir = FileFactory.otherTransPathFind(file, othersamplehash, logtxt);
				if (RemoteParentDir != null) {
					remotefilename = FileFactory.getOtherRemoteFile(file, localFile, remoteFile, RemoteParentDir);
				}
			}
			if (remotefilename!=null) {
				File remotefile = new File(remotefilename);
				if (remotefile.exists() && !FileFactory.fileEqual(file, remotefile)) {
					Thread copyThread = new Thread(new Runnable() {
						public void run() {
							FileEntry fileEntry = new FileEntry(file);
							fileEntry.refresh(file);
							while(true) {
								if (!fileEntry.refresh(file, timeout) && !FileFactory.fileEqual(file, remotefile)) {
									if ((file.lastModified() - remotefile.lastModified()) > 6*60*60*1000L) {
										FileFactory.removeExsitsFile(remotefile, logtxt);
									}
									FileFactory.copyFile(file, remotefile, logtxt);
									break;
								}else if(FileFactory.fileEqual(file, remotefile)) {
									break;
								}
							}
						}
					});
					copyThread.start();
				}
			}
		}		
	}

	@Override
	public void onFileCreate(File file) {
		String filepath = file.getAbsolutePath();
		if (FileFactory.isTransfer(file, machineID)) {
			String remotefilename = null;
			if (analysistype.compareTo(AnalysisType.Protein)==0) {
				HashMap<String, SampleFiles> samplehash = FileFactory.readProteinSampleList(samplelist);
				SampleFiles samplefiles = FileFactory.ProteinSampleFilesFind(file, samplehash, logtxt);
				FileType filetype = filepath.contains("_QC")? FileType.QCFile: samplefiles.getFileType();
				remotefilename = FileFactory.getProteinRemoteFile(file, localFile, remoteFile, samplefiles.getProjectname(), samplefiles.getGroupNumber(), filetype); 
			}else {
				HashMap<String, String> othersamplehash = FileFactory.readOtherSampleList(samplelist);
				String RemoteParentDir = FileFactory.otherTransPathFind(file, othersamplehash, logtxt);
				if (RemoteParentDir != null) {
					remotefilename = FileFactory.getOtherRemoteFile(file, localFile, remoteFile, RemoteParentDir);
				}
			}
			if(remotefilename != null) {
				File remotefile = new File(remotefilename);
				if (!FileFactory.fileEqual(file, remotefile)) {
					Thread copyThread = new Thread(new Runnable(){
						public void run() {
							FileEntry fileEntry = new FileEntry(file);
							fileEntry.refresh(file);
							if (remotefile.exists()) {
								FileFactory.removeExsitsFile(remotefile, logtxt);
							}
							while(true) {
								if (!fileEntry.refresh(file,timeout)) {
									FileFactory.copyFile(file, remotefile, logtxt);
									break;
								}
							}
						}
					});
					copyThread.start();
				}
			}
		}			
	}


	@Override
	public void onFileDelete(File file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart(FileAlterationObserver fileAlterationObserver) {
		
	}

	@Override
	public void onStop(FileAlterationObserver fileAlterationObserver) {
		// TODO Auto-generated method stub
		
	}

}
