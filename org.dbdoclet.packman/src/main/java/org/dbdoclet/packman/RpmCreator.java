/* 
 * $Id$
 *
 * ### Copyright (C) 2006 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 */
package org.dbdoclet.packman;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.Sfv;
import org.dbdoclet.service.ExecResult;
import org.dbdoclet.service.ExecServices;
import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.FindServices;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.xiphias.XmlServices;
import org.xml.sax.SAXException;

public class RpmCreator extends BaseCreator {

	private static Log logger = LogFactory.getLog(RpmCreator.class);

	@Override
	public boolean create() throws Exception {

		String path;

		logger.info("+++ Running RpmCreator");

		String name = getName();
		logger.info("Name: " + name);

		String version = getVersion();
		version = version.replace("-", "_");
		logger.info("Version: " + version);

		String release = getRelease();
		logger.info("Release: " + release);

		log("Creating RPM Package for " + name + "-" + version + "-" + release
				+ "...");

		File archiveFile = getTarArchiveFile();
		logger.info("Tar archive: " + archiveFile.getPath());
		
		File destDir = getBuildDir();

		if (destDir == null) {
			throw new IllegalStateException(
					"The field destDir may not be null!");
		}

		String rpmPath = FileServices.appendPath(destDir, "rpm");
		File rpmDir = new File(rpmPath);

		FileServices.delete(rpmDir);
		FileServices.delete(destDir, "^" + getPackageName() + "-.*\\.rpm");

		path = FileServices.appendPath(rpmDir, "BUILD");
		FileServices.createPath(path);

		path = FileServices.appendPath(rpmDir, "RPMS");
		FileServices.createPath(path);

		path = FileServices.appendPath(rpmDir, "SRPMS");
		FileServices.createPath(path);

		String sourcesPath = FileServices.appendPath(rpmDir, "SOURCES");
		FileServices.createPath(sourcesPath);

		copyArchives(new File(sourcesPath), "Linux");

		String specsPath = FileServices.appendPath(rpmDir, "SPECS");
		FileServices.createPath(specsPath);

		if (archiveFile.exists()) {
			FileServices.copyFileToDir(archiveFile.getPath(), sourcesPath);
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user", getUser());
		params.put("group", getGroup());

		if (getPrefix() != null) {
			params.put("prefix", getPrefix());
		}

		if (getPackageName() != null) {
			params.put("pkgname", getPackageName());
		}

		if (getApplicationName() != null) {
			params.put("application", getApplicationName());
		}

		if (isRelocatable()) {

			path = FileServices.appendPath(rpmDir, "BUILDROOT");
			path = FileServices.appendPath(path, getPackageName() + "-"
					+ version + "-" + getRelease() + ".x86_64");
			params.put("buildroot", path);

		} else {

			params.put("buildroot", "/");
		}

		String out = FileServices.appendFileName(specsPath, name + ".spec");
		xslt("spec", params, out);

		String buildProperties = FileServices.appendPath(rpmDir, "..");
		buildProperties = FileServices.appendFileName(buildProperties,
				"build.properties");

		FileServices.copyFileToDir(buildProperties, rpmDir.getPath());

		ExecResult result;

		String username = System.getProperty("user.name");

		String javaHome = "/usr/local/java/jdk-"
				+ System.getProperty("java.specification.version");
		String antHome = "/usr/local/java/ant";

		String[] envp = new String[5];
		envp[0] = "LOGNAME=" + username;
		envp[1] = "HOME=" + System.getProperty("user.home");
		envp[2] = "JAVA_HOME=" + javaHome;
		envp[3] = "ANT_HOME=" + antHome;
		envp[4] = "PATH=" + javaHome + "/bin:" + antHome + "/bin:/bin:/usr/bin";

		String[] cmd = { "rpmbuild", "--define",
				"_topdir " + rpmDir.getCanonicalPath(), "-bb",
				"SPECS/" + name + ".spec" };
		log(StringServices.arrayToString(cmd));

		result = ExecServices.exec(cmd, envp, rpmDir, false);

		if (result.failed() == true) {
			logger.fatal("ExitCode: " + result.getExitCode() + Sfv.LSEP
					+ "Output: " + result.getOutput() + Sfv.LSEP
					+ "StackTrace: " + result.getStackTrace());
			return false;
		}

		if (result.getExitCode() != 0) {
			logger.fatal(result.getOutput());
			return false;
		}

		path = FileServices.appendPath(rpmDir, "RPMS");
		ArrayList<File> fileList = new ArrayList<File>();
		FindServices.findFile(new File(path), "^.*\\.rpm$", fileList);

		fileList.forEach(file -> { 
			try {
				FileServices.copyFileToDir(file, getBuildDir());
			} catch (IOException oops) {
				logger.error("copyFileToDir", oops);
			}
		});
			
		return true;
	}

	private void xslt(String name, HashMap<String, String> params, String out)
			throws IOException, SAXException,
			TransformerConfigurationException, TransformerException {

		if (name == null) {
			throw new IllegalArgumentException(
					"The argument name may not be null!");
		}

		if (params == null) {
			throw new IllegalArgumentException(
					"The argument params may not be null!");
		}

		if (out == null) {
			throw new IllegalArgumentException(
					"The argument out may not be null!");
		}

		File specFile = getSpecFile();

		String xsl = getResourceAsFile("xslt/rpm/" + name + ".xsl");

		XmlServices.xslt(specFile.getPath(), xsl, params, out);

	}

}
