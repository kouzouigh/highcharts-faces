package org.hcfaces.renderer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URISyntaxException;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.XMLUnit;
import org.hcfaces.renderer.xmlunit.IgnoreJsfViewSateInputElement;
import org.jboss.test.faces.htmlunit.HtmlUnitEnvironment;
import org.junit.After;
import org.junit.Before;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class RendererTestBase {

	static {
		XMLUnit.setNormalizeWhitespace(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreComments(true);
	}

	protected HtmlUnitEnvironment environment;

	@Before
	public void setUp() throws URISyntaxException {
		environment = new HtmlUnitEnvironment();
		environment.withResource("/WEB-INF/faces-config.xml",
				"faces-config.xml");
		environment.withWebRoot(new File(this.getClass().getResource(".")
				.toURI()));
		environment.start();
		// environment.getWebClient().setJavaScriptEnabled(false);
	}

	@After
	public void tearDown() {
		environment.release();
		environment = null;
	}

	protected void doTest(String pageName, String pageElementToTest)
			throws IOException, SAXException {
		doTest(pageName, pageName, pageElementToTest);
	}

	protected void doTest(String jsfPage, String xmlPage,
			String pageElementToTest) throws IOException, SAXException {
		HtmlPage page = environment.getPage('/' + jsfPage + ".jsf");
		HtmlElement panel = page.getElementById(pageElementToTest);
		assertNotNull(panel);

		checkXmlStructure(jsfPage, xmlPage, panel.asXml());
	}

	protected void checkXmlStructure(String pageName, String xmlunitPageName,
			String pageCode) throws SAXException, IOException {
		if (xmlunitPageName == null) {
			xmlunitPageName = pageName + ".xmlunit.xml";
		}
		InputStream expectedPageCode = this.getClass().getResourceAsStream(
				xmlunitPageName + ".xmlunit.xml");
		if (expectedPageCode == null) {
			return;
		}

		Diff xmlDiff = new Diff(new InputStreamReader(expectedPageCode),
				new StringReader(pageCode));
		xmlDiff.overrideDifferenceListener(getDifferenceListener());

		// xmlDiff.overrideElementQualifier(getEelementQualifer());

		if (!xmlDiff.similar()) {
			System.out.println("=== ACTUAL PAGE CODE ===");
			System.out.println(pageCode);
			System.out.println("======== ERROR =========");
			System.out.println(xmlDiff.toString());
			System.out.println("========================");
			fail("XML was not similar:" + xmlDiff.toString());
		}

	}

	protected DifferenceListener getDifferenceListener() {
		return new IgnoreJsfViewSateInputElement();
	}
	
}
