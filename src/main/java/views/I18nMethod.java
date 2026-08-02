package views;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class I18nMethod implements TemplateMethodModelEx {

    private final ResourceBundle bundle;

    public I18nMethod(Locale locale) {
        // Disable fallback to the JVM default locale so that unsupported
        // locales resolve to the base messages.properties (English)
        this.bundle = ResourceBundle.getBundle("conf.messages", locale,
                ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
    }

    @Override
    public Object exec(@SuppressWarnings("rawtypes") List arguments) throws TemplateModelException {
        if (arguments.isEmpty()) {
            throw new TemplateModelException("i18n() requires a message key.");
        }
        return bundle.getString(arguments.get(0).toString());
    }

}
