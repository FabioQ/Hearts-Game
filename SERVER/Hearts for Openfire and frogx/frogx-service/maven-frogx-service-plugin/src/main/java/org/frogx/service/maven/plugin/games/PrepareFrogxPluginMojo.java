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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.ReaderFactory;

/**
 * Prepare the frogx game plugin directory.
 * 
 * @author G&uuml;nther Nie&szlig;
 * @goal prepare-frogx-service-plugin
 * @phase prepare-package
 * @requiresDependencyResolution runtime
 */
public class PrepareFrogxPluginMojo extends AbstractFrogxMojo {
	
	/**
	 * The character encoding scheme to be applied when filtering resources.
	 *
	 * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
	 */
	protected String encoding;
	
	/**
	 * The list of additional key-value pairs aside from that of the System,
	 * and that of the project, which would be used for the filtering.
	 *
	 * @parameter expression="${project.build.filters}"
	 */
	@SuppressWarnings("unchecked")
	protected List filters;
	
	/**
	 * Expression preceded with the String won't be interpolated 
	 * \${foo} will be replaced with ${foo}
	 * @parameter expression="${maven.resources.escapeString}"
	 */
	protected String escapeString;
	
	/**
	 * Overwrite existing files even if the destination files are newer.
	 * @parameter expression="${maven.resources.overwrite}" default-value="false"
	 */
	private boolean overwrite;
	
	/**
	 * Copy any empty directories included in the Ressources.
	 * @parameter expression="${maven.resources.includeEmptyDirs}" default-value="false"
	 */
	protected boolean includeEmptyDirs;
	
	/**
	 * @parameter expression="${session}"
	 * @readonly
	 * @required
	 */
	protected MavenSession session;
	
	/**
	 * 
	 * @component role="org.apache.maven.shared.filtering.MavenResourcesFiltering" role-hint="default"
	 * @required
	 */
	protected MavenResourcesFiltering mavenResourcesFiltering;
	
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		File frogxPluginDir = getFrogxPluginDirectory();
		if (!frogxPluginDir.exists()) {
			if (!frogxPluginDir.mkdirs()) {
				throw new MojoExecutionException("Can't create frogx plugin directory!");
			}
		}
		getLog().info("Preparing classes directory.");
		prepareClassesDirectory();
		getLog().info("Preparing frogx resources.");
		prepareFrogxResources();
		getLog().info("Preparing dependencies.");
		preparePluginLibs();
	}
	
	public void prepareClassesDirectory() throws MojoExecutionException {
		if (getClassesDirectory().exists()) {
			try {
				FileUtils.copyDirectoryStructureIfModified(
						getClassesDirectory(),
						new File(getFrogxPluginDirectory(), "classes"));
			}
			catch (IOException e) {
				throw new MojoExecutionException(
						"Can't copy classes directory to plugin folder.", e);
			}
		}
	}
	
	public void prepareFrogxResources() throws MojoExecutionException {
		try {
			Resource resource = new Resource();
			resource.setDirectory(getFrogxSourceDirectory().getAbsolutePath());
			resource.setFiltering(true);
			resource.setTargetPath(getFrogxPluginDirectory().getAbsolutePath());
			List<Resource> resourceList = new ArrayList<Resource>();
			resourceList.add(resource);
			
			if (StringUtils.isEmpty(encoding)) {
				getLog().warn(
						"File encoding has not been set, using platform encoding " + ReaderFactory.FILE_ENCODING
						+ ", i.e. build is platform dependent!" );
			}
			
			MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution(
					resourceList, getFrogxPluginDirectory(), getProject(), encoding,
					filters, Collections.EMPTY_LIST, session);
			mavenResourcesExecution.setEscapeString(escapeString);
			mavenResourcesExecution.setOverwrite(overwrite);
			mavenResourcesExecution.setIncludeEmptyDirs(includeEmptyDirs);
			mavenResourcesFiltering.filterResources( mavenResourcesExecution );
		}
		catch (MavenFilteringException e) {
			throw new MojoExecutionException("Error while preparing frogx resources: " 
					+ getFrogxSourceDirectory(), e);
		}
		
		if (getGeneratedFrogxResourcesDirectory().exists()) {
			try {
				FileUtils.copyDirectoryStructure(
						getGeneratedFrogxResourcesDirectory(), 
						getFrogxPluginDirectory());
			}
			catch (IOException e) {
				throw new MojoExecutionException(
						"Can't copy generated frogx resources to plugin folder.", e);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void preparePluginLibs() throws MojoExecutionException {
		File libDirectory = new File(getFrogxPluginDirectory(), "lib");
		if (!libDirectory.exists()) {
			if (!libDirectory.mkdirs()) {
				throw new MojoExecutionException("Can't create lib directory!");
			}
		}
		
		Set<Artifact> artifacts = getProject().getArtifacts();
		try {
			for (Artifact artifact : artifacts) {
				ScopeArtifactFilter filter = new ScopeArtifactFilter(Artifact.SCOPE_RUNTIME);
				if (!artifact.isOptional() && filter.include(artifact)) {
					String type = artifact.getType();
					if ("jar".equals(type) || "pack".equals(type)) {
						FileUtils.copyFileIfModified(
								artifact.getFile(),
								new File(libDirectory, artifact.getFile().getName()));
					}
					else {
						getLog().debug("Skipping artifact of type " + type + " for lib.");
					}
				}
			}
			Artifact pluginExtension = getFrogxPluginExtension();
			if (pluginExtension != null) {
				FileUtils.copyFileIfModified(
						pluginExtension.getFile(),
						new File(libDirectory, pluginExtension.getFile().getName()));
			}
		}
		catch (IOException e) {
			throw new MojoExecutionException("Error while preparing libaries.", e);
		}
	}
}
