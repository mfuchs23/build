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

public class NsisCreator extends BaseCreator {

	private static Log logger = LogFactory.getLog(NsisCreator.class);

	@Override
	public boolean create() throws Exception {

		String path;
		String buffer;
		File file;

		logger.info("+++ Running NsisCreator");

		String name = getName();
		logger.info("Name: " + name);

		String version = getVersion();
		logger.info("Version: " + version);

		String release = getRelease();
		logger.info("Release: " + release);

		log("Creating Windows Package for " + name + "-" + version + "-"
				+ release + "...");

		File archiveFile = getZipArchiveFile();
		logger.debug("Zip archive: " + archiveFile.getPath());

		File destDir = getBuildDir();

		if (destDir == null) {
			throw new IllegalStateException(
					"The field destDir may not be null!");
		}

		String nsisPath = FileServices.appendPath(destDir, "nsis");
		FileServices.delete(nsisPath);
		FileServices.createPath(nsisPath);
		FileServices.delete(destDir, "^" + getPackageName() + "-.*\\.exe");

		String buildPath = FileServices.appendPath(nsisPath, "build");
		FileServices.createPath(buildPath);

		copyArchives(new File(nsisPath), "Windows");

		File nsiFile = getNsiFile();

		log("Looking for nsis file " + nsiFile.getCanonicalPath() + "...");

		HashMap<String, String> params = new HashMap<String, String>();

		String buildRoot = FileServices.appendPath(nsisPath, "root");
		FileServices.delete(buildRoot);

		params.put("buildroot", buildRoot);

		if (nsiFile.exists() == false) {

			log("Can't find nsis file. Creating a default nsis file...");
			xslt("nsis", params, nsiFile.getAbsolutePath());
		}

		path = FileServices.appendFileName(nsisPath, nsiFile.getName());
		file = new File(path);

		buffer = FileServices.readToString(nsiFile, "UTF-8");
		String outFile = name + "-"
				+ StringServices.replace(getVersion(), ".", "_") + "-"
				+ getRelease() + ".exe";
		buffer = StringServices.replace(buffer, "OutFile @OutFile@",
				"OutFile \"..\\" + outFile + "\"");
		FileServices.writeFromString(file, buffer, "iso-8859-1");

		xslt("build", params, FileServices.appendFileName(nsisPath, "build.sh"));
		xslt("install", params,
				FileServices.appendFileName(nsisPath, "install.sh"));

		String[] envp = new String[1];
		envp[0] = "PATH=" + System.getProperty("java.library.path");

		ExecResult result;

		String[] cmdBuild = { FileServices.appendFileName(nsisPath, "build.sh") };

		result = ExecServices.exec(cmdBuild, nsisPath);

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

		String[] cmdInstall = { FileServices.appendFileName(nsisPath,
				"install.sh") };

		result = ExecServices.exec(cmdInstall, nsisPath);

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

		// String[] cmdMakensis = { "wine", "c:/Programme/NSIS/makensis.exe",
		// name + ".nsi" };

		String[] cmdMakensis = { "makensis", name + ".nsi" };

		result = ExecServices.exec(cmdMakensis, nsisPath);

		if (result.failed() == true) {
			logger.fatal("FAILED " + StringServices.arrayToString(cmdMakensis)
					+ ":\n" + result.getOutput() + "\n"
					+ result.getStackTrace());
			return false;
		}

		if (result.getExitCode() != 0) {
			logger.fatal("ERROR " + StringServices.arrayToString(cmdMakensis)
					+ ":\n" + result.getOutput());
			return false;
		}

		return true;
	}

	private File getNsiFile() throws IOException {

		File specFile = getSpecFile();

		String specFileName = specFile.getCanonicalPath();
		String nsiFileName = StringServices.replace(specFileName, ".xml",
				".nsi");

		File parentDir;
		File nsiFile = new File(nsiFileName);

		if (nsiFile.exists() == false) {

			parentDir = specFile.getParentFile();

			if (parentDir != null) {
				nsiFileName = parentDir.getCanonicalPath();
			} else {
				nsiFileName = "";
			}

			nsiFileName = FileServices.appendFileName(nsiFileName, getName()
					+ ".nsi");
			nsiFile = new File(nsiFileName);
		}

		return nsiFile;
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

		String xsl = getResourceAsFile("xslt/nsis/" + name + ".xsl");

		XmlServices.xslt(specFile.getPath(), xsl, params, out);

		if (out.endsWith("build.sh") || out.endsWith("install.sh")) {

			String[] cmd = { "chmod", "755", out };
			ExecServices.exec(cmd, ".");
		}
	}
}
/*
 * $Log$
 */
