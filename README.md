<table><tr><td>

[![CircleCI](https://img.shields.io/circleci/project/github/yakworks/commons/master.svg?longCache=true&style=for-the-badge&logo=circleci)](https://circleci.com/gh/yakworks/commons) \
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.yakworks/groovy-commons/badge.svg?style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/org.yakworks/groovy-commons) \
[![9ci](https://img.shields.io/badge/BUILT%20BY-9ci%20Inc-blue.svg?longCache=true&style=for-the-badge)](http://9ci.com) \
[![9ci](https://img.shields.io/badge/GLUTEN-FREE-pink.svg?longCache=true&style=for-the-badge&logo=Atari)](http://9ci.com) \
[![Open Source](https://badges.frapsoft.com/os/v3/open-source.svg?v=103)](https://yak.works/)

</td>
<td>

<pre style="line-height: normal; background-color:#2b2929; color:#76ff00; font-family: monospace; white-space: pre; font-size: 10px">

              _.-````'-,_
          ,-'`           `'-.,_
  /)     (\       9ci's       '``-.
 ( ( .,-') )    Yak Works         ```,
  \ '   (_/                         !!
  |       /)           '           !!!
  ^\    ~'            '     !    !!!! 
    !      _/! , !   !  ! !  !   !!!   
    \Y,   |!!!  !  ! !!  !! !!!!!!!
      `!!! !!!! !!  )!!!!!!!!!!!!!
        !!  ! ! \( \(  !!!|/!  |/!
      /_(      /_(/_(    /_(  /_(   

</pre>
</td></tr></table>

## api-messages

`org.yakworks:api-messages:<<version>>`

- base library for the simplified MsgKey
- results and problem objects for classes. 

Kotlin is a dependency and is required for messages.
groovy is not required unless taking advantage of the traits and the problem api. 

## api-json

`org.yakworks:api-json:<<version>>`

- groovy json converters for Results and Problem api

## groovy-json

- When using groovy's built in json parsing this 
  adds Service Loader that can load groovy.json.JsonGenerator.Converter in other packages.
- When using Jackson adds module to support groovy better with GStrings
  see `JacksonUtil` and `ObjectMapperWrapper` for how its is setup using findAndRegisterModules

### Additions to Groovy's built in JsonGenerator

- When using groovy's built in json parsing this
  adds ServiceLoader that can load groovy.json.JsonGenerator.Converter in other packages/jars if META-INF/services has the files setup. see example.
- Overrides Groovy's DefaultJsonGenerator so static props are not rendered.
  the default fall through for a POGO or Object rendered static properties, the mods here ignore them by default as we found 90% of the time static
  are usually helpers and not intended to be rendered. PR's welcome to make it configurable.

### Jackson

- When using Jackson adds module to support groovy better with GStrings out of the box
  see `JacksonUtil` and `ObjectMapperWrapper` for how its is setup using findAndRegisterModules

## groovy-commons

groovy based utilities

## Developer Notes

### Build and Tests

> While gradle is the build tool, `make` and [ship-kit](https://github.com/yakworks/shipkit) is used for consitency across projects and languages for versioning, docs and deployment. 

- run `make` to see help for the targets

- run `make check` to build and run all tests. same as `./gradlew check`
