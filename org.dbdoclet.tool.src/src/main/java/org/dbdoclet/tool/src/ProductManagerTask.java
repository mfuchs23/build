package org.dbdoclet.tool.src;

import java.io.File;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class ProductManagerTask extends Task {

	private File jarFile;
	private File zipFile;
	private File buildFile;
	private File workDir;
	private String product;

	private final ArrayList<FileSet> fileSetList = new ArrayList<FileSet>();
	private File destJavaDir;

	public void addFileset(FileSet fileset) {
		fileSetList.add(fileset);
	}

	@Override
	public void execute() throws BuildException {

		ArrayList<File> srcDirList = new ArrayList<File>();

		for (FileSet fileSet : fileSetList) {
			srcDirList.add(fileSet.getDir());
		}

		Project project = getProject();
		ProductManager productManager = new ProductManager();
		productManager.setJarFile(jarFile);
		productManager.setZipFile(zipFile);
		productManager.setProduct(product);
		productManager.setBuildFile(buildFile);
		productManager.setWorkDir(workDir);
		productManager.setDestJavaDir(destJavaDir);
		productManager.setSrcDirList(srcDirList);
		project.log(String.format("jarfile = %s", productManager.getJarFile()));
		project.log(String.format("zipfile = %s", productManager.getZipFile()));
		project.log(String.format("workdir = %s", productManager.getWorkDir()));
		project.log(String.format("buildfile = %s",
				productManager.getBuildFile()));
		project.log(String.format("srcDirList = %s",
				productManager.getSrcDirList()));
		try {
			productManager.execute();
		} catch (Exception oops) {
			throw new BuildException(oops);
		}
	}

	public File getBuildFile() {
		return buildFile;
	}

	public File getJarFile() {
		return jarFile;
	}

	public String getProduct() {
		return product;
	}

	public File getWorkDir() {
		return workDir;
	}

	public File getZipFile() {
		return zipFile;
	}

	public void setBuildFile(String buildFileName) {

		Project project = getProject();
		this.buildFile = project.resolveFile(buildFileName);
	}

	public void setJarFile(String jarFileName) {

		Project project = getProject();
		this.jarFile = project.resolveFile(jarFileName);
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public void setWorkDir(String workDirName) {

		Project project = getProject();
		this.workDir = project.resolveFile(workDirName);
		this.destJavaDir = new File(workDir, "java");
	}

	public void setZipFile(String zipFileName) {

		Project project = getProject();
		this.zipFile = project.resolveFile(zipFileName);
	}
}
