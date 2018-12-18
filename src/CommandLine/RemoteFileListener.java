package CommandLine;

import java.io.File;
import org.apache.log4j.Logger;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import monitor.FileEntry;

public class RemoteFileListener implements FileAlterationListener{
	
	private File remotePath;
	private File databasePath;
	private long timeout=1;
	private Logger log;
	
	public RemoteFileListener(File remotePath, File databasePath, Logger log) {
		this.remotePath = remotePath;
		this.databasePath = databasePath;
		this.log = log;
	}
	public RemoteFileListener(String remotePath, String databasePath, Logger log) {
		this(new File(remotePath), new File(databasePath), log);
	}

	@Override
	public void onDirectoryChange(File arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDirectoryCreate(File arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDirectoryDelete(File arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFileChange(File file) {
		File remotefile = FileFactory.getDatabaseFile(file, this.remotePath, this.databasePath);
		FileEntry fileEntry = new FileEntry(file);
		fileEntry.refresh(file);
		if (remotefile.exists()) {
			while (true) {
				if (!fileEntry.refresh(file, timeout)) {
					FileFactory.copyFile(file, remotefile, log);
					break;
				}
			}
		}		
		
	}

	@Override
	public void onFileCreate(File file) {
		File remotefile = FileFactory.getDatabaseFile(file, this.remotePath, this.databasePath);
		FileEntry fileEntry = new FileEntry(file);
		fileEntry.refresh(file);
		Runnable copyRunnable = new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (!fileEntry.refresh(file, timeout)) {
						FileFactory.copyFile(file, remotefile, log);
						break;
					}
				}
			}
		};
		Thread copyThread = new Thread(copyRunnable);
		copyThread.run();
		
	}

	@Override
	public void onFileDelete(File arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart(FileAlterationObserver arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStop(FileAlterationObserver arg0) {
		// TODO Auto-generated method stub
		
	}
	

}
