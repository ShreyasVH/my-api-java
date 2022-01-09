package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.Date;
import java.util.List;

public class Utils
{
    private static final ObjectMapper objMapper = new ObjectMapper();

    public static <T> T convertObject(Object from, Class<T> to) {
        return objMapper.convertValue(from, to);
    }

    public static <T> List<T> convertObjectList(Object from, Class<T> to)
    {
        return objMapper.convertValue(from, TypeFactory.defaultInstance().constructCollectionType(List.class, to));
    }

    public static Date getCurrentDate()
    {
        return new Date();
    }
}
