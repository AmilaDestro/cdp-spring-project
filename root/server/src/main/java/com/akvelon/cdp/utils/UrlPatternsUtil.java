package com.akvelon.cdp.utils;

/**
 * Contains util methods that allow to work with URL patterns
 */
public class UrlPatternsUtil {
    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";
    private static final String URI_DELIMITER = "/";

    /**
     * Gets URL String without http/https
     *
     * @param url - passed URL String that is being processed
     * @return {@link String} that represents just domain name to be followed in browser
     */
    public static String getHostWithoutProtocol(final String url) {
        if (url.contains(HTTP_PREFIX)) {
            return url.substring(HTTP_PREFIX.length() + 1);
        } else if (url.contains(HTTPS_PREFIX)) {
            return url.substring(HTTP_PREFIX.length() + 1);
        }
        return url;
    }

    /**
     * Gets URL String with http/https
     *
     * @param url - passed URL String that is being processed
     * @return {@link String} that represents full URL which includes
     *          http/https protocol + domain name to be followed in browser
     */
    public static String getHostWithProtocol(final String url) {
        final String updatedUrl = url.toLowerCase().trim();
        if (updatedUrl.contains(HTTPS_PREFIX) || updatedUrl.contains(HTTP_PREFIX)) {
            return updatedUrl;
        } else  {
            return HTTP_PREFIX + updatedUrl;
        }
    }

    /**
     * Skips URI absolute path and leaves just domain name
     *
     * @param uri - URI to process like "domain.com/resource"
     * @return domain name
     */
    public static String getDomainNameHostSkipURI(final String uri) {
        final String domainName = getHostWithoutProtocol(uri);
        if (domainName.contains(URI_DELIMITER)) {
            return domainName.split(URI_DELIMITER)[0];
        }
        return domainName;
    }
}
