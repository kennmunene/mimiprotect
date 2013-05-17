/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.variance.vjax.xml;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * 
 * @author Marembo
 */
public class VDocument {
	static {
		System.setProperty("org.xml.sax.driver",
				"org.apache.xerces.parsers.SAXParser");
	}
	private VElement rootElement;
	private URL documentUrl;
	private File documentName;
	@SuppressWarnings("unused")
	private String documentType;
	@SuppressWarnings("unused")
	private String schemaUrl;
	private VElement notationDeclaration;
	private boolean standalone;
	private List<VDocument> includes;

	public VDocument() {
		this.includes = new ArrayList<VDocument>();
		this.standalone = true;
	}

	public VDocument(String name) {
		this(new File(name));
	}

	public VDocument(File documentFile) {
		this();
		this.documentName = documentFile;
		if (!this.documentName.exists()) {
			try {
				// check if the folders exist
				if (this.documentName.getParentFile() != null) {
					if (!this.documentName.getParentFile().exists()) {
						this.documentName.getParentFile().mkdirs();
					}
				}
				this.documentName.getAbsoluteFile().createNewFile();
			} catch (IOException ex) {
				try {
					Logger.getLogger(VDocument.class.getName()).log(
							Level.SEVERE, documentName.getCanonicalPath(), ex);
				} catch (IOException ex1) {
					Logger.getLogger(VDocument.class.getName()).log(
							Level.SEVERE, null, ex1);
				}
			}
		}
		this.doGetURL();
	}

	public VDocument(URL documentUrl, boolean standalone) {
		this();
		this.documentUrl = documentUrl;
		this.standalone = standalone;
	}

	public VDocument(URL documentUrl) {
		this();
		this.documentUrl = documentUrl;
	}

	public VDocument(File documentFile, String documentType, String schemaUrl) {
		this(documentFile);
		this.documentType = documentType;
		this.schemaUrl = schemaUrl;
	}

	public File getDocumentName() {
		return documentName;
	}

	public void setDocumentName(File documentName) {
		this.documentName = documentName;
	}

	private void doGetURL() {
		try {
			this.documentUrl = this.documentName.toURI().toURL();
		} catch (Exception e) {
		}
	}

	public String getName() {
		return documentName.getName();
	}

	public VElement getRootElement() {
		return rootElement;
	}

	public void setRootElement(VElement rootElement) {
		this.rootElement = rootElement;
	}

	@Override
	public String toString() {
		return (this.notationDeclaration == null) ? ""
				: this.notationDeclaration.elemString()
						+ this.rootElement.elemString().trim();
	}

	public String toXmlString() {
		VNamespace vn = this.rootElement.getAssociatedNamespace();
		standalone = vn == null || !vn.isHasSchema();
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone="
				+ ((standalone) ? "\"yes\"" : "\"no\"") + " ?>\n"
				+ this.rootElement.elemString().trim();
	}

	public void parse() {
		BufferedInputStream reader = null;
		try {
			reader = new BufferedInputStream(this.documentUrl.openStream());
			parse(reader);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					Logger.getLogger(VDocument.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		}
	}

	private void parse(InputStream inn, VXMLHandler handler)
			throws SAXException, IOException {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xmlreader = sp.getXMLReader();
			xmlreader.setContentHandler(handler);
			InputSource s = new InputSource(new InputStreamReader(inn));
			xmlreader.setProperty(
					"http://xml.org/sax/properties/lexical-handler", handler);
			// xmlreader.setProperty(
			// "http://xml.org/sax/properties/declaration-handler",
			// handler);
			xmlreader.parse(s);
			this.setRootElement(handler.getRootElement());
			List<String[]> decls = handler.getNotationDeclaration();
			for (String[] d : decls) {
				VElement decl = null;
				if (notationDeclaration == null) {
					notationDeclaration = new VElement(d[0]);
					decl = notationDeclaration;
				} else {
					decl = new VElement(d[0], notationDeclaration);
				}
				decl.addAttribute(new VAttribute("publicid", d[1]));
				decl.addAttribute(new VAttribute("systemid", d[2]));
			}
			// find if we have includes, read then in
			for (VElement elem : rootElement.getChildren()) {
				VNamespace xinclude = elem.getAssociatedNamespace();
				if (xinclude != null) { // Should this be null?
					if (xinclude.equals(VNamespace.XINCLUDE_NAMESPACE)) {
						String name = elem.getAttribute("href").getValue();
						URL url = null;
						// consider file or http or ftp
						if (!name.startsWith("file")
								&& !name.startsWith("http")
								&& !name.startsWith("ftp")) {
							File file = new File(name);
							url = file.toURI().toURL();
						} else {
							url = new URL(name);
						}
						VDocument doc = new VDocument(name);
						doc.parse(url.openStream(), handler);
						this.includes.add(doc);
					}
				}
			}
		} catch (Exception ee) {
			System.out.println(ee.getMessage());
			throw new SAXException(ee);
		}
	}

	private void parse(InputStream inn) throws SAXException, IOException {
		parse(inn, new VXMLHandler());
	}

	public VElement getNotationDeclaration() {
		return notationDeclaration;
	}

	public void removeElement(VElement elem) {
		if (this.rootElement != null) {
			this.rootElement.removeChild(elem);
		}
	}

	public static VDocument parseDocument(InputStream inn) {
		try {
			VDocument doc = new VDocument();
			doc.parse(inn);
			return doc;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static VDocument parseDocument(String filePath) {
		try {
			File file = new File(filePath);
			return parseDocument(new FileInputStream(file));
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static VDocument parseDocumentFromString(String xml) {
		if (!xml.contains("<?xml")) {
			xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\""
					+ " ?>\n" + xml;
		}
		return VDocument
				.parseDocument(new ByteArrayInputStream(xml.getBytes()));
	}

	public void writeDocument() {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(documentName);
			out.write(this.toXmlString().getBytes());
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
					Logger.getLogger(VDocument.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		}
		// write the includes
		for (VDocument doc : includes) {
			doc.writeDocument();
		}
	}

	public void addInclude(VDocument doc) {
		this.includes.add(doc);
		VNamespace namespace = VNamespace.XINCLUDE_NAMESPACE;
		VElement elem = new VElement("include");
		elem.addNamespace(namespace);
		elem.addAttribute(new VAttribute("href", doc.documentName
				.getAbsolutePath()));
		rootElement.addChildAsFirst(elem);
	}

	public void setIncludes(List<VDocument> includes) {
		this.includes = new ArrayList<VDocument>(includes);
		if (!this.includes.isEmpty()) {
			// add the schema namespace
			VNamespace namespace = VNamespace.XINCLUDE_NAMESPACE;
			for (VDocument doc : includes) {
				VElement elem = new VElement("include");
				elem.addNamespace(namespace);
				elem.addAttribute(new VAttribute("href", doc.documentName
						.getAbsolutePath()));
				rootElement.addChildAsFirst(elem);
			}
		}
	}

	public List<VDocument> getIncludes() {
		return includes;
	}
}