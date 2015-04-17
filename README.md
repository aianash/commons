# Commons

## How to use ?

```bash
$ sbt compile
$ sbt commons-core/publish-local
```

Then add it to your dependencies as
```
"com.goshoplane" %% "commons-core" % "0.0.1"
```

Also make sure you have a local repository as resolver group
```
"Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"

```

*If you build commons the second time and publish it local, then
you will have to first clean the reposirtory where you have
used it as dependency, for the new changes to affect*