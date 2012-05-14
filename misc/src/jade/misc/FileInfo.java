package jade.misc;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;

import java.util.Date;

public class FileInfo implements Concept {

	private static final long serialVersionUID = -201631174127710143L;
	
	private String name;
	private String path;
	private boolean directory;
	private Date date;
	private long size;
	
	public FileInfo() {
	}

	public FileInfo(String name, String path, boolean directory, Date date, long size) {
		this.name = name;
		this.path = path;
		this.directory = directory;
		this.date = date;
		this.size = size;
	}

	@Slot(mandatory=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Slot(mandatory=false)
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Slot(mandatory=true)
	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	@Slot(mandatory=true)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Slot(mandatory=false)
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "FileInfo [name=" + name + ", path=" + path + ", directory=" + directory + ", date=" + date + ", size=" + size + "]";
	}
}
