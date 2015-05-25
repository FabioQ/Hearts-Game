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
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Developer;
import org.apache.maven.model.License;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * Provide a plugin descriptor (games.xml) for frogx games.
 * 
 * @author G&uuml;nther Nie&szlig;
 * @goal generate-frogx-resources
 * @phase generate-resources
 */
public class GenerateFrogxResourcesMojo extends AbstractFrogxMojo {
	
	static final public String pluginDescriptorNS = "http://frogx.org/protocol/plugin";
	
	/**
	 * To look up Archiver/UnArchiver implementations
	 *
	 * @component
	 */
	protected ArchiverManager archiverManager;
	
	/**
	 * The implementation of the MUGMatch.
	 *
	 * @parameter expression="${frogx.match}"
	 */
	private String match;
	
	/**
	 * The namespace of the game which will be discovered by disco#info.
	 *
	 * @parameter expression="${frogx.namespace}"
	 */
	private String namespace;
	
	/**
	 * The category of the game.
	 *
	 * @parameter expression="${frogx.category}"
	 */
	private String category;
	
	/**
	 * The description of the game.
	 *
	 * @parameter expression="${frogx.description}" default-value="${project.description}"
	 */
	private String description;
	
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		boolean hasPluginDescriptor = false;
		File frogxPluginDescriptorSource = new File(getFrogxSourceDirectory(),
				frogxPluginDescriptor);
		
		hasPluginDescriptor = generatePluginDescriptor();
		if (frogxPluginDescriptorSource.exists()) {
			if (hasPluginDescriptor) {
				getLog().warn("The generated plugin descriptor "
						+ frogxPluginDescriptor + " "
						+ "overwrites the existing.");
			}
			hasPluginDescriptor = true;
		}
		
		if (!hasPluginDescriptor) {
			getLog().error("Can't generate or find a suitable plugin descriptor ("
					+ frogxPluginDescriptor + ").");
			getLog().error("Please configure the match and namespace in the"
					+ " Project Object Model or provide a "
					+ getRelativePath(frogxPluginDescriptorSource)
					+ " file.");
			throw new MojoFailureException(
					"Can't generate or find a proper plugin descriptor!");
		}
		
		File openfirePluginDescriptor = new File(
				getFrogxSourceDirectory(), "plugin.xml");
		if (supportOpenfire() && !openfirePluginDescriptor.exists()) {
			generateOpenfireDescriptor();
		}
	}
	
	public boolean generatePluginDescriptor() {
		// Check requirements
		if (match == null || namespace == null) {
			return false;
		}
		
		// Create XML data
		getLog().info("Generating a game descriptor (" + frogxPluginDescriptor + ").");
		DocumentFactory documentFactory = DocumentFactory.getInstance();
		Element gameEl = documentFactory.createElement("plugin", pluginDescriptorNS);
		
		Element element = gameEl.addElement("type");
		element.setText("match");
		
		element = gameEl.addElement("match");
		element.setText(match);
		
		element = gameEl.addElement("namespace");
		element.setText(namespace);
		
		if (description != null) {
			element = gameEl.addElement("description");
			element.setText(description);
		}
		
		if (category != null) {
			element = gameEl.addElement("category");
			element.setText(category);
		}
		
		Document doc = documentFactory.createDocument(gameEl);
		doc.setXMLEncoding("UTF-8");
		
		// Check and create the necessary folder
		File genResourceDir = getGeneratedFrogxResourcesDirectory();
		if (!genResourceDir.exists()) {
			genResourceDir.mkdirs();
		}
		
		// Write the game descriptor
		try {
			writeDocument(doc,
					new File(getGeneratedFrogxResourcesDirectory(), frogxPluginDescriptor));
		} catch (IOException e) {
			getLog().warn("An Error occured while writing the generated "
					+ "game descriptor. ", e);
			return false;
		}
		return true;
	}
	
	public boolean generateOpenfireDescriptor() {
		getLog().info("Generating Openfire plugin descriptor.");
		
		// localize the plugin extension
		Artifact pluginExtension = getFrogxPluginExtension();
		if (pluginExtension == null) {
			getLog().warn("Can't locate frogx-plugin-extension.");
			return false;
		}
		
		// Unpack the plugin extension
		File pluginExtensionDir = new File(getOutputDirectory(), 
				pluginExtension.getArtifactId() + "-" + pluginExtension.getVersion());
		try {
			UnArchiver unArchiver;
			unArchiver = archiverManager.getUnArchiver(pluginExtension.getFile());
			unArchiver.setSourceFile(pluginExtension.getFile());
			
			if (!pluginExtensionDir.exists()) {
				if (!pluginExtensionDir.mkdirs()) {
					getLog().warn("Can't create frogx-plugin-extension folder.");
					return false;
				}
			}
			unArchiver.setDestDirectory(pluginExtensionDir);
			unArchiver.extract();
		}
		catch (NoSuchArchiverException e) {
			getLog().warn("Can't initialize archiver.");
			return false;
		}
		catch (ArchiverException e) {
			getLog().warn("Can't extract frogx-plugin-extension.");
			return false;
		}
		catch (IOException e) {
			getLog().warn("Can't extract frogx-plugin-extension.");
			return false;
		}
		
		// Read and parse Openfire plugin descriptor template
		Document document;
		try {
			SAXReader reader = new SAXReader();
			document = reader.read(new File(pluginExtensionDir, "plugin.xml"));
		}
		catch (DocumentException e) {
			getLog().warn("Can't read or parse openfire plugin template.", e);
			return false;
		}
		
		// Setup document
		Element plugin = document.getRootElement();
		Element name = plugin.addElement("name");
		name.setText(getProject().getArtifactId());
		if (this.description != null) {
			Element description = plugin.addElement("description");
			description.setText(this.description);
		}
		@SuppressWarnings("unchecked")
		List<Developer> developers = getProject().getDevelopers();
		if (developers != null && !developers.isEmpty()) {
			Iterator<Developer> it = developers.iterator();
			Developer developer = it.next();
			String authors = developer.getName();
			for (; it.hasNext();) {
				developer = it.next();
				authors = authors + ", " + developer.getName();
			}
			Element authorEl = plugin.addElement("author");
			authorEl.setText(authors);
		}
		if (getProject().getVersion() != null) {
			Element version = plugin.addElement("version");
			version.setText(getProject().getVersion());
		}
		DateFormat dfmt = new SimpleDateFormat("dd/MM/yyyy");
		Element date = plugin.addElement("date");
		date.setText(dfmt.format(new Date()));
		if (getProject().getUrl() != null) {
			Element url = plugin.addElement("url");
			url.setText(getProject().getUrl());
		}
		@SuppressWarnings("unchecked")
		List<License> licenses = getProject().getLicenses();
		if (licenses != null && !licenses.isEmpty()) {
			Iterator<License> it = licenses.iterator();
			License license = it.next();
			String licenseText = license.getName();
			for (; it.hasNext();) {
				license = it.next();
				licenseText = licenseText + ", " + license.getName();
			}
			Element authorEl = plugin.addElement("licenseType");
			authorEl.setText(licenseText);
		}
		
		// Write the game descriptor
		try {
			writeDocument(document,
					new File(getGeneratedFrogxResourcesDirectory(), "plugin.xml"));
		} catch (IOException e) {
			getLog().warn("An Error occured while writing the generated "
					+ "openfire descriptor. ", e);
			return false;
		}
		return true;
	}
	
	public void writeDocument(Document document, File file) throws IOException {
		PrintStream stream = new PrintStream(file);
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(stream, format);
		writer.write(document);
	}
	
}
