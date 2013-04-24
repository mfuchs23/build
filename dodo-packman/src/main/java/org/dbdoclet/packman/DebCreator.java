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
import java.util.HashMap;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.service.ExecResult;
import org.dbdoclet.service.ExecServices;
import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.StringServices;
import org.dbdoclet.xiphias.XPathServices;
import org.dbdoclet.xiphias.XmlServices;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class DebCreator extends BaseCreator {

	private static Log logger = LogFactory.getLog(DebCreator.class);

	@Override
	public boolean create() throws Exception {

		logger.info("DebCreator.create");

		File specFile = getSpecFile();

		String name = getName();
		logger.info("Name: " + name);

		String version = getVersion();
		logger.info("Version: " + version);

		String release = getRelease();
		logger.info("Release: " + release);

		log("Creating Debian Package for " + name + "-" + version + "-"
				+ release + "...");

		File tarFile = getTarArchiveFile();
		logger.info("Tar archive: " + tarFile.getPath());

		File archiveFile = getZipArchiveFile();
		logger.info("Zip archive: " + archiveFile.getPath());

		File destDir = getBuildDir();

		if (destDir == null) {
			throw new IllegalStateException(
					"The field destDir may not be null!");
		}

		String debPath = FileServices.appendPath(destDir, "deb");
		FileServices.delete(debPath);
		FileServices.createPath(debPath);
		FileServices.delete(destDir, "^" + getPackageName() + "_.*\\.deb");
		FileServices.delete(destDir, "^" + getPackageName() + "_.*\\.changes");

		copyArchives(new File(debPath), "Linux");

		File pkgDir = new File(debPath);

		String debianPath = FileServices.appendPath(debPath, "debian");

		logger.debug("Debian directory: " + debianPath);

		Document doc = XmlServices.parse(specFile, false);
		Object obj = XPathServices.getNode(doc, "/package/menu");

		boolean hasMenu = false;

		if (obj != null) {
			hasMenu = true;
		}

		FileServices.createPath(debianPath);

		xslt("build", debPath);
		xslt("changelog", debianPath);
		xslt("compat", debianPath);
		xslt("control", debianPath);
		xslt("copyright", debianPath);
		xslt("install", debPath);
		xslt("postinst", debianPath);
		xslt("postrm", debianPath);
		xslt("preinst", debianPath);
		xslt("prepare", debPath);
		xslt("prerm", debianPath);
		xslt("rules", debianPath);

		if (hasMenu == true) {
			xslt("desktop", debianPath);
		}

		if (hasMenu == true) {
			xslt("menu", debianPath);
		}

		String buildProperties = FileServices.appendPath(getBuildDir(), ".");
		buildProperties = FileServices.appendFileName(buildProperties,
				"build.properties");

		FileServices.copyFileToDir(buildProperties, destDir.getPath());

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

		logger.info("Working directory: " + pkgDir);

		String[] cmd = { "dpkg-buildpackage", "-us", "-uc", "-b", "-rfakeroot" };
		log(StringServices.arrayToString(cmd));

		result = ExecServices.exec(cmd, envp, pkgDir, false);

		if (result.failed() == true || result.getExitCode() != 0) {

			logger.fatal(result.getOutput() + "\nStackTrace:\n"
					+ result.getStackTrace());
			return false;
		}

		return true;
	}

	private void xslt(String name, String destPath) throws IOException,
			SAXException, TransformerConfigurationException,
			TransformerException {

		if (name == null) {
			throw new IllegalArgumentException(
					"The argument name may not be null!");
		}

		if (destPath == null) {
			throw new IllegalArgumentException(
					"The argument destPath may not be null!");
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

		if (getBuildDir() != null) {

			String buildroot = getBuildDir().getCanonicalPath();
			buildroot = FileServices.appendPath(buildroot, "deb");
			buildroot = FileServices.appendPath(buildroot, "debian");
			buildroot = FileServices.appendPath(buildroot, getPackageName());
			params.put("buildroot", buildroot);
		}

		File specFile = getSpecFile();

		String xsl = getResourceAsFile("xslt/deb/" + name + ".xsl");
		String out = FileServices.appendFileName(destPath, name);

		if (name.equals("menu") == true) {
			out = FileServices.appendFileName(destPath, getPackageName()
					+ ".menu");
		}

		if (name.equals("desktop") == true) {
			out = FileServices.appendFileName(destPath, getPackageName()
					+ ".desktop");
		}

		logger.debug("Debian " + name + ": " + out);

		XmlServices.xslt(specFile.getPath(), xsl, params, out);

		if (name.equals("rules") || name.equals("prepare")
				|| name.equals("build") || name.equals("install")
				|| name.equals("preinst") || name.equals("postinst")) {

			String[] cmd = { "chmod", "755", name };
			ExecServices.exec(cmd, destPath);
		}
	}

}
