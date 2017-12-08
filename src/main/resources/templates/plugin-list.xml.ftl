<?xml version="1.0" encoding="UTF-8"?>
<plugin-repository>
    <ff>"${pluginsCategory}"</ff>
    <category name="${pluginsCategory}">
<#list pluginDescriptors as pluginDescriptor>
        <idea-plugin downloads='0' size='${pluginDescriptor.archiveSize?c}' date='${pluginDescriptor.lastModifiedTime?c}' url=''>
            <name>${pluginDescriptor.name}</name>
            <id>${pluginDescriptor.id}</id>
            <description>
                <![CDATA[
                ${pluginDescriptor.description}
                ]]>
            </description>
            <version>${pluginDescriptor.version}</version>
            <idea-version min="n/a" max="n/a" since-build="${pluginDescriptor.earliestSupportedBuildNumber}" until-build="${pluginDescriptor.latestSupportedBuildNumber}"/>
            <download-url>http://localhost:8080/plugins/download/${pluginDescriptor.sourceArchive}</download-url>
            <vendor email="${pluginDescriptor.vendorEmail}" url="${pluginDescriptor.vendorUrl}">${pluginDescriptor.vendorName}</vendor>
            <change-notes>
                <![CDATA[
                ${pluginDescriptor.changeNotes}
                ]]>
            </change-notes>
        </idea-plugin>
</#list>
    </category>
</plugin-repository>
