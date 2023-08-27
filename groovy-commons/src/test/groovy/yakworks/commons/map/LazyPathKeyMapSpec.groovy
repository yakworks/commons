package yakworks.commons.map

import spock.lang.Specification

class LazyPathKeyMapSpec extends Specification {

    LazyPathKeyMap getSample(){
        Map sub = [:]
        sub.put("name", "Dierk Koenig")
        sub.put("dob", "01/01/1970")
        sub.put("address.postCode", "345435")
        sub.put("address.town", "Swindon")
        sub.put("nested", new LazyPathKeyMap(['foo.bar': 'baz']))

        List nestedList = []
        nestedList << new LazyPathKeyMap(['foo.bar': 'baz'])
        nestedList << new LazyPathKeyMap(['foo.bar': 'baz'])
        sub.put("nestedList", nestedList)

        return LazyPathKeyMap.of(sub)
    }

    void "test variations"() {

        when:
        LazyPathKeyMap theMap = getSample()

        then:
        //theMap['name', 'dob'] == [name:"Dierk Koenig", dob:"01/01/1970"]
        theMap['name'] == "Dierk Koenig"
        theMap['dob'] == "01/01/1970"
        theMap.address.postCode == "345435"
        theMap.address.town == "Swindon"
        theMap.nested.foo.bar == 'baz'
        theMap.nestedList[0].foo.bar == 'baz'
        theMap.nestedList[1].foo.bar == 'baz'
    }

    void "test cloneMap"() {

        when:
        LazyPathKeyMap initial = getSample()
        LazyPathKeyMap theMap = initial.clone()

        then:
        theMap['name'] == "Dierk Koenig"
        theMap['dob'] == "01/01/1970"
        theMap.address.postCode == "345435"
        theMap.address.town == "Swindon"
        theMap.nested.foo.bar == 'baz'
        theMap.nestedList[0].foo.bar == 'baz'
        theMap.nestedList[1].foo.bar == 'baz'
    }

    void "test basic"() {
        given:
        //order scambled on purpose
        Map sub = [
            "a.b.c": "cValue",
            "a.b.d": "dValue",
        ]

        when:
        LazyPathKeyMap theMap = LazyPathKeyMap.of(sub)

        then:
        !theMap.containsKey("a.b.c")

        Map aMap = theMap['a']
        aMap instanceof Map
        aMap.b instanceof Map
        aMap.b.c == "cValue"
        aMap.b.d == "dValue"
    }

    void "test flat params"() {
        given:
        //order scambled on purpose
        Map sub = [
            "a.b.c": "cValue",
            "a.bc": "bcValue",
            "a.e.f": "fValue",
            "a.b.d": "dValue",
            "a.e.g": "gValue",
            "x.y" : "yValue",
            "a.b.e": "abeValue",
        ]

        when:
        LazyPathKeyMap theMap = LazyPathKeyMap.of(sub)

        then:
        !theMap.containsKey("a.b.c")

        Map aMap = theMap['a']
        aMap instanceof Map
        aMap.b instanceof Map

        !aMap.containsKey('b.c')
        aMap.b.c == "cValue"
        aMap.b.d == "dValue"
        aMap.bc == "bcValue"

        theMap.a['e'] instanceof Map
        theMap.a.e.f == "fValue"
        theMap.a.e.g == "gValue"
        theMap.x.y == "yValue"
        !theMap.containsKey("x.y")
    }

    //This works but seems like it should not, see trip x
    void "test key conflicts"() {
        when:
        Map sub = [
            "a.b.c": "abcValue",
            "a.c": "acValue",
            //This one should make it fail since it conflicts with a.b.c
            "a.b": "abValue"
        ]
        LazyPathKeyMap theMap = LazyPathKeyMap.of(sub)
        then:
        theMap.a.b == "abValue"
    }

    void "test key conflicts order dictates what wins"() {
        when:
        Map sub = [
            "a.b": "abValue",
            "a.c": "acValue",
            "a.b.c": "abcValue",
        ]
        LazyPathKeyMap theMap = LazyPathKeyMap.of(sub)
        assert theMap

        then:
        theMap.a.b.c == "abcValue"
        // IllegalStateException ex = thrown()
        // ex.message == "Bad keys, expecting a map for path key b.c"
    }

    void "test merge when both path keys and map"() {
        when:
        Map sub = [
            "a.b": "abValue",
            a: [
                c: "acValue"
            ]
        ]
        LazyPathKeyMap theMap = LazyPathKeyMap.of(sub)

        then:
        theMap.keySet().size() == 1
        theMap.a.keySet().size() == 2

        theMap.a == [b: "abValue", c: "acValue"]

    }

