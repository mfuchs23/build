package org.dbdoclet.packman;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbdoclet.service.ExecResult;
import org.dbdoclet.service.ExecServices;
import org.dbdoclet.service.FileServices;
import org.dbdoclet.service.ZipServices;

public class TarCreator extends BaseCreator {

	private static Log logger = LogFactory.getLog(TarCreator.class);

	@Override
	public boolean create() throws Exception {

		String name = getName();
		logger.debug("Name: " + name);

		String version = getVersion();
		logger.debug("Version: " + version);

		File archiveFile = getZipArchiveFile();
		logger.debug("Zip archive: " + archiveFile.getPath());

		File destDir = archiveFile.getParentFile();

		if (destDir == null) {
			throw new IllegalStateException(
					"The field destDir may not be null!");
		}

		ZipServices.unzip(archiveFile, destDir);

		String pkgPath = destDir.getPath();
		pkgPath = FileServices.appendPath(pkgPath, name + "-" + version);

		File pkgDir = new File(pkgPath);
		if (pkgDir.exists() == false) {
			throw new FileNotFoundException(pkgPath);
		}

		ExecResult result;

		String[] envp = new String[1];
		envp[0] = "PATH=" + System.getProperty("java.library.path");

		String buildroot = FileServices.appendFileName(
				pkgDir.getCanonicalPath(), "buildroot");

		String[] cmdAnt = { "ant", "-Dinstall.root=" + buildroot, "clean",
				"build", "install" };

		result = ExecServices.exec(cmdAnt, envp, pkgDir, false);

		if (result.failed() == true) {
			logger.fatal(result.getStackTrace());
			return false;
		}

		if (result.getExitCode() != 0) {
			logger.fatal(result.getOutput());
			return false;
		}

		String out = name + "-" + version + ".tar.gz";

		String[] cmdTar = { "tar", "-C", buildroot, "-czf", out };

		result = ExecServices.exec(cmdTar, envp, destDir, false);

		if (result.failed() == true) {
			logger.fatal(result.getStackTrace());
			return false;
		}

		if (result.getExitCode() != 0) {
			logger.fatal(result.getOutput());
			return false;
		}

		return true;
	}
}
