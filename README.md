# TaintAnalysis
This is a field sensitive taint analysis client implementation on top of Heros,
which uses Boomerang to resolve aliasing.

## Dependencies
Following dependencies must be built to run the analysis.
- Heros: https://github.com/Sable/heros
- BoomerangPDS: https://github.com/CodeShield-Security/SPDS
- PathExpressions: execute the `install_dependencies.sh` in `/dependencies` folder

## How to run
- Various test cases are listed under `test/target/taint`
- Run the test cases in `TaintAnalysisTest`
- sources and sinks are defined as `SootMethodRef`'s in `createAnalysisTransformer` method.

## Authenticating to GitHub Packages

To access the GitHub packages repository, you also need to set up GitHub credentials in your Maven's `settings.xml` file. Therefore, you need to add a `server` block with the id `github`, your username and an access token that has `package:read` rights to your `setting.xml`.
An in-depth documentation on how to do this can be found [here](https://docs.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages#authenticating-to-github-packages).


```xml 
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
        </repository>
        <repository>
          <id>github</id>
          <url>https://maven.pkg.github.com/CodeShield-Security/SPDS</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>USER</username>
      <password>TOKEN-USER</password>
    </server>
  </servers>
</settings>

```