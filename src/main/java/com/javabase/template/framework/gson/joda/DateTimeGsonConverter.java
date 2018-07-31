package com.javabase.template.framework.gson.joda;

import java.lang.reflect.Type;

import org.joda.time.DateTime;
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
 * Gson Joda DateTime Serializer, Deserializer
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class DateTimeGsonConverter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

    /** Formatter specifier */
    private static final String DATETIMEPATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @Override
    public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String jsonString = json.getAsString();
        if(jsonString == null) {
            return null;
        }
        final DateTimeFormatter formatter = DateTimeFormat.forPattern(DATETIMEPATTERN);
        return formatter.parseDateTime(jsonString);
    }

    @Override
    public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
        final DateTimeFormatter formatter = DateTimeFormat.forPattern(DATETIMEPATTERN);
        return new JsonPrimitive(formatter.print(src));
    }

}
