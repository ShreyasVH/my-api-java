package myapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import myapi.constants.Constants;
import myapi.models.*;
import play.libs.Akka;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by shreyasvh on 8/27/17.
 */
public class Utils {

    private static final ObjectMapper objMapper = new ObjectMapper();

    public static final ExecutionContext playActorContext = Akka.system().dispatchers().lookup(Constants.PLAY_ACTOR_CONTEXT);

    public static <T> T convertObject(Object from, Class<T> to) {
        return objMapper.convertValue(from, to);
    }

    public static <T> List<T> convertObjectList(Object from, Class<T> to)
    {
        return objMapper.convertValue(from, TypeFactory.defaultInstance().constructCollectionType(List.class,
                to));
    }

    public static String generateUniqueIdByParam(String type)
    {
        Date currentDate = new Date();
        Long currentTimeStamp = currentDate.getTime();
        String hexString = Long.toHexString(currentTimeStamp * 1000);

        return (type + hexString);
    }

    public static <T> byte[] serialize(T object)
    {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try
        {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            return bos.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            try
            {
                if (out != null)
                    out.close();
            }
            catch (IOException ex)
            {
            }
            try
            {
                bos.close();
            }
            catch (IOException ex)
            {
            }
        }
    }

    public static Object deserialize(byte[] byteArray)
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
        ObjectInput in = null;
        try
        {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            return o;
        }
        catch (ClassNotFoundException | IOException e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            try
            {
                bis.close();
            }
            catch (IOException ex)
            {
            }
            try
            {
                if (in != null)
                    in.close();
            }
            catch (IOException ex)
            {
            }
        }
    }

    public static GenericResponse getResponseFromBooleanFlag(Boolean flag, RequestType requestType)
    {
        GenericResponse response = new GenericResponse();
        if(flag)
        {
            response.setType(ResponseType.SUCCESS);
            SuccessResponse successResponse = null;
            switch(requestType)
            {
                case CREATE:
                    successResponse = SuccessResponse.CREATE_SUCCESS;
                    break;
                case UPDATE:
                    successResponse = SuccessResponse.UPDATE_SUCCESS;
                    break;
                case DELETE:
                    successResponse = SuccessResponse.DELETE_SUCCESS;
                    break;
                default:
            }
            response.setCode(successResponse.getCode());
            response.setDescription(successResponse.getDescription());
        }
        else
        {
            response.setType(ResponseType.ERROR);
            response.setCode(ErrorResponse.API_FAILED.getCode());
            response.setDescription(ErrorResponse.API_FAILED.getDescription());
        }
        return response;
    }

    public static void scheduleOnce(Runnable runnable) {
        Akka.system().scheduler().scheduleOnce(Duration.create(10, TimeUnit.MILLISECONDS), runnable, playActorContext);
    }

    public static String ucfirst(String string)
    {
        String returnString = string;
        if(null != string)
        {
            returnString = (Character.toUpperCase(string.charAt(0)) + string.substring(1));
        }
        return returnString;
    }
}
