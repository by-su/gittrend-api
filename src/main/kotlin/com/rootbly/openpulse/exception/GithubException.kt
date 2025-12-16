package com.rootbly.openpulse.exception

/**
 * Exception thrown on GitHub API 4xx client errors
 *
 * Represents client-side errors (bad request, auth failure, forbidden, not found, etc.).
 * Typically occurs with HTTP status codes 400, 401, 403, 404.
 *
 * @param message Error detail message (includes status code and response body)
 */
class GithubClientException(message: String) : RuntimeException(message)

/**
 * Exception thrown on GitHub API 5xx server errors
 *
 * Represents GitHub server-side errors (internal error, service unavailable, etc.).
 * Typically occurs with HTTP status codes 500, 502, 503.
 *
 * @param message Error detail message (includes status code and response body)
 */
class GithubServerException(message: String) : RuntimeException(message)