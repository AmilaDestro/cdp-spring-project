package com.akvelon.cdp.endpoints;

import static com.akvelon.cdp.utils.UrlPatternsUtil.getHostWithProtocol;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import com.akvelon.cdp.entities.Request;
import com.akvelon.cdp.services.requests.RequestWithMetrics;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Does redirects to some sources on the Internet
 */

@RequiredArgsConstructor
@RestController
@Slf4j
public class RequestController {

    private static final String APP_HOME_PAGE = "http://localhost:8081/hello";
    private static final long BYTES_LIMIT_100K = 100000;
    @NonNull
    private RequestWithMetrics requestService;

    /**
     * Redirects to specified URL and calculates some metrics during request execution (amount of transferred data,
     * speed, request duration)
     *
     * @param url - URL to which redirect must be performed
     * @return {@link Object} which represents html web page to which redirection was made
     */
    @RequestMapping(value = "/redirect", method = GET)
    public Object redirectToSpecifiedUrlAndUpdateStatistic(@RequestParam(value = "url") final String url) {
        if (url == null || url.isEmpty()) {
            return requestService.sendGetRequestAndReturnPage(APP_HOME_PAGE).getFirst();
        }

        final String updatedUrl = url.toLowerCase().trim();
        final String urlWithHttp = getHostWithProtocol(updatedUrl);
        final Pair<String, Double> responsePair = requestService.sendGetRequestAndReturnPage(urlWithHttp);
        if (responsePair.getSecond() > BYTES_LIMIT_100K) {
            return new RedirectView(urlWithHttp);
        }
        return responsePair.getFirst();
    }

    /**
     * Gets {@link Request} with specified id
     *
     * @param requestId of request entity to return
     * @return found {@link Request}
     */
    @RequestMapping(value = "/requests/{id}", method = GET)
    public Request getRequestById(@PathVariable(name = "id") final long requestId) {
        return requestService.getInternalRequest(requestId);
    }

    /**
     * Gets last created {@link Request}
     *
     * @return latest {@link Request}
     */
    @RequestMapping(value = "/requests/last", method = GET)
    public Request getLastRequest() {
        return requestService.getLastCreatedInternalRequest();
    }

    /**
     * Gets all existing requests
     *
     * @return {@link List<Request>}
     */
    @RequestMapping(value = "/requests", method = GET)
    public List<Request> getAllRequests() {
        return requestService.getInternalRequests();
    }

    /**
     * Deletes {@link Request} with the given id
     *
     * @param requestId of request to delete
     * @return true if deletion was successful
     */
    @RequestMapping(value = "/requests/{id}", method = DELETE)
    public boolean deleteRequest(@PathVariable(name = "id") final long requestId) {
        return requestService.deleteInternalRequest(requestId);
    }
}
