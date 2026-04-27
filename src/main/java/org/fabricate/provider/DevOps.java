package org.fabricate.provider;

import java.util.List;
import org.fabricate.random.Rng;

/**
 * DevOps primitives: semver, git SHAs, environments, log levels, HTTP status, k8s names.
 *
 * {@snippet :
 * String v   = fab.devops().semver();          // "2.7.3"
 * String sha = fab.devops().gitSha();          // 40 hex chars
 * String env = fab.devops().environment();     // "staging"
 * int status = fab.devops().httpStatus();      // 200, 404, 503, ...
 * }
 */
public final class DevOps {

    private static final List<String> ENVIRONMENTS = List.of(
            "production", "staging", "development", "test", "qa", "preview", "canary", "sandbox");

    private static final List<String> LOG_LEVELS = List.of(
            "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL");

    private static final List<String> HTTP_METHODS = List.of(
            "GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS");

    private static final List<Integer> HTTP_STATUSES = List.of(
            200, 201, 202, 204,
            301, 302, 304,
            400, 401, 403, 404, 409, 410, 422, 429,
            500, 502, 503, 504);

    private static final List<String> BRANCH_PREFIXES = List.of(
            "feature", "fix", "chore", "hotfix", "refactor", "docs", "test");

    private static final List<String> BRANCH_TOPICS = List.of(
            "auth", "login", "signup", "checkout", "billing", "search", "dashboard",
            "profile", "notifications", "metrics", "deploy", "cache", "schema",
            "migration", "logging", "retry", "queue", "webhook", "rate-limit");

    private static final List<String> CLOUD_REGIONS = List.of(
            "us-east-1", "us-east-2", "us-west-1", "us-west-2",
            "eu-west-1", "eu-west-2", "eu-west-3", "eu-central-1", "eu-north-1",
            "ap-south-1", "ap-southeast-1", "ap-southeast-2", "ap-northeast-1", "ap-northeast-2",
            "sa-east-1", "ca-central-1");

    private static final List<String> K8S_RESOURCES = List.of(
            "api", "web", "worker", "scheduler", "ingest", "egress", "auth", "billing",
            "search", "cache", "broker", "indexer", "frontend", "backend");

    private final Rng rng;
    private final Numbers numbers;
    private final Texts texts;

    public DevOps(Rng rng) {
        this.rng = rng;
        this.numbers = new Numbers(rng);
        this.texts = new Texts(rng);
    }

    /** A semantic version like {@code 1.4.7}. */
    public String semver() {
        return numbers.intBetween(0, 9) + "." + numbers.intBetween(0, 20) + "." + numbers.intBetween(0, 50);
    }

    /** A semantic version with optional pre-release identifier (e.g. {@code 1.4.7-beta.2}). */
    public String semverPreRelease() {
        String[] tags = {"alpha", "beta", "rc"};
        return semver() + "-" + tags[numbers.intBetween(0, tags.length - 1)] + "." + numbers.intBetween(1, 9);
    }

    /** A 40-character lowercase hex git commit SHA. */
    public String gitSha() {
        return texts.hex(40);
    }

    /** A 7-character abbreviated git SHA. */
    public String gitShaShort() {
        return texts.hex(7);
    }

    /** A typical deployment environment name. */
    public String environment() {
        return rng.pick(ENVIRONMENTS);
    }

    /** A log level token. */
    public String logLevel() {
        return rng.pick(LOG_LEVELS);
    }

    /** An HTTP method token. */
    public String httpMethod() {
        return rng.pick(HTTP_METHODS);
    }

    /** A common HTTP status code drawn from a curated list. */
    public int httpStatus() {
        return rng.pick(HTTP_STATUSES);
    }

    /** A branch name like {@code feature/auth-login}. */
    public String branchName() {
        return rng.pick(BRANCH_PREFIXES) + "/" + rng.pick(BRANCH_TOPICS) + "-" + texts.word();
    }

    /** A cloud region in AWS-style format like {@code eu-west-1}. */
    public String cloudRegion() {
        return rng.pick(CLOUD_REGIONS);
    }

    /** A Kubernetes-style pod name like {@code api-7c9d8f6b-xz4qm}. */
    public String podName() {
        return rng.pick(K8S_RESOURCES) + "-" + texts.hex(10) + "-" + lowerLetters(5);
    }

    /** A Kubernetes namespace like {@code prod-billing}. */
    public String namespace() {
        String env = rng.pick(ENVIRONMENTS);
        String prefix = env.length() >= 4 ? env.substring(0, 4) : env;
        return prefix + "-" + rng.pick(K8S_RESOURCES);
    }

    /** A Docker image tag like {@code registry.io/team/service:1.4.2}. */
    public String dockerImage() {
        return "registry.io/" + texts.word() + "/" + rng.pick(K8S_RESOURCES) + ":" + semver();
    }

    /** Latency in milliseconds, weighted toward fast responses. */
    public long latencyMs() {
        // log-uniform 1ms..10s
        int exp = numbers.intBetween(0, 13);
        long base = 1L << exp;
        return base + numbers.longBetween(0, base);
    }

    private String lowerLetters(int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append((char) ('a' + numbers.intBetween(0, 25)));
        }
        return sb.toString();
    }
}
