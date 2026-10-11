# Releasing fabricate

This project publishes to **Maven Central** via the
[Central Publisher Portal](https://central.sonatype.com/) using
`central-publishing-maven-plugin`.

## One-time setup

1. **Sonatype Central account** — the `dev.havlli` namespace was verified
   in the Central Publisher Portal via a DNS TXT record on `havlli.dev`
   on 2026-10-11.

2. **GPG signing key** — use a signing key whose public key is available
   from a keyserver. Key generation and public-key upload require separate
   authorization. Select the signing key with `-Dgpg.keyname=<KEY_ID>`.

   The release profile uses non-interactive signing with loopback pinentry
   (`--pinentry-mode loopback`). Supply the passphrase only through the
   runtime environment variable `MAVEN_GPG_PASSPHRASE`, which
   maven-gpg-plugin reads by default. Do not put a passphrase in command
   arguments, the POM, settings files, logs, or version control.

3. **Maven settings** — configure the `central` server id in your local
   `~/.m2/settings.xml`. This example contains placeholders only:

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
   Keep the actual settings file outside version control.

## Cutting a release

Publishing and tagging require separate approval. Confirm the working tree
is clean and `pom.xml` contains the intended non-SNAPSHOT version (`0.1.0`
for this release), then follow this order:

1. Run the unsigned release dry run:

   ```bash
   mise exec -- mvn -B -ntp -P release -Dgpg.skip verify
   ```

   Check the main jar, sources jar, and javadoc jar in `target/`.
   This command stops at verification and does not upload artifacts.

2. After publishing approval, supply the signing passphrase through
   `MAVEN_GPG_PASSPHRASE` and deploy with your signing key id:

   ```bash
   mise exec -- mvn -B -ntp -P release -Dgpg.keyname='<KEY_ID>' deploy
   ```

   Replace `<KEY_ID>` with your key id before running the command. The
   release profile attaches sources, javadoc, and GPG signatures, then
   uploads through the `central` server. With `autoPublish=false` and
   `waitUntil=validated`, the deployment stops in **VALIDATED** state.

3. Inspect the deployment and all artifacts in the
   [Central Publisher Portal](https://central.sonatype.com/publishing/deployments).

4. Publish the inspected deployment using the Portal's **Publish** button.
   Confirm publication succeeds before tagging.

5. After tagging approval, tag the released commit and push the tag:

   ```bash
   git tag -s v0.1.0 -m "release 0.1.0"
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
  <groupId>dev.havlli</groupId>
  <artifactId>fabricate</artifactId>
  <version>0.1.0</version>
</dependency>
```

```java
System.out.println(dev.havlli.fabricate.Fake.fullName());
```

If the artifact resolves and the program prints a name, the release is
healthy.
