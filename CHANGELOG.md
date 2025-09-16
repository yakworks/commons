### v3.18

[Full Changelog](https://github.com/yakworks/commons/compare/v3.17...v3.18)
- fix nexusPublishing [link](https://github.com/yakworks/commons/commit/fe85d2c65526415829200f5aeeb203e6ade9961a)
- release [link](https://github.com/yakworks/commons/commit/dca8c8fd40a5464a2a50c3ef99b7e31e587ecee2)
- Merge pull request #29 from yakworks/dev [link](https://github.com/yakworks/commons/commit/83e15b079f4bba675efc1444b7720e301a2deaeb)
- add call to MsgServiceRegistry.service?.get(msg) when generating the Violation AsMap [link](https://github.com/yakworks/commons/commit/37dfb0e6fe09c0778a04e6eed7abc2eef6bad377)
- add the toMap method [link](https://github.com/yakworks/commons/commit/4fb490fded986a4abeb2130b5c32da68ef69f0c2)

### v3.17

[Full Changelog](https://github.com/yakworks/commons/compare/v3.16...v3.17)
- Merge branch 'master' of https://github.com/yakworks/commons [link](https://github.com/yakworks/commons/commit/b2a395236dc8ad7a8d4bee08d0cad3b7229f7436)
- release [link](https://github.com/yakworks/commons/commit/6755f2a7233dd95d11d932f2658ae0bf2095ff05)
- Merge pull request #27 from yakworks/dev [link](https://github.com/yakworks/commons/commit/2d0f47250f2cec9a960447c65e575d8148118e37)
- move nowAppZone extension here. Allow to set a default application timeZone different than the system timezone that would normally be Zulu on server. (#26) [link](https://github.com/yakworks/commons/commit/8d0d6a2c52a2cff9345b514cf475bac1a7e5b7c7)

### v3.16

[Full Changelog](https://github.com/yakworks/commons/compare/v3.15...v3.16)
- release [link](https://github.com/yakworks/commons/commit/ee7120b2292d6715a61a38ed68e618d5b660e158)
- remove class from MetaMap, make sure we dont use proxies. (#25) [link](https://github.com/yakworks/commons/commit/0c111d41c4eb9faa45194929a532b45e4332ddd5)

### v3.15

[Full Changelog](https://github.com/yakworks/commons/compare/v3.14...v3.15)
- release 3.15 [link](https://github.com/yakworks/commons/commit/a240626fc716a0075f522429ea251c2523e0f6a0)
- Merge pull request #24 from yakworks/dev [link](https://github.com/yakworks/commons/commit/cd2e0efcfe2727fee05d9970a879744f5892915c)
- remove the circular hydrate reference [link](https://github.com/yakworks/commons/commit/9c48b85f931e97738658324dd29892bcfc8e99dd)
- fix for the different grails PagedResultList classes [link](https://github.com/yakworks/commons/commit/d79951bbaa20c963ad17921ece34ff51bbd6f243)
- change handlebarsInstance to double locked (#23) [link](https://github.com/yakworks/commons/commit/4fd6d8cb5596c5d197f4a60ac1e5b7ff189d2148)
- MetaMap serialize josh (#22) [link](https://github.com/yakworks/commons/commit/3d7e75945930a2afaafa626a6d5283d97e5cd2a8)
- Add Maps.remove helper (#21) [link](https://github.com/yakworks/commons/commit/8e03df37d114b5eaf138e438ce89329b115063ec)
- Merge branch 'master' into dev [link](https://github.com/yakworks/commons/commit/724507f5c3fc81bfd65304d5d0c710035e873fe3)
- Add helper to get private field value (#18) [link](https://github.com/yakworks/commons/commit/47992dfe3aea270c8cfa8478ed510dbea9bf9bf1)

### v3.14

[Full Changelog](https://github.com/yakworks/commons/compare/v3.13...v3.14)
- trigger [link](https://github.com/yakworks/commons/commit/d0a32417f71e3f5b7ee53f98d4e405af642b8ba1)
- trigger release with new GPG key signing [link](https://github.com/yakworks/commons/commit/5d8c29c91e1659058e8f3904750ddd8f53484148)
- release [link](https://github.com/yakworks/commons/commit/c21257ed94f15da576f67112d27c1df20b98efae)
- Add helper to get private field value (#18) (#19) [link](https://github.com/yakworks/commons/commit/557f6d4ff443bdad0678b69b29f92f73f4f5a70a)

### v3.13

[Full Changelog](https://github.com/yakworks/commons/compare/v3.12...v3.13)
- lazy map bug with _ delimiter and sub-map(#16) [link](https://github.com/yakworks/commons/commit/07caa9a317e0069f59c1ff978c6259e1afad5ab4)

### v3.12

[Full Changelog](https://github.com/yakworks/commons/compare/v3.11.1...v3.12)
- release 3.12 [link](https://github.com/yakworks/commons/commit/fc5145f58310a9ecfdac32e5dcc0ccaccb63ed98)
- Merge pull request #15 from yakworks/dev [link](https://github.com/yakworks/commons/commit/86c78676dacfc9693e4a4af1487dd66b67d3b7b2)
- add regex sandbox test [link](https://github.com/yakworks/commons/commit/46e9d5459d66fe536c74de9fc48fcba19e37b41e)
- Merge branch 'release/3.11.x' into dev [link](https://github.com/yakworks/commons/commit/38a67f04e4b5b88be5a48a389302948c99fa253a)
- tweaks for nested LazyPathKeyMap [link](https://github.com/yakworks/commons/commit/c7f890bb51fcd3268596d045d25fdfcc2b513601)
- New DataProblem keys, LazyPathKeyMap, Map utils and setValue for path… (#14) [link](https://github.com/yakworks/commons/commit/e3976549c46e254e0406353854590316ca572a08)

### v3.11.1

[Full Changelog](https://github.com/yakworks/commons/compare/v3.11...v3.11.1)
- release [link](https://github.com/yakworks/commons/commit/3e51b53112861f01fe25b1275b37d3d5e99da8f0)
- fix up the Bars.java static initializer, was calling a get. move into method so can be reused if needed [link](https://github.com/yakworks/commons/commit/52e2cf6a4ca7a72691a67b5f42a38af76c910df6)
- fix comments on the Bars class, point to docs. [link](https://github.com/yakworks/commons/commit/9f595091b00e44c8a8e669cd8a4f2f1039124731)

### v3.11

[Full Changelog](https://github.com/yakworks/commons/compare/v3.10...v3.11)
- Handlebars (#13) [link](https://github.com/yakworks/commons/commit/8a4e14783b4dc37f0313703e20751e1257e77452)
- 9ci/domain9#1377 Fix pathkeymap to support list of elements which are… (#12) [link](https://github.com/yakworks/commons/commit/dbbe14a36efd9cd77115ed6dbc890c319825025a)

### v3.10

[Full Changelog](https://github.com/yakworks/commons/compare/v3.9...v3.10)
- Binder with Jackson, new BeanTools and JacksonJson utils.  (#11) [link](https://github.com/yakworks/commons/commit/ef64861e660c155e6a771e52467ac67f61d6da2f)

### v3.9

[Full Changelog](https://github.com/yakworks/commons/compare/v3.8...v3.9)
- add springframework.utils for reflection so we can use the nice features without full dependecy on the framework. [link](https://github.com/yakworks/commons/commit/c665cad14778515edd79a4f47af514b9612dde43)
- copy in springframework.util for reflection and calss utils add mkdirs and delete to the path object add setFieldValue that can be used to set privates and finals for those pesky situations. [link](https://github.com/yakworks/commons/commit/9f362b2172ae2bab1d9e68a249e7e6683017d48e)

### v3.8

[Full Changelog](https://github.com/yakworks/commons/compare/v3.7...v3.8)
- change gradle.projectDir and gradle.rootProjectDir to project prefix … (#10) [link](https://github.com/yakworks/commons/commit/7823b02f84dc7b1126c0d4c171c9b1cc10f0e931)

### v3.7

[Full Changelog](https://github.com/yakworks/commons/compare/v3.6...v3.7)
- enable simple streaming for basic types, enhance metaentity, add test… (#9) [link](https://github.com/yakworks/commons/commit/6b6dc0284d1eb277eb0a8d74a8d656867b12337f)

### v3.6

[Full Changelog](https://github.com/yakworks/commons/compare/v3.5...v3.6)
- MetaEntity prop name consistent,  PathKeyMap, JsonEngine thread safe, (#8) [link](https://github.com/yakworks/commons/commit/1a49b765ede5a3769249c16518686e970bc3ee3f)

### v3.5

[Full Changelog](https://github.com/yakworks/commons/compare/v3.4...v3.5)
- release [link](https://github.com/yakworks/commons/commit/e3d10afba2a6c04a30bcf1b37ffeeb982e0d3454)
- fix findGenericTypeForCollection to use clazz.getDeclaredMethods() (#6) [link](https://github.com/yakworks/commons/commit/4c806c5c0187f838062176a65e7d4db2e3887bce)

### v3.4

[Full Changelog](https://github.com/yakworks/commons/compare/v3.3...v3.4)
- Metamap and centralized json into groovy-commons (#5) [link](https://github.com/yakworks/commons/commit/d6a5e92dca4a4d96f8310fc03366504d71fd20dc)

### v3.3

[Full Changelog](https://github.com/yakworks/commons/compare/v3.2...v3.3)
- cleanup, change ApiResults to use list property (#4) [link](https://github.com/yakworks/commons/commit/e1ad2fc43b4b86f409b565adb55751c7a5d1bb89)

### v3.2

[Full Changelog](https://github.com/yakworks/commons/compare/v3.1...v3.2)
- trigger release [link](https://github.com/yakworks/commons/commit/632d808643fb220a2f4e8e38a2d002e78cda0fd3)
- Move problem kotlin3 factory (#3) [link](https://github.com/yakworks/commons/commit/59910d3bbd7e46055e22090cb52ecd00fe272296)

### v3.1

Initial Release
