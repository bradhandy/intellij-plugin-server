package net.jackofalltrades.idea.server;

import javax.annotation.PostConstruct;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class FreemarkerConfigurer {

    private final Environment environment;
    private final Configuration configuration;

    @Autowired
    public FreemarkerConfigurer(Environment environment, Configuration configuration) {
        this.environment = environment;
        this.configuration = configuration;
    }

    @PostConstruct
    void setEnvironmentVariablesOnFreeMarkerConfiguration() throws TemplateException {
        configuration.setSharedVariable("pluginsCategory", environment.getProperty("plugins.category"));
        configuration.setSharedVariable("pluginServer", environment.getProperty("plugins.server"));
    }

}
