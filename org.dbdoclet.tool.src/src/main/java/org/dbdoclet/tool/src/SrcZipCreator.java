package org.dbdoclet.tool.src;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class SrcZipCreator extends Task {

	private String zipFile;

	public String getZipFile() {
		return zipFile;
	}

	public void setZipFile(String zipFile) {
		this.zipFile = zipFile;
	}

	@Override
	public void execute() throws BuildException {

		log(zipFile);

		ProductManager pm = new ProductManager();
		try {
			pm.execute();
		} catch (Exception oops) {
			throw new BuildException(oops);
		}
	}

}
