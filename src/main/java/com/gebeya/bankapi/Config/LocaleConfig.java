package com.gebeya.bankAPI.Config;
/**
 * The localConfig  program implements an application that
 * simply displays Language Messages! to the standard output.
 *
 * @author  Elizabeth
 * @version 2.0
 * @since   2023-01-01
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class LocaleConfig {
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sir = new SessionLocaleResolver();
        sir.setDefaultLocale(Locale.US);
        return sir;
    }
    @Bean
    public ResourceBundleMessageSource messageSource()

    {
        ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
        rs.setBasename("messages");
        rs.setUseCodeAsDefaultMessage(true);
        return rs;
    }

}