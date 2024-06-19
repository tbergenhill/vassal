/**
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License (LGPL) v2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License v2.1 for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, copies are available
 * at http://www.opensource.org.
 */

package VASSAL.tools;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.FileInputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.apache.commons.io.IOUtils;

import VASSAL.build.Builder;


/**
 * Read the XML file (vassal.xml) which defines the location of the vassal engine server
 * and the access information for the database logger.
 * 
 * @author tdecarlo
 *
 */
public class ConfigFileReader {

	private static String configFileName = "vassal.xml";

	private Boolean serverSSL = false;
	private String serverName = "127.0.0.1"; //$NON-NLS-1$
	private String serverURL = "http://127.0.0.1/"; //$NON-NLS-1$
	private String serverURLAndPort = "http://127.0.0.1:5050/"; //$NON-NLS-1$
	private Integer serverPort = 5050;
	private String wiki = "wiki/";
	private String util = "util/";
	private String tileCachePath = VASSAL.Info.getConfDir()+"/tiles/";
	private static ConfigFileReader instance = null;

	public ConfigFileReader() {
	}

	private static String checkForEndingSlash(String value) {
		if (!value.endsWith("/")) {
			return value + "/";
		}
		return value;
	}

	private static String checkForNoStartingSlash(String value) {
		if (value.startsWith("/")) {
			return value.substring(1);
		}
		return value;
	}

	private static void init() {
		if (instance != null) return;
		instance = new ConfigFileReader();

		BufferedInputStream in = null;
		try {
			try {
				in = new BufferedInputStream(new FileInputStream(configFileName));
			}
			catch (IOException e) {
				ErrorDialog.show("Error.file_not_found",configFileName);
				return;
			}

			final Document doc = Builder.createDocument(in);
			DocumentTraversal docTrav = (DocumentTraversal)doc;
			NodeIterator nodeIter = docTrav.createNodeIterator(doc.getDocumentElement(),NodeFilter.SHOW_ELEMENT,null,true);
			for (Node node = nodeIter.nextNode(); node != null; node = nodeIter.nextNode()) {
				String nodeName = node.getNodeName();
				String nodeText = node.getTextContent();
				switch (nodeName) {
				case "useSSL":
					instance.serverSSL = Boolean.parseBoolean(nodeText);
					break;
				case "server":
					instance.serverName = nodeText;
					break;
				case "port":
					instance.serverPort = Integer.parseInt(nodeText);
					break;
				case "wiki":
					instance.wiki = nodeText;
					break;
				case "util":
					instance.util = nodeText;
					break;
				case "tileCachePath":
					instance.tileCachePath = nodeText;
					break;
				}
			}
			String serverNameWithSlash = checkForEndingSlash(instance.serverName);
			instance.serverURL = (instance.serverSSL ? "https://" : "http://") + serverNameWithSlash;
			instance.serverURLAndPort = instance.serverURL + ":" + instance.serverPort.toString(); 
			instance.wiki = checkForNoStartingSlash(instance.wiki);
			instance.wiki = checkForEndingSlash(instance.wiki);
			instance.wiki = instance.serverURL + instance.wiki;
			instance.util = checkForNoStartingSlash(instance.util);
			instance.util = checkForEndingSlash(instance.util);
			instance.util = instance.serverURL + instance.util;
			instance.tileCachePath = checkForEndingSlash(instance.tileCachePath);
			in.close();
		} catch (Exception e) {
			ErrorDialog.bug(e);
		}
		finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * @return the serverSSL
	 */
	public static Boolean getServerSSL() {
		init();
		return instance.serverSSL;
	}

	/**
	 * @return the serverName
	 */
	public static String getServerName() {
		init();
		return instance.serverName;
	}

	/**
	 * @return the serverPort
	 */
	public static Integer getServerPort() {
		init();
		return instance.serverPort;
	}

	/**
	 * @return the serverURL, constructed from the SSL state and server name
	 */
	public static String getServerURL() {
		init();
		return instance.serverURL;
	}

	/**
	 * @return the serverURLAndPort, constructed from the SSL state, server name, and port
	 */
	public static String getServerURLAndPort() {
		init();
		return instance.serverURLAndPort;
	}

	/**
	 * @return the wiki URL
	 */
	public static String getWikiURL() {
		init();
		return instance.wiki;
	}

	/**
	 * @return the util URL
	 */
	public static String getUtilURL() {
		init();
		return instance.util;
	}

	public static String getTileCachePath() {
		init();
		return instance.tileCachePath;
	}

}
