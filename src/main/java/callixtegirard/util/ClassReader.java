package callixtegirard.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static callixtegirard.util.Debug.d;


public class ClassReader
{
    public static List<String> getFieldsNames(Object o, boolean excludeStatic)
    {
        List<String> out = new ArrayList<>();
        for (Field field : getFields(o, excludeStatic))
        {
            String fieldName = field.getName();
            out.add(fieldName);
        }
        return out;
    }


    public static List<Field> getFields(Object o, boolean excludeStatic)
    {
        Class<?> clazz = o.getClass();
        List<Field> out = new ArrayList<>() ;

        for (Field field : clazz.getDeclaredFields())
        {
            if (! (excludeStatic && java.lang.reflect.Modifier.isStatic(field.getModifiers())) )
                out.add(field);

            // some other useful methods :
//            f.getClass().isPrimitive() // to check if the type is int, float, double, char or boolean
//            java.lang.reflect.Modifier.isStatic(f.getModifiers()) // to check if the field is static
        }
        return out ;
    }


    public static List<Object> getFieldsValues(Object o, List<Field> fields)
    {
        List<Object> out = new ArrayList<>();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldValue = field.get(o);
                out.add(fieldValue);
            }
        } catch (IllegalAccessException illegalAccessException) {
            d(illegalAccessException);
        }
        return out;
    }


	/*public static Object getFieldValFromName(Object o, String field_name)
	{
		Class<?> c = o.getClass();

		try
		{
			Field f = c.getDeclaredField(field_name);
			f.setAccessible(true);

			Object val = f.get(o);

			return val ;
		}

		catch (Exception ex)
		{
			ex.printStackTrace();
			return null ;
		}

	}*/

}
