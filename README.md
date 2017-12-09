# intellij-plugin-server
The intention of this project is to mimic the JetBrains plugin repository so an organization/individual can host a plugin server without an involved publishing process.

The server currently reads the plugin.xml files within the deployed plugins to generate an available plugin list at the time of request.  There are no performance enhancements to cache the available plugins at this time.  The list is filtered by the build number provided by a JetBrains product, and the information from the most recently modified archive is returned to the requester.

# Packaging
`./gradlew[.bat] build`

This will generate the plugin-server-[version].jar file which contains everything to run the server.

# Execution
`java [-Doption=value ...] -jar [path-to-jar]/plugin-server-[version].jar`

