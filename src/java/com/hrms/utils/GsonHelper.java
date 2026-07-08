
package com.hrms.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GsonHelper {
    
    // Java16+ ko truy cập đc Local... vì bị set private -> phải đky TypeAdapter để dùng đc
    private static final Gson gson = new GsonBuilder()
        // Adapter cho LocalDateTime
        .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> 
            new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
        .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
            LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        
        // Adapter cho LocalDate
        .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> 
            new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
        .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
            LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
        
        .create();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

//    public static <T> T fromJson(String json, Class<T> classOfT) {
//        return gson.fromJson(json, classOfT);
//    }
    
    public static <T> T fromJson(java.io.Reader reader, Class<T> classOfT) {
        return gson.fromJson(reader, classOfT);
    }
}
