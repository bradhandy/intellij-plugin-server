package net.jackofalltrades.idea;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Create PluginDescriptor objects from plugin archives.
 *
 * @author bhandy
 */
public class PluginDescriptorFactory {

    public static PluginDescriptor createDescriptorFromArchive(ZipInputStream zipInputStream) throws IOException {
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (zipEntry.isDirectory()) {
                continue;
            }

            if (zipEntry.getName().endsWith(".jar") || zipEntry.getName().endsWith(".zip")) {
                PluginDescriptor pluginDescriptor = createDescriptorFromArchive(new ZipInputStream(zipInputStream));
                if (pluginDescriptor != null) {
                    return pluginDescriptor;
                }
            } else if (zipEntry.getName().equals("META-INF/plugin.xml")) {
                return createDescriptorFromXml(zipInputStream);
            }
        }

        return null;
    }

    private static PluginDescriptor createDescriptorFromXml(InputStream inputStream) throws IOException {
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            PluginXmlHandler xmlHandler = new PluginXmlHandler();
            reader.setContentHandler(xmlHandler);
            reader.parse(new InputSource(inputStream));

            return xmlHandler.buildPluginDescriptor();
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException(e);
        }
    }

    private static class PluginXmlHandler extends DefaultHandler {

        private PluginDescriptor.Builder builder;

        private String currentElement;
        private Attributes elementAttributes;
        private StringBuilder elementContent;

        @Override
        public void startElement(String uri, String localName, String qualifiedName, Attributes attributes) throws SAXException {
            if (qualifiedName.equals("idea-plugin")) {
                builder = PluginDescriptor.builder();
            } else {
                this.currentElement = qualifiedName;
                this.elementAttributes = attributes;
                this.elementContent = new StringBuilder();
            }
        }

        @Override
        public void endElement(String uri, String localName, String qualifiedName) throws SAXException {
            if (currentElement != null && currentElement.equals(qualifiedName)) {
                String content = elementContent.length() > 0 ? elementContent.toString().trim() : "";

                switch (currentElement) {
                    case "id":
                        builder.withId(content);
                        break;
                    case "name":
                        builder.withName(content);
                        break;
                    case "vendor":
                        builder.withVendorName(content)
                            .withVendorEmail(elementAttributes.getValue("email"))
                            .withVendorUrl(elementAttributes.getValue("url"));
                        break;
                    case "version":
                        builder.withVersion(content);
                        break;
                    case "idea-version":
                        builder.withEarliestSupportedBuildNumber(IntellijBuildVersion.fromString(elementAttributes.getValue("since-build")))
                            .withLatestSupportedBuilderNumber(IntellijBuildVersion.fromString(elementAttributes.getValue("until-build")));
                        break;
                    case "description":
                        builder.withDescription(content);
                        break;
                    case "change-notes":
                        builder.withChangeNotes(content);
                        break;
                    case "depends":
                        String optionalFlag = elementAttributes.getValue("optional");
                        if (optionalFlag != null && Boolean.valueOf(optionalFlag)) {
                            builder.withOptionalDependency(content);
                        } else {
                            builder.withRequiredDependency(content);
                        }
                        break;
                }
            }
        }

        @Override
        public void characters(char[] content, int start, int length) throws SAXException {
            if (elementContent != null) {
                elementContent.append(content, start, length);
            }
        }

        public PluginDescriptor buildPluginDescriptor() {
            return builder.build();
        }

    }

    private PluginDescriptorFactory() {

    }

}
