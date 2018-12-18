package monitor;

import java.io.File;
import javax.swing.JTextPane;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import tools.FileFactory;
import tools.FileType;

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
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onFileCreate(File file) {
		File remotefile = FileFactory.getProcessRemoteFile(file, this.processFile, this.remoteFile);
		FileEntry fileEntry = new FileEntry(file);
		fileEntry.refresh(file);
		Runnable copyRunnable = new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (!fileEntry.refresh(file, timeout)) {
						FileFactory.copyFile(file, remotefile, logtxt);
						break;
					}
				}
			}
		};
		Thread copyThread = new Thread(copyRunnable);
		copyThread.run();
	}
	@Override
	public void onFileChange(File file) {
		File remotefile = FileFactory.getProcessRemoteFile(file, this.processFile, this.remoteFile);
		FileEntry fileEntry = new FileEntry(file);
		fileEntry.refresh(file);
		
		if (remotefile.exists()) {
			while (true) {
				if (!fileEntry.refresh(file, timeout)) {
					FileFactory.copyFile(file, remotefile, logtxt);
					break;
				}
			}
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
