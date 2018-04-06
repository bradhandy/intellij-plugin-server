package net.jackofalltrades.idea.server;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import com.google.common.collect.Sets;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/plugin/uploadPlugin")
@ConfigurationProperties
public class IdeaPluginUploadController {

    private static final Set<String> ACCEPTABLE_CONTENT_TYPES = Sets.newHashSet("application/jar", "application/zip", "application/octet-stream");
    private static final String ZIP_CONTENT_TYPE = "application/zip";
    private static final String JAR_CONTENT_TYPE = "application/jar";

    private final Path pluginsDirectory;
    private final Tika tika;

    @Autowired
    public IdeaPluginUploadController(@Value("${plugins.dir}") String pluginsDirectory) {
        this.pluginsDirectory = Paths.get(pluginsDirectory);
        this.tika = new Tika();
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "multipart/form-data")
    void uploadPlugin(@RequestParam("file") MultipartFile pluginArchive, HttpServletResponse response) throws IOException {
        if (!ACCEPTABLE_CONTENT_TYPES.contains(pluginArchive.getContentType())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String contentType = tika.detect(pluginArchive.getInputStream());
        if (ZIP_CONTENT_TYPE.equals(contentType) || JAR_CONTENT_TYPE.equals(contentType)) {
            Files.copy(pluginArchive.getInputStream(), pluginsDirectory.resolve(pluginArchive.getOriginalFilename()));
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
