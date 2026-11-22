package dev.havlli.fabricate;

/**
 * A source of development and operations values, sharing its owning {@link Fabricate} provider.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} and must not implement this interface. Methods may be added
 * in future versions. Creating a generator consumes no random values.
 * Instances share their provider state and are not thread-safe.
 */
public interface DevOpsGen {

    /** A semantic version like {@code 1.4.7}. */
    String semver();

    /** A semantic version with optional pre-release identifier (e.g. {@code 1.4.7-beta.2}). */
    String semverPreRelease();

    /** A 40-character lowercase hex git commit SHA. */
    String gitSha();

    /** A 7-character abbreviated git SHA. */
    String gitShaShort();

    /** A typical deployment environment name. */
    String environment();

    /** A log level token. */
    String logLevel();

    /** An HTTP method token. */
    String httpMethod();

    /** A common HTTP status code drawn from a curated list. */
    int httpStatus();

    /** A branch name like {@code feature/auth-login}. */
    String branchName();

    /** A cloud region in AWS-style format like {@code eu-west-1}. */
    String cloudRegion();

    /** A Kubernetes-style pod name like {@code api-7c9d8f6b-xz4qm}. */
    String podName();

    /** A Kubernetes namespace like {@code prod-billing}. */
    String namespace();

    /** A Docker image tag like {@code registry.io/team/service:1.4.2}. */
    String dockerImage();

    /** Latency in milliseconds, weighted toward fast responses. */
    long latencyMs();
}
