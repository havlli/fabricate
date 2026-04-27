package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class DevOpsTest {

    @Test
    void semver_isThreeNumericComponents() {
        DevOps dev = new DevOps(Rng.seeded(0L));

        for (int i = 0; i < 30; i++) {
            assertThat(dev.semver()).matches("\\d+\\.\\d+\\.\\d+");
        }
    }

    @Test
    void semverPreRelease_appendsTagAndNumber() {
        DevOps dev = new DevOps(Rng.seeded(1L));

        for (int i = 0; i < 30; i++) {
            assertThat(dev.semverPreRelease()).matches("\\d+\\.\\d+\\.\\d+-(alpha|beta|rc)\\.\\d");
        }
    }

    @Test
    void gitSha_is40HexChars() {
        DevOps dev = new DevOps(Rng.seeded(2L));

        for (int i = 0; i < 30; i++) {
            assertThat(dev.gitSha()).matches("[0-9a-f]{40}");
        }
    }

    @Test
    void gitShaShort_is7HexChars() {
        DevOps dev = new DevOps(Rng.seeded(3L));

        for (int i = 0; i < 30; i++) {
            assertThat(dev.gitShaShort()).matches("[0-9a-f]{7}");
        }
    }

    @Test
    void environment_isFromCuratedList() {
        DevOps dev = new DevOps(Rng.seeded(4L));

        for (int i = 0; i < 30; i++) {
            assertThat(dev.environment()).matches("[a-z]+");
        }
    }

    @Test
    void logLevel_isValidLevel() {
        DevOps dev = new DevOps(Rng.seeded(5L));

        for (int i = 0; i < 30; i++) {
            assertThat(dev.logLevel()).isIn("TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL");
        }
    }

    @Test
    void httpMethod_isStandardMethod() {
        DevOps dev = new DevOps(Rng.seeded(6L));

        for (int i = 0; i < 30; i++) {
            assertThat(dev.httpMethod()).isIn("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS");
        }
    }

    @Test
    void httpStatus_isInValidRange() {
        DevOps dev = new DevOps(Rng.seeded(7L));

        for (int i = 0; i < 30; i++) {
            int code = dev.httpStatus();
            assertThat(code).isBetween(100, 599);
        }
    }

    @Test
    void branchName_hasPrefixAndTopic() {
        DevOps dev = new DevOps(Rng.seeded(8L));

        for (int i = 0; i < 30; i++) {
            assertThat(dev.branchName()).matches("[a-z]+/[a-z\\-]+");
        }
    }

    @Test
    void cloudRegion_matchesAwsShape() {
        DevOps dev = new DevOps(Rng.seeded(9L));

        for (int i = 0; i < 30; i++) {
            assertThat(dev.cloudRegion()).matches("(us|eu|ap|sa|ca)-[a-z]+-\\d");
        }
    }

    @Test
    void podName_hasResourceHashAndSuffix() {
        DevOps dev = new DevOps(Rng.seeded(10L));

        for (int i = 0; i < 30; i++) {
            assertThat(dev.podName()).matches("[a-z]+-[0-9a-f]{10}-[a-z]{5}");
        }
    }

    @Test
    void namespace_hasPrefixAndService() {
        DevOps dev = new DevOps(Rng.seeded(11L));

        for (int i = 0; i < 30; i++) {
            assertThat(dev.namespace()).matches("[a-z]{2,4}-[a-z]+");
        }
    }

    @Test
    void dockerImage_hasRegistryPathAndTag() {
        DevOps dev = new DevOps(Rng.seeded(12L));

        for (int i = 0; i < 30; i++) {
            assertThat(dev.dockerImage()).matches("registry\\.io/[a-z]+/[a-z]+:\\d+\\.\\d+\\.\\d+");
        }
    }

    @Test
    void latencyMs_isPositive() {
        DevOps dev = new DevOps(Rng.seeded(13L));

        for (int i = 0; i < 100; i++) {
            assertThat(dev.latencyMs()).isPositive().isLessThan(20_000L);
        }
    }
}
