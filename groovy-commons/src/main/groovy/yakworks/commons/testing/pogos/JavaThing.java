package yakworks.commons.testing.pogos;

import yakworks.commons.model.Named;

import java.time.LocalDate;

public class JavaThing implements Named {
    private Long id;
    private String name;
    private LocalDate localDate;
    private static String someStatic = "foo";

    public String getSomeGetter() {
        return "x";
    }

    public static JavaThing of(Long id, String name) {
        JavaThing thing = new JavaThing();
        thing.setName(name);
        thing.setId(id);
        return thing;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public static String getSomeStatic() {
        return someStatic;
    }

    public static void setSomeStatic(String someStatic) {
        JavaThing.someStatic = someStatic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
