package net.jackofalltrades.idea;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.io.IOException;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.*;

public class PluginDescriptorFactoryTest {

    @Test
    public void verifyPluginDescriptorFromZipArchive() throws Exception {
        PluginDescriptor pluginDescriptor = PluginDescriptorFactory.createDescriptorFromArchive(
                new ZipInputStream(ClassLoader.getSystemResourceAsStream("sample-plugin.zip")));

        assertNotNull("There should have been a plugin descriptor.", pluginDescriptor);
        assertEquals("The plugin ID does not match.", "com.plugin.some", pluginDescriptor.getId());
        assertEquals("The plugin name does not match.", "Some Plugin", pluginDescriptor.getName());
        assertEquals("The vendor email address does not match.", "bhandy@jack-of-all-trades.net", pluginDescriptor.getVendorEmail());
        assertEquals("The vendor URL does not match.", "https://www.jack-of-all-trades.net", pluginDescriptor.getVendorUrl());
        assertEquals("The vendor name does not match.", "Jack-of-all-trades.net", pluginDescriptor.getVendorName());
        assertEquals("The version number does not match.", "1.0", pluginDescriptor.getVersion());
        assertEquals("The earliest supported build number does not match.", IntellijBuildVersion.fromString("1.2.3"),
                pluginDescriptor.getEarliestSupportedBuildNumber());
        assertEquals("The latest supported build number does not match.", IntellijBuildVersion.fromString("1.*"),
                pluginDescriptor.getLatestSupportedBuildNumber());
        assertEquals("The description does not match.", "Provides support for quickly editing and refactoring internal Dynamic Interview configuration files.",
                pluginDescriptor.getDescription());
        assertEquals("The list of required dependencies does not match.",
                Sets.newHashSet("com.intellij.modules.platform", "com.intellij.modules.xml"),
                pluginDescriptor.getRequiredDependencies());
        assertEquals("The list of optional dependencies does not match.",
                Sets.newHashSet("com.intellij.properties", "com.intellij.spring"),
                pluginDescriptor.getOptionalDependencies());
        assertTrue("The change notes should have been parsed.",
                pluginDescriptor.getChangeNotes() != null && pluginDescriptor.getChangeNotes().contains("<li> Version 6.0.0 </li>"));
    }

    @Test
    public void verifyPluginDescriptorFromJarArchive() throws Exception {
        PluginDescriptor pluginDescriptor = PluginDescriptorFactory.createDescriptorFromArchive(
                new ZipInputStream(ClassLoader.getSystemResourceAsStream("test.jar")));

        assertNotNull("There should have been a plugin descriptor.", pluginDescriptor);
        assertEquals("The plugin ID does not match.", "com.plugin.some", pluginDescriptor.getId());
        assertEquals("The plugin name does not match.", "Some Plugin", pluginDescriptor.getName());
        assertEquals("The vendor email address does not match.", "bhandy@jack-of-all-trades.net", pluginDescriptor.getVendorEmail());
        assertEquals("The vendor URL does not match.", "https://www.jack-of-all-trades.net", pluginDescriptor.getVendorUrl());
        assertEquals("The vendor name does not match.", "Jack-of-all-trades.net", pluginDescriptor.getVendorName());
        assertEquals("The version number does not match.", "1.0", pluginDescriptor.getVersion());
        assertEquals("The earliest supported build number does not match.", IntellijBuildVersion.fromString("1.2.3"),
                pluginDescriptor.getEarliestSupportedBuildNumber());
        assertEquals("The latest supported build number does not match.", IntellijBuildVersion.fromString("1.*"),
                pluginDescriptor.getLatestSupportedBuildNumber());
        assertEquals("The description does not match.", "Provides support for quickly editing and refactoring internal Dynamic Interview configuration files.",
                pluginDescriptor.getDescription());
        assertEquals("The list of required dependencies does not match.",
                Sets.newHashSet("com.intellij.modules.platform", "com.intellij.modules.xml"),
                pluginDescriptor.getRequiredDependencies());
        assertEquals("The list of optional dependencies does not match.",
                Sets.newHashSet("com.intellij.properties", "com.intellij.spring"),
                pluginDescriptor.getOptionalDependencies());
        assertTrue("The change notes should have been parsed.",
                pluginDescriptor.getChangeNotes() != null && pluginDescriptor.getChangeNotes().contains("<li> Version 6.0.0 </li>"));
    }

    @Test
    public void verifyNoPluginDescriptorFromZipArchive() throws Exception {
        assertNull("There should not have been a plugin descriptor.", PluginDescriptorFactory.createDescriptorFromArchive(
                new ZipInputStream(ClassLoader.getSystemResourceAsStream("bad-plugin.zip"))));
    }

    @Test
    public void verifyNoPluginDescriptorFromJarArchive() throws Exception {
        assertNull("There should not have been a plugin descriptor.", PluginDescriptorFactory.createDescriptorFromArchive(
                new ZipInputStream(ClassLoader.getSystemResourceAsStream("bad-plugin.jar"))));
    }

    @Test
    public void verifyNoPluginDescriptorFromEmptyArchive() throws Exception {
        assertNull("There should not have been a plugin descriptor.", PluginDescriptorFactory.createDescriptorFromArchive(
                new ZipInputStream(ClassLoader.getSystemResourceAsStream("empty.zip"))));
    }

    @Test(expected = IOException.class)
    public void verifyInvalidPluginDescriptorThrowsException() throws Exception {
        PluginDescriptorFactory.createDescriptorFromArchive(new ZipInputStream(ClassLoader.getSystemResourceAsStream("invalid-plugin-xml.jar")));
    }

}
