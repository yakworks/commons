package yakworks.testing

import groovy.transform.CompileStatic

@CompileStatic
class AdminUser implements NameTrait{
    //String name
    String city
    Integer age
    String onlyAdmin = "foo"
    Thing thing
    List<Thing> things = [] as List<Thing>

    String getSomeGetter(){
        return "x"
    }

    @CompileStatic
    static class Thing {
        String name
    }

    static String someStatic = "some val"
}
