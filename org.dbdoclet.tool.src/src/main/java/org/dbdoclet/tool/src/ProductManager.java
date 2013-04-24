package org.dbdoclet.tool.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.option.DirectoryOption;
import org.dbdoclet.option.FileOption;
import org.dbdoclet.option.OptionList;
import org.dbdoclet.option.StringOption;
import org.dbdoclet.progress.InfoListener;
import org.dbdoclet.service.ExecResult;
import org.dbdoclet.service.ExecServices;
import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.ResourceServices;

/**
 * Die Klasse {@code ProductManager} realisiert ein Werkzeug für die
 * Kommandozeile, das für ein OpenSource-Prdukt aus dem Inhalt des
 * jars-Verzeichnisses ein Projekt erstellt, welches die Quellen und eine
 * Build-Datei enthält.
 * 
 * @author Michael Fuchs
 * 
 */
public class ProductManager implements InfoListener {

	private static final Log logger = LogFactory.getLog(ProductManager.class);

	public static void main(String[] args) throws IOException {

		ProductManager pm = new ProductManager();
		if (pm.setCommandLineArgs(args)) {
			pm.execute();
		}
	}

	private File jarFile;
	private final Pattern filter = Pattern.compile("org/dbdoclet/.*\\.class");
	private ArrayList<File> srcDirList;
	private File workDir = new File("./distrib/src");
	private File destJavaDir = new File(workDir, "java");
	private File buildFile;
	private String product;
	private File zipFile;

	private void copyBuildFile() {

		try {

			String buildCode = ResourceServices.getResourceAsString(
					"/gpl-build.xml", this.getClass().getClassLoader());

			if (buildCode != null) {
				buildCode = buildCode.replace("${zipfile}",
						zipFile.getAbsolutePath());
				File gplBuildFile = new File(workDir, "gpl-build.xml");
				FileServices.writeFromString(gplBuildFile, buildCode);
			} else {
				pl("Can't find resource %s", "gpl-build.xml");
			}

			String header = ResourceServices.getResourceAsString(
					"/gpl-header.txt", this.getClass().getClassLoader());

			if (header != null) {

				header = header.replace("${product}", product);
				File gplHeaderFile = new File(workDir, "gpl-header.txt");
				FileServices.writeFromString(gplHeaderFile, header);

			} else {
				pl("Can't find resource %s!", "gpl-header.txt");
			}

			if (buildFile != null) {
				File destFile = new File(workDir, "build.xml");
				FileServices.copyFileToFile(buildFile, destFile);
			} else {
				pl("Cant find build file!");
			}

		} catch (IOException oops) {
			logger.error(oops);
		}
	}

	private void copySourceFile(ZipFile jarArchive, ZipEntry entry)
			throws IOException {

		String name = entry.getName();

		if (name.contains("$")) {
			return;
		}

		// pl("Source file %s.", name);

		if (filter.matcher(name).matches()) {

			String srcPath;

			if (name.endsWith(".class")) {
				srcPath = FileServices.getFileBase(name) + ".java";
			} else {
				srcPath = name;
			}

			boolean copied = false;

			for (File srcDir : srcDirList) {

				if (srcDir.exists() == false) {
					pl("FATAL: Source directory %s does not exist!",
							srcDir.getAbsolutePath());
				}

				File srcFile = new File(FileServices.appendFileName(srcDir,
						srcPath));

				if (srcFile.exists()) {

					String destPath = FileServices.appendFileName(destJavaDir,
							srcPath);
					File destFile = new File(destPath);
					// pl("%s -> %s", srcFile.getPath(), destFile.getPath());
					FileServices.copyFileToFile(srcFile, destFile);
					copied = true;
				}

			}

			if (copied == false) {
				pl("WARNING: Sourcedatei %s could not be found! Perhaps an inner class?",
						srcPath);
			}

		} else {

			InputStream is = jarArchive.getInputStream(entry);
			String destPath = FileServices.appendFileName(destJavaDir, name);
			File destFile = new File(destPath);
			// pl("%s -> %s", name, destFile.getPath());

			destFile.getParentFile().mkdirs();
			FileServices.writeFromStream(destFile.getAbsolutePath(), is);

		}
	}

