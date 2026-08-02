package views;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.ocpsoft.prettytime.PrettyTime;

import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class PrettyTimeMethod implements TemplateMethodModelEx {

    private final Locale locale;

    public PrettyTimeMethod(Locale locale) {
        this.locale = locale;
    }

    @Override
    public Object exec(@SuppressWarnings("rawtypes") List arguments) throws TemplateModelException {
        if (arguments.isEmpty() || !(arguments.get(0) instanceof TemplateDateModel)) {
            throw new TemplateModelException("prettyTime() requires a date argument.");
        }
        Date date = ((TemplateDateModel) arguments.get(0)).getAsDate();
        return new PrettyTime(locale).format(date);
    }

}
