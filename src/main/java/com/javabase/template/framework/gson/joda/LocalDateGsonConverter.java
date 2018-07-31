package com.javabase.template.framework.gson.joda;

import java.lang.reflect.Type;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Gson Joda LocalDate Serializer, Deserializer
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class LocalDateGsonConverter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    /** Formatter specifier */
    private static final String DATEPATTERN = "yyyy-MM-dd";

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String jsonString = json.getAsString();
        if(jsonString == null) {
            return null;
        }
        final DateTimeFormatter formatter = DateTimeFormat.forPattern(DATEPATTERN);
        return formatter.parseLocalDate(jsonString);
    }

    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        final DateTimeFormatter formatter = DateTimeFormat.forPattern(DATEPATTERN);
        return new JsonPrimitive(formatter.print(src));
    }

}
