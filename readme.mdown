<table><tr><td>

[![CircleCI](https://img.shields.io/circleci/project/github/yakworks/commons/master.svg?longCache=true&style=for-the-badge&logo=circleci)](https://circleci.com/gh/yakworks/commons) \
[![9ci](https://img.shields.io/badge/BUILT%20BY-9ci%20Inc-blue.svg?longCache=true&style=for-the-badge)](http://9ci.com) \
[![9ci](https://forthebadge.com/images/badges/gluten-free.svg)](http://9ci.com)

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

## Developer Notes

### Build and Tests

> While gradle is the build tool, `make` and [ship-kit](https://github.com/yakworks/shipkit) is used for consitency across projects and languages for versioning, docs and deployment. 

- run `make` to see help for the targets

- run `make check` to build and run all tests. same as `./gradlew check`
