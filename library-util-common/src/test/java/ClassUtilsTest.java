import com.dxy.library.util.common.ClassUtils;
import com.dxy.library.util.common.SystemUtils;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author duanxinyuan
 * 2019/2/15 17:47
 */
public class ClassUtilsTest {

    @Test
    public void instantiateClass() {
        Object object = ClassUtils.instantiateClass(GameInfo.class.getName());
        System.out.println(object);
        GameInfo gameInfo = ClassUtils.instantiateClass(GameInfo.class);
        System.out.println(gameInfo);
    }

    @Test
    public void isAssignableFrom1() {
        System.out.println(ClassUtils.isPrimitiveOrWrapper(HashMap.class));
        System.out.println(ClassUtils.isPrimitiveOrWrapper(int.class));
        System.out.println(ClassUtils.isPrimitiveOrWrapper(Character.class));
        System.out.println(ClassUtils.isPrimitiveOrWrapper(Enum.class));
        System.out.println(ClassUtils.isPrimitiveOrWrapper(Date.class));
        System.out.println(ClassUtils.isPrimitiveOrWrapper(BigInteger.class));
        System.out.println(ClassUtils.isPrimitiveOrWrapper(BigDecimal.class));
    }

    @Test
    public void isAssignableFrom() {
        System.out.println(ClassUtils.isAssignable(HashMap.class, Map.class));
    }

    @Test
    public void loadClass() throws ClassNotFoundException {
        Class<?> aClass = ClassUtils.getClass(SystemUtils.class.getName());
        System.out.println(aClass.getName());
    }

    @Test
    public void getClasses() throws IOException, ClassNotFoundException {
        String name = SystemUtils.class.getPackage().getName();
        Set<Class<?>> classes = ClassUtils.getClasses(name);
        for (Class<?> aClass : classes) {
            System.out.println(aClass.getName());
        }
    }

}
