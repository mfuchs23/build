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
package org.dbdoclet.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.dbdoclet.packman.Packman;

public class PackmanTask extends Task {

	private File specFile;
	private File destDir;
	private String type = "auto";
	private boolean verbose = false;

	@Override
	public void execute() throws BuildException {

		try {

			Project project = getProject();
			Location location = getLocation();

			if (specFile == null) {
				throw new BuildException("The attribute spec must be set!",
						location);
			}

			if (specFile.exists() == false) {
				throw new BuildException("The spec file "
						+ specFile.getAbsolutePath() + " doesn't exist!",
						location);
			}

			if (destDir == null) {
				throw new BuildException("The attribute destdir must be set!",
						location);
			}

			if (type == null) {
				throw new BuildException("The attribute type must be set!",
						location);
			}

			String user = getProperty("project.user");
			String group = getProperty("project.group");
			String prefix = getProperty("project.prefix");

			Packman packman = new Packman();
			packman.setVerbose(verbose);
			packman.start(specFile, destDir, prefix,
					new File(project.getProperty("kits.dir")), user, group,
					type);

		} catch (BuildException oops) {

			throw oops;

		} catch (Throwable oops) {
			throw new BuildException(oops);
		}
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void setDestDir(String path) {

		Project project = getProject();
		destDir = project.resolveFile(path);
	}

	public void setSpec(String path) {

		Project project = getProject();
		specFile = project.resolveFile(path);
	}

	public void setType(String type) {
		this.type = type;
	}

	private String getProperty(String name) throws BuildException {

		return getProperty(name, true);
	}

	private String getProperty(String name, boolean mandatory)
			throws BuildException {

		if (name == null) {
			throw new IllegalArgumentException(
					"The argument name may not be null!");
		}

		Project project = getProject();
		String prop = project.getProperty(name);

		if (prop == null && mandatory == true) {
			throw new BuildException("Property " + name + " is undefined!");
		}

		return prop;
	}
}
