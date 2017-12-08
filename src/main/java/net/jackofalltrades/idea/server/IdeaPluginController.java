package net.jackofalltrades.idea.server;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipInputStream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.jackofalltrades.idea.IntellijBuildVersion;
import net.jackofalltrades.idea.PluginDescriptor;
import net.jackofalltrades.idea.PluginDescriptorFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plugins")
@ConfigurationProperties
public class IdeaPluginController {

    private final Path pluginsDirectory;
    private final String pluginsCategory;
    private final Configuration freemarkerConfiguration;

    public IdeaPluginController(@Value("${plugins.dir}") String pluginsDirectory, @Value("${plugins.category}") String pluginsCategory, Configuration freemarkerConfiguration) {
        this.pluginsDirectory = Paths.get(pluginsDirectory);
        this.pluginsCategory = pluginsCategory;
        this.freemarkerConfiguration = freemarkerConfiguration;
    }

    @RequestMapping(path = "/list", method = RequestMethod.GET, produces = "application/xml")
    @ResponseBody
    String listPlugins(@RequestParam("build") String buildNumber) {
        Map<String, PluginDescriptor> pluginDescriptorMap = Maps.newHashMap();
        IntellijBuildVersion buildVersion = IntellijBuildVersion.fromString(buildNumber);
        try {
            Files.walkFileTree(pluginsDirectory, new PluginFileVisitor(pluginDescriptorMap, buildVersion));
            Template template = freemarkerConfiguration.getTemplate("plugin-list.xml.ftl");
            Map<String, Object> dataModel = ImmutableMap.of("pluginDescriptors", pluginDescriptorMap.values(), "pluginsCategory", pluginsCategory);

            StringWriter output = new StringWriter();
            template.process(dataModel, output);

            return output.toString();
        } catch (IOException | TemplateException e) {
            throw new PluginListGenerationFailed(e);
        }
    }

    @RequestMapping(path = "/download/{file:.+}", method = RequestMethod.GET, produces = {"application/zip", "application/jar"})
    void downloadFile(@PathVariable String file, HttpServletResponse response) throws IOException {
        Path filePath = Paths.get(file);
        sanitizePath(filePath);

        String finalFilePath = filePath.toString();
        if (finalFilePath.endsWith(".jar")) {
            response.setContentType("application/jar");
        } else if (finalFilePath.endsWith(".zip")) {
            response.setContentType("application/zip");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", filePath.getFileName().toString()));
        Files.copy(pluginsDirectory.resolve(filePath), response.getOutputStream());
    }

    @ResponseStatus(value = HttpStatus.NO_CONTENT, reason = "No plugin list")
    public static class PluginListGenerationFailed extends RuntimeException {

        public PluginListGenerationFailed(Throwable cause) {
            super(cause);
        }

    }

    private void sanitizePath(Path path) {
        Iterator<Path> pathIterator = path.iterator();
        while (pathIterator.hasNext()) {
            if ("..".equals(pathIterator.next().toString())) {
                pathIterator.remove();
            }
        }
    }

    private class PluginFileVisitor implements FileVisitor<Path> {

        private final Map<String, PluginDescriptor> pluginDescriptorMap;
        private final IntellijBuildVersion buildVersion;

        public PluginFileVisitor(Map<String, PluginDescriptor> pluginDescriptorMap, IntellijBuildVersion buildVersion) {
            this.pluginDescriptorMap = pluginDescriptorMap;
            this.buildVersion = buildVersion;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            String fileName = file.getFileName().toString().toLowerCase();
            if (fileName.endsWith(".zip") || fileName.endsWith(".jar")) {
                PluginDescriptor pluginDescriptor = PluginDescriptorFactory.createDescriptorFromArchive(new ZipInputStream(new FileInputStream(file.toFile())));
                if (pluginDescriptor.supportsVersion(buildVersion)) {
                    PluginDescriptor linkedPluginDescriptor = pluginDescriptor.linkToSourceArchive(pluginsDirectory.relativize(file), Files.size(file),
                            Files.getLastModifiedTime(file).toMillis());
                    if (!pluginDescriptorMap.containsKey(linkedPluginDescriptor.getId())
                            || linkedPluginDescriptor.getLastModifiedTime() > pluginDescriptorMap.get(linkedPluginDescriptor.getId()).getLastModifiedTime()) {
                        pluginDescriptorMap.put(linkedPluginDescriptor.getId(), linkedPluginDescriptor);
                    }
                }
            }

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }
}
