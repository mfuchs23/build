package org.dbdoclet.packman;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.option.BooleanOption;
import org.dbdoclet.option.DirectoryOption;
import org.dbdoclet.option.FileOption;
import org.dbdoclet.option.OptionList;
import org.dbdoclet.option.SelectOption;
import org.dbdoclet.option.StringOption;

public class Packman {

	private static Log logger = LogFactory.getLog(Packman.class);
	private static ResourceBundle res = PropertyResourceBundle
			.getBundle("org.dbdoclet.packman.Resources");
	private boolean verbose = false;

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void start(File specFile, File buildDir, String prefix,
			File kitsDir, String user, String group, String type)

	throws Exception {

		if (specFile == null) {
			throw new IllegalArgumentException(
					"The argument specFile may not be null!");
		}

		if (buildDir == null) {
			throw new IllegalArgumentException(
					"The argument buildDir may not be null!");
		}

		if (user == null) {
			throw new IllegalArgumentException(
					"The argument user may not be null!");
		}

		if (group == null) {
			throw new IllegalArgumentException(
					"The argument group may not be null!");
		}

		if (type == null) {
			throw new IllegalArgumentException(
					"The argument type may not be null!");
		}

		if (type.equalsIgnoreCase("auto")) {
			type = getType();
		}

		if (type == null) {
			throw new IllegalStateException("Can't guess packet type!");
		}

		PrintWriter logWriter = null;

		try {

			pl("Spec File: " + specFile);
			logWriter = new PrintWriter(new FileWriter("packman.log"));

			System.setProperty("xml.catalog.ignoreMissing", "yes");

			logger.info("Operating System: " + System.getProperty("os.name"));
			logger.info("Spec File       : " + specFile.getAbsolutePath());
			logger.info("Packet type     : " + type);
			logger.info("Prefix          : " + prefix);

			Creator creator = null;

			if (type.equals("deb")) {
				creator = new DebCreator();
			}

			if (type.equals("rpm")) {
				creator = new RpmCreator();
			}

			if (type.equals("nsis")) {
				creator = new NsisCreator();
			}

			if (type.equals("tgz")) {
				creator = new TgzCreator();
			}

			if (creator == null) {
				throw new IllegalStateException("Can't get creator object!");
			}

			creator.setVerbose(verbose);
			creator.setSpecFile(specFile);
			creator.setBuildDir(buildDir);
			creator.setPrefix(prefix);
			creator.setKitsDir(kitsDir);
			creator.setUser(user);
			creator.setGroup(group);

			if (creator.create() == false) {
				throw new Exception("BUILD FAILED");
			}

		} finally {

			if (logWriter != null) {
				logWriter.close();
			}
		}
	}

	private void pl(String msg) {
		if (verbose == true) {
			System.out.println(msg);
		}
	}

	private String getType() {

		String os = System.getProperty("os.name");
		String type = "rpm";

		if (os != null && os.equals("Linux")) {

			File debianFile = new File("/etc/debian_version");
			if (debianFile.exists()) {
				type = "deb";
			}
		}

		if (os != null && os.startsWith("Windows")) {
			type = "nsis";
		}

		return type;
	}

	public static String getString(String key) {

		if (key == null) {
			throw new IllegalArgumentException(
					"The argument key may not be null!");
		}

		String str;

		try {

			str = res.getString(key);

		} catch (MissingResourceException oops) {

			str = key;
		}

		return str;
	}

	public static void usage() {
		usage("");
	}

	public static void usage(String msg) {

		System.out.println("Packman 2.0");
		System.out
				.println("Copyright (c) 2004 Michael Fuchs. All Rights Reserved.");
		System.out.println();
		System.out.println("Syntax: packman");
		System.out.println(" -s, --spec-file <FILE>           The spec file.");
		System.out
				.println(" -b, --build-dir <DIRECTORY>      The build directory.");
	}

	public static void main(String[] args) {

		try {

			OptionList options = new OptionList(args);

			BooleanOption optHelp = new BooleanOption("h", "help");
			options.add(optHelp);

			FileOption optSpec = new FileOption("spec-file", "s");
			optSpec.isRequired(true);
			optSpec.isExisting(true);
			options.add(optSpec);

			DirectoryOption optBuildDir = new DirectoryOption("build-dir", "b");
			optBuildDir.isRequired(true);
			options.add(optBuildDir);

			StringOption optPrefix = new StringOption("prefix", "p");
			optPrefix.setDefault("");
			options.add(optPrefix);

			DirectoryOption optKitsDir = new DirectoryOption("kits-dir", "k");
			optKitsDir.setDefault(new File("."));
			options.add(optKitsDir);

			StringOption optUser = new StringOption("user", "u");
			optUser.setDefault("root");
			options.add(optUser);

			StringOption optGroup = new StringOption("group", "g");
			optGroup.setDefault("root");
			options.add(optGroup);

			SelectOption optType = new SelectOption("type", "t");
			optType.setDefault("auto");
			String[] typeList = { "auto", "deb", "nsis", "rpm", "tgz" };
			optType.setList(typeList);
			options.add(optType);

			StringOption optPkgName = new StringOption("package-name", "p");
			options.add(optPkgName);

			StringOption optAppName = new StringOption("application-name", "a");
			options.add(optAppName);

			boolean valid = options.validate();

			if (optHelp.getValue() == true) {
				usage();
			}

			if (valid == false) {
				System.out.println(options.getError());
				System.exit(255);
			}

			String prefix = optPrefix.getValue();

			if (optPrefix.isUnset()) {
				prefix = null;
			}

			File kitsDir = optKitsDir.getValue();

			if (optKitsDir.isUnset()) {
				kitsDir = null;
			}

			Packman app = new Packman();
			app.start(optSpec.getValue(), optBuildDir.getValue(), prefix,
					kitsDir, optUser.getValue(), optGroup.getValue(),
					optType.getValue());

		} catch (Exception oops) {
			oops.printStackTrace();
		}

	}
}
