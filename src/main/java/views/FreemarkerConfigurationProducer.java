package views;

import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

public class FreemarkerConfigurationProducer {

    @Produces
    @ApplicationScoped
    public Configuration configuration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_33);
        configuration.setClassLoaderForTemplateLoading(Thread.currentThread().getContextClassLoader(), "views");
        configuration.setDefaultEncoding("UTF-8");
        // HTML-escape template variables, matching Ninja's default behavior
        configuration.setOutputFormat(HTMLOutputFormat.INSTANCE);
        configuration.setLocalizedLookup(false);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        return configuration;
    }

}
