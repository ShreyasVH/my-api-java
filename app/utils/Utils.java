package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public static String generateUniqueIdByParam(String type)
    {
        Date currentDate = new Date();
        Long currentTimeStamp = currentDate.getTime();
        String hexString = Long.toHexString(currentTimeStamp * 1000);

        return (type + hexString);
    }

    public static Date parseDateString(String dateString)
    {
        Date date = null;

        try
        {
            date = (new SimpleDateFormat("yyyy-MM-dd")).parse(dateString);
        }
        catch(ParseException ex)
        {}

        return date;
    }
}
