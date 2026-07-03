package views;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;

@ApplicationScoped
public class ViewRenderer {

    @Inject
    Configuration configuration;

    public String render(String templateName, Map<String, Object> data, HttpHeaders headers) {
        Locale locale = resolveLocale(headers);

        Map<String, Object> model = new HashMap<>(data);
        model.put("contextPath", "");
        model.put("i18n", new I18nMethod(locale));
        model.put("prettyTime", new PrettyTimeMethod(locale));

        try {
            Template template = configuration.getTemplate(templateName, locale);
            StringWriter writer = new StringWriter();
            template.process(model, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to render template: " + templateName, e);
        }
    }

    protected Locale resolveLocale(HttpHeaders headers) {
        List<Locale> locales = headers == null ? List.of() : headers.getAcceptableLanguages();
        for (Locale locale : locales) {
            if ("ja".equals(locale.getLanguage())) {
                return Locale.JAPANESE;
            }
            if ("en".equals(locale.getLanguage())) {
                return Locale.ENGLISH;
            }
        }
        return Locale.ENGLISH;
    }

}
