# Groovy json

- When using groovy's built in json parsing this
  adds Service Loader that can load groovy.json.JsonGenerator.Converter in other packages.
- When using Jackson adds module to support groovy better with GStrings
  see `JacksonUtil` and `ObjectMapperWrapper` for how its is setup using findAndRegisterModules

