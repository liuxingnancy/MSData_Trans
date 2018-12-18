package tools;

public class SampleFiles {
	
	private String projectname;
	private String samplename;
	private int groupnumber;
	private FileType filetype;
	private int filenumber;
	
	public SampleFiles(String projectname, String samplename, int groupnumber, FileType filetype, int filenumber) {
		this.projectname = projectname;
		this.samplename = samplename;
		this.groupnumber = groupnumber;
		this.filetype = filetype;
		this.filenumber = filenumber;
	}
	public SampleFiles(String projectname, String samplename, FileType filetype, int filenumber) {
		this(projectname, samplename, 0, filetype, filenumber);
	}
	
	public void setProjectName(String projectname) {
		this.projectname = projectname;
	}
	public void setSampleName(String samplename) {
		this.samplename = samplename;
	}
	public void setgroupnumber (int groupnumber) {
		this.groupnumber =groupnumber;
	}
	public void setFileType (FileType filetype) {
		this.filetype = filetype;
	}
	public void detFileNumber(int filenumber) {
		this.filenumber = filenumber;
	}
	
	public String getProjectname() {
		return this.projectname;
	}
	public String getSampleName() {
		return this.samplename;
	}
	public int getGroupNumber() {
		return this.groupnumber;
	}
	public FileType getFileType() {
		return this.filetype;
	}
	public int getFileNumber() {
		return this.filenumber;
	}

}
