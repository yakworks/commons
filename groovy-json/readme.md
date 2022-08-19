# Groovy json

## Additions to Groovy's built in JsonGenerator
- When using groovy's built in json parsing this
  adds ServiceLoader that can load groovy.json.JsonGenerator.Converter in other packages/jars if META-INF/services has the files setup. see example.
- Overrides Groovy's DefaultJsonGenerator so static props are not rendered. 
  the default fall through for a POGO or Object rendered static properties, the mods here ignore them by default as we found 90% of the time static
  are usually helpers and not intended to be rendered. PR's welcome to make it configurable. 

- When using Jackson adds module to support groovy better with GStrings
  see `JacksonUtil` and `ObjectMapperWrapper` for how its is setup using findAndRegisterModules

