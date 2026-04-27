# Releasing fabricate

This project publishes to **Maven Central** via the
[Central Publisher Portal](https://central.sonatype.com/) using
`central-publishing-maven-plugin`.

## One-time setup

1. **Sonatype Central account** — sign up at
   <https://central.sonatype.com/> and verify the `io.github.havlli`
   namespace (already verified for this project).

2. **GPG signing key** — generate one if needed and publish the public
   key to a keyserver:

   ```bash
   gpg --gen-key
   gpg --keyserver keyserver.ubuntu.com --send-keys <KEY_ID>
   ```

3. **Maven settings** — add credentials to `~/.m2/settings.xml`:

   ```xml
   <settings>
     <servers>
       <server>
         <id>central</id>
         <username>YOUR_CENTRAL_TOKEN_USER</username>
         <password>YOUR_CENTRAL_TOKEN_PASSWORD</password>
       </server>
     </servers>
   </settings>
   ```

   Tokens are generated at <https://central.sonatype.com/account>.

## Cutting a release

1. Make sure the working tree is clean and tests pass:

   ```bash
   mise exec -- mvn -B verify
   ```

2. Confirm `pom.xml` has the version you want to release (no `-SNAPSHOT`).

3. Tag and deploy:

   ```bash
   git tag -s v$(mvn -q help:evaluate -Dexpression=project.version -DforceStdout) -m "release $(...)"
   mise exec -- mvn -B -Prelease deploy
   ```

   The `release` profile attaches the sources jar, javadoc jar, GPG
   signatures, and uploads via the central plugin.

4. Verify on the
   [Central Publisher portal](https://central.sonatype.com/publishing/deployments).
   The deployment lands in **VALIDATED** state — review the artifacts
   and click "Publish" to push it to Maven Central.

   (Once a few releases have gone smoothly, set `autoPublish=true` in
   `pom.xml` to skip the manual button.)

5. Push the tag:

   ```bash
   git push origin v0.1.0
   ```

6. Bump to the next snapshot for further development:

   ```bash
   # edit pom.xml: 0.1.0 → 0.1.1-SNAPSHOT
   git commit -am "back to dev: 0.1.1-SNAPSHOT"
   ```

## Post-release smoke test

Add the just-released coordinates to a throwaway project and verify
resolution works:

```xml
<dependency>
  <groupId>io.github.havlli</groupId>
  <artifactId>fabricate</artifactId>
  <version>0.1.0</version>
</dependency>
```

```java
System.out.println(org.fabricate.Fake.fullName());
```

If the artifact resolves and the program prints a name, the release is
healthy.