    void "test with both path keys and initial map"() {
        when: "map comes first"
        LazyPathKeyMap theMap2 = LazyPathKeyMap.of([
            a: [
                c: "acValue"
            ],
            "a.b.d": "abdValue"
        ])

        then:
        theMap2.keySet().size() == 1
        theMap2.a.keySet().size() == 2
        theMap2.a.b.keySet().size() == 1

        theMap2.a == [c: 'acValue', b:[d:'abdValue']]
    }

    void "test with dup key"() {
        when: "map comes first"
        LazyPathKeyMap theMap2 = LazyPathKeyMap.of([
            "a.c": "acValue2",
            a: [
                c: "acValue"
            ]
        ])

        then:
        theMap2.keySet().size() == 1
        theMap2.a.keySet().size() == 1

        theMap2.a == [c: 'acValue']
    }

    void "test with dup key, map first"() {
        when: "map comes first"
        LazyPathKeyMap m = LazyPathKeyMap.of([
            a: [
                c: "acValue"
            ],
            "a.c": "acValue2"
        ])

        then:
        m.keySet().size() == 1
        m.a.keySet().size() == 1

        m.a == [c: 'acValue2']

        when:
        m.a = [c:'foo']
        then:
        m.a.c == 'foo'

        when:
        m.a.c = 'foo2'
        then:
        m.a.c == 'foo2'

        // when:
        // m['a.c'] = 'foo3'
        // then:
        // m.a.c == 'foo3'
        // m.keySet().size() == 1
        // m.a.keySet().size() == 1
        //
        // when:
        // m['a.c'] = [d: 'foo4']
        // then:
        // m.a.c.d == 'foo4'
        // m.keySet().size() == 1
        // m.a.keySet().size() == 1
    }

    void "test multi dimensional map with different delim"() {
        given:
        Map sub = [
            "a_b_c": "abcValue",
            //this should fail it.
            //"a_b": "bValue",
            "a_bc": "abcValue2",
            "a_b_d": "abdValue",
            "a_e_f": "fValue",
            "a_e_g": "gValue"
        ]

        when:
        LazyPathKeyMap theMap = LazyPathKeyMap.of(sub, "_")
        Map aMap = theMap['a']
        Map bMap = aMap['b']

        then:
        aMap instanceof Map
        aMap.keySet().size() == 3
        bMap.keySet().size() == 2
        //theMap.a.b == "abValue"
        theMap.a.b.c == "abcValue"
        theMap.a.b.d == "abdValue"

        theMap.a.bc == "abcValue2"

        assert theMap.a['e'] instanceof Map
        assert theMap.a.e.f == "fValue"
        assert theMap.a.e.g == "gValue"
    }

    void "test plus opperator works on it"() {
        given:
        Map m = ["album": "Foxtrot"]
        def originalMap =  LazyPathKeyMap.of(m)

        when:
        def newMap = originalMap + [vocalist: 'Peter']

        then:
        originalMap.containsKey('album')
        !originalMap.containsKey('vocalist')
        newMap.containsKey('album')
        newMap.containsKey('vocalist')
    }

    void testNestedKeyAutoGeneration() {
        given:
        def params = LazyPathKeyMap.of([:])

        when:
        params.'company.department.team.numberOfEmployees' = 42
        params.'company.department.numberOfEmployees' = 2112
        def firstKey = 'alpha'
        def secondKey = 'beta'
        params."${firstKey}.${secondKey}.foo" = 'omega'
        params.put "prefix.${firstKey}.${secondKey}", 'delta'

        def company = params.company

        then:
        assert company instanceof Map

        when:
        def department = company.department

        then:
        assert department instanceof Map
        assert department.numberOfEmployees == 2112

        when:
        def team = department.team

        then:
        assert team instanceof Map
        assert team.numberOfEmployees == 42

        assert params['alpha'] instanceof Map
        assert params['alpha']['beta'] instanceof Map
        assert params['alpha']['beta'].foo == 'omega'

        assert params['prefix'] instanceof Map
        assert params['prefix']['alpha'] instanceof Map
        assert params['prefix']['alpha'].beta == 'delta'
    }

    void "test cloning"() {
        Map sub = ["name":"Dierk Koenig", "address.postCode": "345435", "dob": "01/01/1970"]
        LazyPathKeyMap theMap =  LazyPathKeyMap.of(sub)

        when:
        Map theClone = theMap.clone()

        then:
        theMap.size() == theClone.size()
        theMap.each { k, v ->
            assert theMap[k] == theClone[k], "theclone should have the same value for $k as the original"
        }
    }

    void "test when value contains simple collection"() {
        setup:
        Map sub = ["name":"Dierk Koenig",  "address.postCode": "345435", "tags": [1,2,3]]
        LazyPathKeyMap theMap =  LazyPathKeyMap.of(sub)

        when: "contains a tags collection of non PathKeyMap type elements"
        LazyPathKeyMap theClone = theMap.clone()

        then:
        noExceptionThrown()
        theMap.size() == theClone.size()
        theClone["address"]["postCode"] == "345435"
        theClone.tags == [1,2,3]

    }


}
