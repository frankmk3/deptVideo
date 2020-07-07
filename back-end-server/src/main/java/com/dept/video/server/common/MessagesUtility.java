package com.dept.video.server.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * RequestMessage internationalization resolver
 */
@Component
public class MessagesUtility {


    private final MessageSource messageSource;

    @Autowired
    public MessagesUtility(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Get the corresponding translate value,
     * or the same by default.
     *
     * @param id
     * @return
     */
    public String getMessage(String id) {
        return getMessage(id, id);
    }


    /**
     * Get the corresponding translate value, or the default value. Replace values
     * using arguments.
     *
     * @param id
     * @param defaultValue
     * @param arg
     * @return
     */
    public String getMessage(String id, String defaultValue, Object... arg) {
        final Locale locale = LocaleContextHolder.getLocale();
        String message = "";
        try {
            message = messageSource.getMessage(id, arg, locale);
        } catch (NoSuchMessageException ex) {
            if (defaultValue != null) {
                message = MessageFormat.format(defaultValue, arg);
            }
        }
        return message;
    }
}
