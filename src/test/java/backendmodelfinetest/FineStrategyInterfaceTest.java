package backendmodelfinetest;



import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

class FineStrategyInterfaceTest {

    @Test
    void testFineStrategyInterface_ExistsAndHasCorrectMethod() throws Exception {
        Class<?> interfaceClass = Class.forName("backend.model.fine.FineStrategy");

        // 1. التأكد إنها interface
        assertTrue(interfaceClass.isInterface(), "FineStrategy should be an interface");

        // 2. التأكد إن فيها دالة calculateFine
        Method method = interfaceClass.getDeclaredMethod("calculateFine", int.class);

        // 3. التأكد إن الدالة ترجع double
        assertEquals(double.class, method.getReturnType(), 
                     "calculateFine should return double");

        // 4. التأكد إن الدالة تأخذ int كمعامل
        Class<?>[] parameterTypes = method.getParameterTypes();
        assertEquals(1, parameterTypes.length, "calculateFine should have exactly one parameter");
        assertEquals(int.class, parameterTypes[0], "calculateFine parameter should be int");
    }
}