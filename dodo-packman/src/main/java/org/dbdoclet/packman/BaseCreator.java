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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.service.FileServices;
import org.dbdoclet.xiphias.W3cServices;
import org.dbdoclet.xiphias.XPathServices;
import org.dbdoclet.xiphias.XmlServices;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class BaseCreator implements Creator {

	private static Log logger = LogFactory.getLog(BaseCreator.class);

	private File specFile;
	private File buildDir;
	private File kitsDir;
	private String prefix;
	private String user;
	private String group;
	private Document doc;
	private boolean verbose = false;

	@Override
	public abstract boolean create() throws Exception;

	@Override
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	@Override
	public void setSpecFile(File specFile) throws Exception {

		if (specFile == null) {
			throw new IllegalArgumentException(
					"The argument specFile may not be null!");
		}

		this.specFile = specFile;
		doc = XmlServices.parse(specFile, false);
	}

	public File getSpecFile() {
		return specFile;
	}

	@Override
	public void setBuildDir(File buildDir) {
		this.buildDir = buildDir;
	}

	public File getBuildDir() {
		return buildDir;
	}

	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	@Override
	public void setKitsDir(File kitsDir) {
		this.kitsDir = kitsDir;
	}

	public File getKitsDir() {
		return kitsDir;
	}

	@Override
	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	@Override
	public void setGroup(String group) {
		this.group = group;
	}

	public String getGroup() {
		return group;
	}

	public String getApplicationName() {

		if (doc == null) {
			throw new IllegalStateException("The field doc may not be null!");
		}

		Element root = doc.getDocumentElement();

		String name = (String) XPathServices.getValue(root, "/name");

		if (name == null || name.length() == 0) {
			throw new IllegalStateException("The field name may not be null!");
		}

		return name;
	}

	public String getPackageName() {

		if (doc == null) {
			throw new IllegalStateException("The field doc may not be null!");
		}

		Element root = doc.getDocumentElement();

		String name = (String) XPathServices.getValue(root, "/pkgname");

		if (name == null || name.length() == 0) {
			name = getApplicationName();
		}

		return name;
	}

	protected boolean isRelocatable() {

		if (doc == null) {
			throw new IllegalStateException("The field doc may not be null!");
		}

		Element root = doc.getDocumentElement();

		String attr = root.getAttribute("relocatable");

		boolean relocatable = false;

		if (attr != null) {

			if (attr.equalsIgnoreCase("yes") || attr.equalsIgnoreCase("true")
					|| attr.equalsIgnoreCase("on")) {

				relocatable = true;
			}
		}

		return relocatable;
	}

	protected String getName() {

		if (doc == null) {
			throw new IllegalStateException("The field doc may not be null!");
		}

		Element root = doc.getDocumentElement();

		Element elem = W3cServices.getChildElement(root, "name");

		if (elem == null) {
			throw new IllegalStateException(
					"Can't find element package -> name!");
		}

		String name = W3cServices.getText(elem);

		if (name == null || name.length() == 0) {
			throw new IllegalStateException("The field name may not be null!");
		}

		return name;
	}

	protected String getVersion() {

		if (doc == null) {
			throw new IllegalStateException("The field doc may not be null!");
		}

		Element root = doc.getDocumentElement();

		Element elem = W3cServices.getChildElement(root, "version");

		if (elem == null) {
			throw new IllegalStateException(
					"Can't find element package -> version!");
		}

		String version = W3cServices.getText(elem);

		if (version == null || version.trim().length() == 0) {
			throw new IllegalStateException(
					"The field version may not be null!");
		}

		return version.trim();
	}

	protected String getRelease() {

		if (doc == null) {
			throw new IllegalStateException("The field doc may not be null!");
		}

		Element root = doc.getDocumentElement();

		Element elem = W3cServices.getChildElement(root, "release");

		if (elem == null) {
			throw new IllegalStateException(
					"Can't find element package -> release!");
		}

		String release = W3cServices.getText(elem);

		if (release == null || release.trim().length() == 0) {
			throw new IllegalStateException(
					"The field release may not be null!");
		}

		return release.trim();
	}

	protected List<String> getArchives() {

		if (doc == null) {
			throw new IllegalStateException("The field doc may not be null!");
		}

		Element root = doc.getDocumentElement();

		Element archives = W3cServices.getChildElement(root, "archives");

		if (archives == null) {
			return new ArrayList<String>();
		}

		Element archive;
		Element archiveFile;
		String path;

		ArrayList<String> list = new ArrayList<String>();
		List<?> children = W3cServices.getChildElements(archives, "archive");

		Iterator<?> iterator = children.iterator();

		while (iterator.hasNext()) {

			archive = (Element) iterator.next();

			archiveFile = W3cServices.getChildElement(archive, "archive-file");

			if (archiveFile != null) {

				path = W3cServices.getText(archiveFile);

				if (path == null || path.length() == 0) {
					throw new IllegalStateException(
							"The tag archive-file may not be empty!");
				}

				list.add(path);
			}
		}

		return list;
	}

	protected File getZipArchiveFile() throws FileNotFoundException {

		String fileName = FileServices.appendPath(getBuildDir(), "archives");
		fileName = FileServices.appendFileName(fileName, getPackageName() + "-"
				+ getVersion() + ".zip");

		File file = new File(fileName);

		if (file.exists() == false) {
			String msg = MessageFormat.format(
					Packman.getString("C_WARN_ARCHIVE_FILE_DOES_NOT_EXIST"),
					fileName);
			logger.warn(msg);
		}

		return file;
	}

	protected File getTarArchiveFile() throws FileNotFoundException {

		String fileName = FileServices.appendPath(getBuildDir(), "archives");
		fileName = FileServices.appendFileName(fileName, getPackageName() + "-"
				+ getVersion() + ".tar.gz");

		File file = new File(fileName);

		if (file.exists() == false) {
			String msg = MessageFormat.format(
					Packman.getString("C_WARN_ARCHIVE_FILE_DOES_NOT_EXIST"),
					fileName);
			logger.warn(msg);
		}

		return file;
	}

	protected void copyArchives(File destDir, String os) throws IOException {

		if (destDir == null) {
			throw new IllegalArgumentException(
					"The argument destDir may not be null!");
		}

		List<String> list = getArchives();

		logger.debug("Found " + list.size() + " archive(s).");

		String name;
		String path;
		String msg;

		File file;

		Iterator<String> iterator = list.iterator();

		while (iterator.hasNext()) {

			name = iterator.next();
			path = FileServices.appendFileName(getBuildDir(), "archives");
			path = FileServices.appendFileName(path, name);
			msg = Packman.getString("C_COPY_ARCHIVE");

			file = new File(path);

			logger.debug("Looking for archive " + file.getAbsolutePath()
					+ "...");

			if (file.exists() == true) {

				msg = MessageFormat.format(msg, path, destDir.getPath());
				logger.info(msg);
				FileServices.copyFileToDir(file, destDir);
				continue;
			}

			path = FileServices.appendFileName(getKitsDir(), os);
			path = FileServices.appendFileName(path, "Java");
			path = FileServices.appendFileName(path, name);

			file = new File(path);

			logger.debug("Looking for archive " + file.getAbsolutePath()
					+ "...");

			if (file.exists() == true) {

				msg = MessageFormat.format(msg, path, destDir.getPath());
				logger.info(msg);
				FileServices.copyFileToDir(file, destDir);
				continue;
			}

			path = FileServices.appendFileName(getKitsDir(), os);
			path = FileServices.appendFileName(path, "i386");
			path = FileServices.appendFileName(path, name);

			file = new File(path);

			logger.debug("Looking for archive " + file.getAbsolutePath()
					+ "...");

			if (file.exists() == true) {

				msg = MessageFormat.format(msg, path, destDir.getPath());
				logger.info(msg);
				FileServices.copyFileToDir(file, destDir);
				continue;
			}
		}
	}

	protected String getResourceAsFile(String name)
			throws FileNotFoundException {

		if (name == null) {
			throw new IllegalArgumentException(
					"The argument name may not be null!");
		}

		logger.debug("Looking for resource '" + name + "'.");

		ClassLoader loader = this.getClass().getClassLoader();
		URL url = loader.getResource(name);

		if (url == null) {
			throw new FileNotFoundException("Resource: " + name);
		}

		return url.toString();
	}

	protected void log(String msg) {

		if (verbose == true) {
			println(msg);
		}
	}

	protected static final void println(String msg) {

		System.out.println(msg);
	}
}
