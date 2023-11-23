package com.gebeya.bankAPI.ServiceImpl;

import com.gebeya.bankAPI.Service.Profile;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ProfileImpl implements Profile {

    ResourceBundleMessageSource messageSource;
    public ProfileImpl(ResourceBundleMessageSource messageSource)
    {
        this.messageSource=messageSource;
    }
    public String[] language(String lang){
        Locale locale1 = new Locale(lang);
        String[] messages = new String[8];
        for(int i = 0;i<messages.length;i++)
        {
            String messageKey = "message" + i;
            messages[i] =messageSource.getMessage(messageKey,null,locale1);
        }
        return messages;

    }
}