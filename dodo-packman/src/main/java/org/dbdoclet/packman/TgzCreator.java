/* 
 * $Id$
 *
 * ### Copyright (C) 2005 Michael Fuchs ###
 * ### All Rights Reserved.             ###
 *
 * Author: Michael Fuchs
 * E-Mail: michael.fuchs@unico-group.com
 * URL:    http://www.michael-a-fuchs.de
 *
 * RCS Information
 * Author..........: $Author$
 * Date............: $Date$
 * Revision........: $Revision$
 * State...........: $State$
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
import org.dbdoclet.xiphias.XmlServices;
import org.xml.sax.SAXException;

public class TgzCreator extends BaseCreator {

	private static Log logger = LogFactory.getLog(TgzCreator.class);

	@Override
	public boolean create() throws Exception {

		String path;
		String buffer;
		File file;

		String name = getName();
		logger.debug("Name: " + name);

		String version = getVersion();
		logger.debug("Version: " + version);

		String release = getRelease();
		logger.info("Release: " + release);

		log("Creating tar.gz Package for " + name + "-" + version + "-"
				+ release + "...");

		File archiveFile = getZipArchiveFile();
		logger.debug("Zip archive: " + archiveFile.getPath());

		File destDir = getBuildDir();

		if (destDir == null) {
			throw new IllegalStateException(
					"The field destDir may not be null!");
		}

		String tgzPath = FileServices.appendPath(destDir, "tgz");
		FileServices.delete(tgzPath);
		FileServices.createPath(tgzPath);
		FileServices.delete(destDir, "^" + getPackageName() + "-.*\\.tar\\.gz");

		String buildPath = FileServices.appendPath(tgzPath, "build");
		FileServices.createPath(buildPath);

		copyArchives(new File(tgzPath), "Linux");

		HashMap<String, String> params = new HashMap<String, String>();

		String buildRoot = FileServices.appendPath(tgzPath, "root");
		FileServices.delete(buildRoot);

		params.put("buildroot", buildRoot);

		xslt("build", params, FileServices.appendFileName(tgzPath, "build.sh"));
		xslt("install", params,
				FileServices.appendFileName(tgzPath, "install.sh"));

		String[] envp = new String[1];
		envp[0] = "PATH=" + System.getProperty("java.library.path");

		ExecResult result;

		String[] cmdBuild = { FileServices.appendFileName(tgzPath, "build.sh") };

		result = ExecServices.exec(cmdBuild, tgzPath);

		if (result.failed() == true) {
			logger.fatal("FAILED " + StringServices.arrayToString(cmdBuild)
					+ ":\n" + result.getOutput() + ". "
					+ result.getStackTrace());
			return false;
		}

		if (result.getExitCode() != 0) {
			logger.fatal("ERROR " + StringServices.arrayToString(cmdBuild)
					+ ":\n" + result.getOutput());
			return false;
		}

		String[] cmdInstall = { FileServices.appendFileName(tgzPath,
				"install.sh") };

		result = ExecServices.exec(cmdInstall, tgzPath);

		if (result.failed() == true) {
			logger.fatal("FAILED " + StringServices.arrayToString(cmdInstall)
					+ ":\n" + result.getOutput() + "\n"
					+ result.getStackTrace());
			return false;
		}

		if (result.getExitCode() != 0) {
			logger.fatal("ERROR " + StringServices.arrayToString(cmdInstall)
					+ ":\n" + result.getOutput());
			return false;
		}

		buffer = name + "-" + getVersion() + "-" + getRelease() + ".tar.gz";
		buffer = FileServices.appendFileName(getBuildDir(), buffer);

		path = FileServices.appendPath("root", getPrefix());

		String[] cmdTar = { "tar", "-C", path, "-czf", buffer, "." };

		logger.info("Executing tar: " + StringServices.arrayToString(cmdTar)
				+ "...");

		result = ExecServices.exec(cmdTar, tgzPath);

		if (result.failed() == true) {
			logger.fatal("FAILED " + StringServices.arrayToString(cmdTar)
					+ ":\n" + result.getOutput() + "\n"
					+ result.getStackTrace());
			return false;
		}

		if (result.getExitCode() != 0) {
			logger.fatal("ERROR " + StringServices.arrayToString(cmdTar)
					+ ":\n" + result.getOutput());
			return false;
		}

		buffer = name + "-src-" + getVersion() + "-" + getRelease() + ".tar.gz";
		buffer = FileServices.appendFileName(getBuildDir(), buffer);

		path = FileServices.appendPath("/", getPrefix());
		path = FileServices.appendPath(path, name);

		file = new File(FileServices.appendPath(path, "src"));

		if (file.exists() == true) {

			String[] cmdSrcTar = { "tar", "-C", path, "-czf", buffer, "src" };

			logger.info("Executing tar: "
					+ StringServices.arrayToString(cmdSrcTar) + "...");

			result = ExecServices.exec(cmdSrcTar, tgzPath);

			if (result.failed() == true) {
				logger.fatal("FAILED "
						+ StringServices.arrayToString(cmdSrcTar) + ":\n"
						+ result.getOutput() + "\n" + result.getStackTrace());
				return false;
			}

			if (result.getExitCode() != 0) {
				logger.fatal("ERROR " + StringServices.arrayToString(cmdSrcTar)
						+ ":\n" + result.getOutput());
				return false;
			}
		}

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

		String xsl = getResourceAsFile("xslt/tgz/" + name + ".xsl");

		XmlServices.xslt(specFile.getPath(), xsl, params, out);

		if (out.endsWith("build.sh") || out.endsWith("install.sh")) {

			String[] cmd = { "chmod", "755", out };
			ExecResult result = ExecServices.exec(cmd, ".");

			if (result.failed()) {
				System.err.println(result.getOutput());
			}

		}
	}
}
/*
 * $Log$
 */
