package CommandLine;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;

public class clusterFileMonitor {
	
	private File remoteDir;
	private File databaseDir;
	private long monitortimeout;
	private FileAlterationMonitor filemonitor;
	private Logger log;
	
	public clusterFileMonitor(File remotefile, File databasefile, long monitortimeout, Logger log) {
		this.remoteDir = remotefile;
		this.databaseDir = databasefile;
		this.monitortimeout = monitortimeout;
		this.log = log;
		this.filemonitor = new FileAlterationMonitor(this.monitortimeout*1000);
		RemoteFileListener filelistener = new RemoteFileListener(this.remoteDir, this.databaseDir, log);
		FileAlterationObserver fileobserver = new FileAlterationObserver(this.remoteDir);
		fileobserver.addListener(filelistener);
		this.filemonitor.addObserver(fileobserver);
	}
	
	public clusterFileMonitor(String remotefile, String databasefile, long monitortimeout, Logger log) {
		this(new File(remotefile), new File(databasefile), monitortimeout, log);
	}
	
	public void checkRemote() {
		List<File> remotefiles = new ArrayList<File>();
		remotefiles = FileFactory.listfiles(this.remoteDir, remotefiles);
		for (File remotefile: remotefiles) {
			File dfile = FileFactory.getDatabaseFile(remotefile, remoteDir, databaseDir);
			if (!FileFactory.fileEqual(remotefile, dfile)) {
				FileFactory.copyFile(remotefile, dfile, log);
			}
		}
	}
	
	public void start() {
		String loginfo;
		loginfo = "Check the remote directory and copy the new generateed files!\n ";
		log.info(loginfo);
		Thread checkRemoteThread = new Thread(new Runnable() {
			@Override
			public void run() {
				checkRemote();
			}
		});
		checkRemoteThread.start();
		try {
			this.filemonitor.start();
			loginfo = "File Monitor start !\n";
			log.info(loginfo);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void stop() {
		try {
			this.filemonitor.stop();
			String loginfo;
			loginfo = "File Monitor top !\n";
			log.info(loginfo);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}	

}
