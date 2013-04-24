package org.dbdoclet.packman;

import java.io.File;

public interface Creator {

	public boolean create() throws Exception;

	public void setSpecFile(File specFile) throws Exception;

	public void setBuildDir(File buildDir);

	public void setKitsDir(File kitsDir);

	public void setPrefix(String prefixDir);

	public void setUser(String user);

	public void setGroup(String group);

	public void setVerbose(boolean verbose);
}