	private void createSrcZip() throws ZipException, IOException {

		// pl(String.format("+++ %s", jarFile.getName()));

		if (jarFile == null) {
			throw new IllegalStateException("The jar file is undefined!");
		}

		if (zipFile == null) {
			throw new IllegalStateException(
					"The destination zip file is undefined!");
		}

		if (product == null) {
			throw new IllegalStateException("The product name is undefined!");
		}

		if (buildFile == null) {
			throw new IllegalStateException("The build file is undefined!");
		}

		if (workDir == null) {
			throw new IllegalStateException(
					"The working directory is undefined!");
		}

		if (destJavaDir == null) {
			throw new IllegalStateException(
					"The java destination directory is undefined!");
		}

		workDir.mkdirs();
		destJavaDir.mkdirs();

		ZipInputStream zis = null;

		try {

			ZipFile jarArchive = new ZipFile(jarFile);
			FileInputStream fis = new FileInputStream(jarFile);
			zis = new ZipInputStream(fis);

			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {

				// pl("zip entry = %s", entry);

				if (entry.isDirectory() == false) {
					copySourceFile(jarArchive, entry);
				}
			}

		} finally {
			if (zis != null) {
				zis.close();
			}
		}

		copyBuildFile();
		executeAnt();
	}

	public void execute() throws ZipException, IOException {

		pl("ProductManager 0.1");
		createSrcZip();
	}

	private void executeAnt() {
		ExecResult res = ExecServices.exec("ant -f gpl-build.xml", workDir,
				this);
		if (res.failed()) {
			throw new IllegalStateException("Build of source package failed!");
		}
	}

	public File getBuildFile() {
		return buildFile;
	}

	public File getDestJavaDir() {
		return destJavaDir;
	}

	public File getJarFile() {
		return jarFile;
	}

	public String getProduct() {
		return product;
	}

	public ArrayList<File> getSrcDirList() {
		return srcDirList;
	}

	public File getWorkDir() {
		return workDir;
	}

	public File getZipFile() {
		return zipFile;
	}

	@Override
	public void info(String text) {
		pl(text);
	}

	/**
	 * @param text
	 */
	private void pl(String format, Object... args) {
		System.out.println(String.format(format, args));
	}

	public void setBuildFile(File buildFile) {
		this.buildFile = buildFile;
	}

	private boolean setCommandLineArgs(String[] args) throws IOException {
		OptionList options = new OptionList(args);

		FileOption optJarsFile = new FileOption("jar-file", "j");
		optJarsFile.isRequired(true);
		optJarsFile.isExisting(true);
		options.add(optJarsFile);

		DirectoryOption optSrcDir = new DirectoryOption("source-directory", "s");
		optSrcDir.isRequired(true);
		optSrcDir.isExisting(true);
		optSrcDir.isUnique(false);
		options.add(optSrcDir);

		DirectoryOption optWorkDir = new DirectoryOption("working-directory",
				"w");
		optWorkDir.isRequired(true);
		options.add(optWorkDir);

		FileOption optBuildFile = new FileOption("build-file", "b");
		optBuildFile.isRequired(true);
		options.add(optBuildFile);

		FileOption optZipFile = new FileOption("zip-file", "z");
		optZipFile.isRequired(true);
		options.add(optZipFile);

		StringOption optProduct = new StringOption("product", "p");
		optProduct.isRequired(true);
		options.add(optProduct);

		if (options.validate(true) == false) {
			logger.error(options.getError());
			return false;
		}

		product = optProduct.getValue();
		buildFile = optBuildFile.getValue();
		jarFile = optJarsFile.getValue();
		workDir = optWorkDir.getValue();
		zipFile = optZipFile.getValue();
		FileServices.delete(workDir);

		destJavaDir = new File(workDir, "java");

		srcDirList = new ArrayList<File>();

		for (File srcPath : optSrcDir.getValues()) {
			srcDirList.add(srcPath);
		}

		return true;
	}

	public void setDestJavaDir(File destJavaDir) {
		this.destJavaDir = destJavaDir;
	}

	public void setJarFile(File jarFile) {
		this.jarFile = jarFile;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public void setSrcDirList(ArrayList<File> srcDirList) {
		this.srcDirList = srcDirList;
	}

	public void setWorkDir(File workDir) {
		this.workDir = workDir;
	}

	public void setZipFile(File zipFile) {
		this.zipFile = zipFile;
	}

}
