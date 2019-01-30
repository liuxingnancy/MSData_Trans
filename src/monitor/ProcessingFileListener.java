package monitor;

import java.io.File;
import javax.swing.JTextPane;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import tools.FileFactory;

/**
 * 
 * @author liuxing
 * @email liuxing2@genomics.cn
 * @date 2018_10_30
 *
 */

public class ProcessingFileListener implements FileAlterationListener {
	
	private File processFile;
	private File remoteFile;
	private long timeout = 5;
	private JTextPane logtxt;

	public ProcessingFileListener (String processFile, String remoteFile,  JTextPane logtxt) {
		this(new File(processFile), new File(remoteFile), logtxt);
	}
	public ProcessingFileListener (File processFile, File remoteFile,  JTextPane logtxt) {
		this.processFile = processFile;
		this.remoteFile = remoteFile;
		this.logtxt = logtxt;
	}
	
	@Override
	public void onStart(FileAlterationObserver observer) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDirectoryCreate(File directory) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDirectoryChange(File directory) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDirectoryDelete(File directory) {		
	}
	@Override
	public void onFileCreate(File file) {
		File remotefile = FileFactory.getProcessRemoteFile(file, this.processFile, this.remoteFile);
		if (!FileFactory.fileEqual(file, remotefile)) {
			FileEntry fileEntry = new FileEntry(file);
			fileEntry.refresh(file);
			Thread copyThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						if (!fileEntry.refresh(file, timeout) && file.renameTo(file)) {
							FileFactory.copyFile(file, remotefile, logtxt);
							break;
						}
					}
				}
			});
			copyThread.start();
		}		
	}
	
	@Override
	public void onFileChange(File file) {
		File remotefile = FileFactory.getProcessRemoteFile(file, this.processFile, this.remoteFile);
		if (remotefile!=null && remotefile.exists() && file.renameTo(file) && !FileFactory.fileEqual(file, remotefile)) {
			FileEntry fileEntry = new FileEntry(file);
			fileEntry.refresh(file);
			Thread copyThread = new Thread(new Runnable() {
				@Override
				public void run() {					
					if(!fileEntry.refresh(file, 60) && file.renameTo(file) && !FileFactory.fileEqual(file, remotefile)) {
						FileFactory.copyFile(file,  remotefile,  logtxt);							
					}					
				}
			});
			copyThread.start();
		}
	}
	@Override
	public void onFileDelete(File file) {
		// TODO Auto-generated method stub		
	}
	@Override
	public void onStop(FileAlterationObserver observer) {
		// TODO Auto-generated method stub
		
	}

}
