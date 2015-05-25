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

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.jar.JarArchiver;

/**
 * Build an Openfire Plugin jar. This Mojo is based on
 * <a href="http://maven.apache.org/plugins/maven-jar-plugin/">Maven 2 Jar Plugin</a>.
 * 
 * @author G&uuml;nther Nie&szlig;
 * @goal frogx-service-plugin
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class FrogxPluginMojo extends AbstractFrogxMojo {
	
	private static final String[] DEFAULT_EXCLUDES = new String[] { "**/package.html" };
	
	private static final String[] DEFAULT_INCLUDES = new String[] { "**/**" };
	
	/**
	 * Classifier to add to the artifact generated. If given, the artifact will be an attachment instead.
	 *
	 * @parameter
	 */
	private String classifier;
	
	/**
	 * List of files to include. Specified as fileset patterns which are relative to the input directory whose contents
	 * is being packaged into the JAR.
	 *
	 * @parameter
	 */
	private String[] includes;
	
	/**
	 * List of files to exclude. Specified as fileset patterns which are relative to the input directory whose contents
	 * is being packaged into the JAR.
	 *
	 * @parameter
	 */
	private String[] excludes;
	
	/**
	 * Name of the generated JAR.
	 *
	 * @parameter alias="jarName" expression="${jar.finalName}" default-value="${project.build.finalName}"
	 * @required
	 */
	private String finalName;
	
	/**
	 * The Jar archiver.
	 *
	 * @component role="org.codehaus.plexus.archiver.Archiver" roleHint="jar"
	 */
	private JarArchiver jarArchiver;
	
	/**
	 * The archive configuration to use.
	 * See <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven Archiver Reference</a>.
	 *
	 * @parameter
	 */
	private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();
	
	/**
	 * Path to the default MANIFEST file to use. It will be used if
	 * <code>useDefaultManifestFile</code> is set to <code>true</code>.
	 *
	 * @parameter expression="${project.build.outputDirectory}/META-INF/MANIFEST.MF"
	 * @required
	 * @readonly
	 */
	private File defaultManifestFile;
	
	/**
	Set this to <code>true</code> to enable the use of the <code>defaultManifestFile</code>.
	 *
	 * @parameter expression="${jar.useDefaultManifestFile}" default-value="false"
	 */
	private boolean useDefaultManifestFile;
	
	/**
	 * @component
	 */
	private MavenProjectHelper projectHelper;
	
	/**
	 * Whether creating the archive should be forced.
	 *
	 * @parameter expression="${jar.forceCreation}" default-value="false"
	 */
	private boolean forceCreation;
	
	protected String getClassifier() {
		return classifier;
	}
	
	protected static File getJarFile( String basedir, String finalName, String classifier ) {
		if ( classifier == null ) {
			classifier = "";
		}
		else if ( classifier.trim().length() > 0 && !classifier.startsWith( "-" ) ) {
			classifier = "-" + classifier;
		}
		
		return new File( basedir, finalName + classifier + ".jar" );
	}
	
	/**
	 * Default Manifest location. Can point to a non existing file.
	 * Cannot return null.
	 */
	protected File getDefaultManifestFile() {
		return defaultManifestFile;
	}
	
	/**
	 * Generates the JAR.
	 */
	public File createArchive() throws MojoExecutionException {
		File jarFile = getJarFile(getOutputDirectory(), finalName, getClassifier());
		
		MavenArchiver archiver = new MavenArchiver();
		archiver.setArchiver( jarArchiver );
		archiver.setOutputFile( jarFile );
		archive.setForced( forceCreation );
		
		try {
			File contentDirectory = getFrogxPluginDirectory();
			if ( !contentDirectory.exists() ) {
				getLog().warn( "JAR will be empty - no content was marked for inclusion!" );
			}
			else {
				archiver.getArchiver().addDirectory(contentDirectory, getIncludes(), getExcludes());
			}
			
			File existingManifest = getDefaultManifestFile();
			if ( useDefaultManifestFile && existingManifest.exists() && archive.getManifestFile() == null ) {
				archive.setManifestFile( existingManifest );
			}
			
			archiver.createArchive(getProject(), archive);
			return jarFile;
		}
		catch ( Exception e ) {
			throw new MojoExecutionException( "Error assembling JAR", e );
		}
	}
	
	/**
	 * Generates the JAR.
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		//TODO: Remove this if we request maven 2.1.x and add prepare-package in the lifecycle.
		//Mojo prepareFrogxServicePlugin = new PreparePackageMojo();
		//prepareFrogxServicePlugin.execute();
		
		File jarFile = createArchive();
		
		String classifier = getClassifier();
		if ( classifier != null ) {
			projectHelper.attachArtifact( getProject(), "jar", classifier, jarFile );
		}
		else {
			getProject().getArtifact().setFile( jarFile );
		}
	}
	
	private String[] getIncludes() {
		if ( includes != null && includes.length > 0 ) {
			return includes;
		}
		return DEFAULT_INCLUDES;
	}
	
	private String[] getExcludes() {
		if ( excludes != null && excludes.length > 0 ) {
			return excludes;
		}
		return DEFAULT_EXCLUDES;
	}
	
}
