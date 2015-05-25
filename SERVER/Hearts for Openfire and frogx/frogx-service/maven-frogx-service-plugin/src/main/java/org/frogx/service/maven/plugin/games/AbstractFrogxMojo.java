/**
 * Copyright (C) 2009 Guenther Niess. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.frogx.service.maven.plugin.games;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

public abstract class AbstractFrogxMojo extends AbstractMojo {
	
	static final public String frogxPluginExtensionArtifactId = "frogx-plugin-extension";
	static final public String frogxPluginExtensionGroupId = "org.frogx.service";
	
	/**
	 * The maven project.
	 *
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;
	
	/**
	 * The directory containing generated classes.
	 *
	 * @parameter expression="${project.build.outputDirectory}"
	 * @required
	 * @readonly
	 */
	private File classesDirectory;
	
	/**
	 * The directory for the generated JAR.
	 *
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private String outputDirectory;
	
	/**
	 * Single directory for frogx plugin files like <tt>game.xml</tt>,
	 * <tt>changelog.html</tt> and <tt>readme.html</tt>.
	 *
	 * @parameter expression="${frogx.resource.dir}" default-value="${basedir}/src/main/frogx"
	 * @required
	 */
	private File frogxSourceDirectory;
	
	/**
	 * The directory where the frogx plugin is built.
	 *
	 * @parameter expression="${frogx.build.dir}" default-value="${project.build.directory}/${project.build.finalName}"
	 * @required
	 */
	private File frogxPluginDirectory;
	
	/**
	 * The directory where the generated frogx resources are built.
	 *
	 * @parameter expression="${frogx.build.resource.dir}" default-value="${project.build.directory}/generated-resources/frogx-service"
	 * @required
	 */
	private File generatedFrogxResourcesDirectory;
	
	/**
	 * Add frogx-plugin-extension to support Openfire server.
	 * 
	 * @parameter expression="${frogx.openfire.support}" default-value=true
	 */
	private boolean openfireSupport;
	
	/** 
	 * @parameter expression="${plugin.artifacts}"
	 */
	@SuppressWarnings("unchecked")
	private List pluginArtifacts;
	
	public static final String frogxPluginDescriptor = "frogx.xml";
	
	
	public MavenProject getProject() {
		return project;
	}
	
	public File getClassesDirectory() {
		return classesDirectory;
	}
	
	public String getOutputDirectory() {
		return outputDirectory;
	}
	
	public File getFrogxSourceDirectory() {
		return frogxSourceDirectory;
	}
	
	public File getFrogxPluginDirectory() {
		return frogxPluginDirectory;
	}
	
	public File getGeneratedFrogxResourcesDirectory() {
		return generatedFrogxResourcesDirectory;
	}
	
	public String getRelativePath(File file) {
		String filepath = file.getAbsolutePath();
		filepath = filepath.replace(project.getBasedir().getAbsolutePath(), "");
		return filepath.substring(1, filepath.length());
	}
	
	public boolean supportOpenfire() {
		return openfireSupport;
	}
	
	@SuppressWarnings("unchecked")
	public Artifact getFrogxPluginExtension() {
		// Check project for plugin extension
		for (Artifact artifact : (Set<Artifact>) project.getArtifacts()) {
			if (frogxPluginExtensionArtifactId.equals(artifact.getArtifactId()) &&
					frogxPluginExtensionGroupId.equals(artifact.getGroupId())) {
				return artifact;
			}
		}
		// Check this maven plugin for frogx plugin extension
		for (Iterator<Artifact> it = pluginArtifacts.iterator(); it.hasNext();) {
			Artifact artifact = it.next();
			if (frogxPluginExtensionArtifactId.equals(artifact.getArtifactId()) &&
					frogxPluginExtensionGroupId.equals(artifact.getGroupId())) {
				return artifact;
			}
		}
		return null;
	}
}
