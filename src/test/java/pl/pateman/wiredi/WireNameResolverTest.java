package pl.pateman.wiredi;

import org.junit.Test;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class WireNameResolverTest {

    @Test
    public void shouldReturnCorrectWireNameForFieldWithoutAnnotation() throws NoSuchFieldException {
        Field field = WireNameResolverTestSimpleClass.class.getField("fieldWithoutAnnotation");

        String name = WireNameResolver.resolve(field);

        assertEquals("java.lang.String", name);
    }

    @Test
    public void shouldReturnCorrectWireNameForAnnotatedField() throws NoSuchFieldException {
        Field fieldWithAnnotation = WireNameResolverTestSimpleClass.class.getField("fieldWithAnnotation");

        String name = WireNameResolver.resolve(fieldWithAnnotation);

        assertEquals("annotated_field", name);
    }

    @Test
    public void shouldReturnCorrectWireNameForUnannotatedSetter() throws NoSuchMethodException {
        Method method = WireNameResolverTestSimpleClass.class.getMethod("setterOne", long.class);

        String name = WireNameResolver.resolve(method);

        assertEquals("long", name);
    }

    @Test
    public void shouldReturnCorrectWireNameForAnnotatedSetterWithoutName() throws NoSuchMethodException {
        Method method = WireNameResolverTestSimpleClass.class.getMethod("setterTwo", String.class);

        String name = WireNameResolver.resolve(method);

        assertEquals("java.lang.String", name);
    }

    @Test
    public void shouldReturnCorrectWireNameForAnnotatedSetter() throws NoSuchMethodException {
        Method method = WireNameResolverTestSimpleClass.class.getMethod("setterThree", String.class);

        String name = WireNameResolver.resolve(method);

        assertEquals("setter_three", name);
    }

    @Test
    public void shouldReturnCorrectWireNameForAnnotatedSetterParameter() throws NoSuchMethodException {
        Method method = WireNameResolverTestSimpleClass.class.getMethod("setterFour", String.class);

        String name = WireNameResolver.resolve(method);

        assertEquals("testValue", name);
    }

    @Test
    public void shouldReturnCorrectWireNameForAnnotatedSetterParameterWithoutName() throws NoSuchMethodException {
        Method method = WireNameResolverTestSimpleClass.class.getMethod("setterFive", String.class);

        String name = WireNameResolver.resolve(method);

        assertEquals("java.lang.String", name);
    }

    @Test
    public void shouldReturnCorrectWireNameForAnnotationArray() throws NoSuchMethodException {
        Method method = WireNameResolverTestSimpleClass.class.getMethod("setterFour", String.class);
        Annotation[] annotations = method.getParameters()[0].getAnnotations();

        String name = WireNameResolver.resolve(String.class, annotations);

        assertEquals("testValue", name);
    }

    @Test
    public void shouldReturnCorrectWireNameForAnnotationArrayWithEmptyName() throws NoSuchMethodException {
        Method method = WireNameResolverTestSimpleClass.class.getMethod("setterFive", String.class);
        Annotation[] annotations = method.getParameters()[0].getAnnotations();

        String name = WireNameResolver.resolve(String.class, annotations);

        assertEquals("java.lang.String", name);
    }

    @Test
    public void shouldReturnCorrectWireNameForEmptyAnnotationArray() throws NoSuchMethodException {
        Method method = WireNameResolverTestSimpleClass.class.getMethod("setterFive", String.class);
        Annotation[] annotations = new Annotation[0];

        String name = WireNameResolver.resolve(String.class, annotations);

        assertEquals("java.lang.String", name);
    }

    @Test
    public void shouldReturnCorrectWireNameForAnnotationArrayWithNoSuitableAnnotations() throws NoSuchMethodException {
        Method method = WireNameResolverTestSimpleClass.class.getMethod("setterSix", String.class);
        Annotation[] annotations = method.getParameters()[0].getAnnotations();

        String name = WireNameResolver.resolve(String.class, annotations);

        assertEquals("java.lang.String", name);
    }

    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface WireNameResolverTestAnno {

    }

    private class WireNameResolverTestSimpleClass {
        public String fieldWithoutAnnotation;

        @Wire(name = "annotated_field")
        public String fieldWithAnnotation;

        public void setterOne(long testValue) {

        }

        @Wire
        public void setterTwo(String testValue) {

        }

        @Wire(name = "setter_three")
        public void setterThree(String testValue) {

        }

        public void setterFour(@WireName("testValue") String testValue) {

        }

        public void setterFive(@WireName("") String testValue) {

        }

        public void setterSix(@WireNameResolverTestAnno String testValue) {

        }
    }

}