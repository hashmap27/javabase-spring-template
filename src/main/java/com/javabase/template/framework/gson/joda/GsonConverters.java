package com.javabase.template.framework.gson.joda;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Gson Converter Registration Util
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class GsonConverters {

    private GsonConverters() { /* do nothing */ }

    public static GsonBuilder registerAllConverters(GsonBuilder gsonBuilder) {
        if(gsonBuilder == null) {
            throw new NullPointerException("builder cannot be null.");
        }

        registerJodaLocalDateTimeConverter(gsonBuilder);

        return gsonBuilder;
    }

    public static GsonBuilder registerJodaLocalDateTimeConverter(GsonBuilder gsonBuilder) {
        if(gsonBuilder == null) {
            throw new NullPointerException("builder cannot be null.");
        }
        gsonBuilder.registerTypeAdapter(new TypeToken<LocalDateTime>(){}.getType(), new LocalDateTimeGsonConverter());
        gsonBuilder.registerTypeAdapter(new TypeToken<LocalDate>(){}.getType(), new LocalDateGsonConverter());
        gsonBuilder.registerTypeAdapter(new TypeToken<DateTime>(){}.getType(), new DateTimeGsonConverter());
        return gsonBuilder;
    }
}
