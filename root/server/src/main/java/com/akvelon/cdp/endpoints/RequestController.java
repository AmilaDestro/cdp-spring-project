package com.akvelon.cdp.endpoints;

import com.akvelon.cdp.entities.Request;
import com.akvelon.cdp.exceptions.NotFoundException;
import com.akvelon.cdp.exceptions.RequestNotFoundException;
import com.akvelon.cdp.exceptions.WebPageNotFoundException;
import com.akvelon.cdp.services.requests.RequestWithMetrics;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

import static com.akvelon.cdp.utils.UrlPatternsUtil.getHostWithProtocol;
import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Does redirects to some sources on the Internet
 */

@RequiredArgsConstructor
@RestController
@Slf4j
public class RequestController {
    private static final String APP_HOME_PAGE = "http://localhost:8081/hello";
    @NonNull
    private RequestWithMetrics requestService;

    /**
     * Redirects to specified URL and calculates some metrics during request execution (amount of transferred data,
     * speed, request duration)
     *
     * @param url - URL to which redirect must be performed
     * @return {@link RedirectView} which represents web page to which redirection was made
     */
    @RequestMapping(value = "/redirect", method = GET)
    public RedirectView redirectToSpecifiedUrlAndUpdateStatistic(@RequestParam(value = "url") final String url) throws WebPageNotFoundException {
        if (url == null || url.isEmpty()) {
            return new RedirectView(APP_HOME_PAGE);
        }

        final String urlWithHttp = getHostWithProtocol(url);
        Request createdRequest = null;
        try {
            createdRequest = requestService.createRequest(urlWithHttp);
        } catch (NotFoundException e) {
            log.error(format("Error occurred during creation of request to URL {}", urlWithHttp));
        }

        if (createdRequest != null) {
            return new RedirectView(urlWithHttp);
        } else {
            throw new WebPageNotFoundException(urlWithHttp);
        }
    }

    /**
     * Gets {@link Request} with specified id
     *
     * @param requestId of request entity to return
     * @return found {@link Request}
     * @throws RequestNotFoundException if {@link Request} with the given id was not found
     */
    @RequestMapping(value = "/requests/{id}", method = GET)
    public Request getRequestById(@PathVariable(name = "id") final long requestId) throws RequestNotFoundException {
        return requestService.getRequest(requestId);
    }

    /**
     * Gets last created {@link Request}
     *
     * @return latest {@link Request}
     */
    @RequestMapping(value = "/requests/last", method = GET)
    public Request getLastRequest() {
        return requestService.getLastCreatedRequest();
    }

    /**
     * Gets all existing requests
     *
     * @return {@link List<Request>}
     */
    @RequestMapping(value = "/requests", method = GET)
    public List<Request> getAllRequests() {
        return requestService.getRequests();
    }

    /**
     * Deletes {@link Request} with the given id
     * @param requestId of request to delete
     * @return true if deletion was successful
     * @throws RequestNotFoundException if request with the given id was not found
     */
    @RequestMapping(value = "/requests/{id}", method = DELETE)
    public boolean deleteRequest(@PathVariable(name = "id") final long requestId) throws RequestNotFoundException {
        return requestService.deleteRequest(requestId);
    }
}
