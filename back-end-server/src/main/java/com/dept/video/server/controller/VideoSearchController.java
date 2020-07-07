package com.dept.video.server.controller;

import com.dept.video.server.common.Constants;
import com.dept.video.server.common.MessagesUtility;
import com.dept.video.server.dto.PaginatedResponse;
import com.dept.video.server.dto.Response;
import com.dept.video.server.exception.EntityNotFoundException;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.exception.TooManyRequestException;
import com.dept.video.server.service.VideoSearchService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.dept.video.server.common.Constants.*;

/**
 * Controller class to manage video search information
 */
@Slf4j
@Controller
@RequestMapping("/video")
public class VideoSearchController {

    private static final String LABEL = "Video search";

    private final Constants constants;

    private final VideoSearchService videoSearchService;

    private final MessagesUtility messagesUtility;

    @Autowired
    public VideoSearchController(Constants constants, VideoSearchService videoSearchService, MessagesUtility messagesUtility) {
        this.constants = constants;
        this.videoSearchService = videoSearchService;
        this.messagesUtility = messagesUtility;
    }

    /**
     * Search video base on the q parameter. The search use an online video db to
     * get the list of result and use other video source to describe each specific video
     *
     * @return a paged response with status 200 and the resultant entity collection.
     * In case of bad query parameter, 400. In case of too many requests, 409. In case of bad credentials, 401
     */
    @ApiOperation(value = "Get videos information", tags = {LABEL})
    @ApiImplicitParams({@ApiImplicitParam(name = AUTHORIZATION,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getVideos(@RequestParam(name = "q") String q,
                                    @RequestParam(name = "page", required = false, defaultValue = "-1") final int page) {
        ResponseEntity<String> responseEntity;
        int requestPage = page <= 0 ? constants.getPaginatorPage() : page;
        Pageable pageable = PageRequest.of(requestPage, constants.getPaginatorSize());
        try {
            PaginatedResponse searchResults = videoSearchService.search(q, pageable);
            responseEntity = new ResponseEntity(searchResults, HttpStatus.OK);
        } catch (QueryException e) {
            responseEntity = new ResponseEntity(
                    Response.builder().
                            status(HttpStatus.BAD_REQUEST.value()).
                            message(messagesUtility.getMessage("bad.request")).build()
                    , HttpStatus.BAD_REQUEST);
            log.error(e.getMessage(), e);
        } catch (EntityNotFoundException e) {
            responseEntity = new ResponseEntity(
                    Response.builder().
                            status(HttpStatus.NOT_FOUND.value()).
                            message(messagesUtility.getMessage("not.found")).build()
                    , HttpStatus.NOT_FOUND);
            log.error(e.getMessage(), e);
        } catch (TooManyRequestException e) {
            responseEntity = new ResponseEntity(
                    Response.builder().
                            status(HttpStatus.TOO_MANY_REQUESTS.value()).
                            message(messagesUtility.getMessage("too.many.request")).build()
                    , HttpStatus.TOO_MANY_REQUESTS);
            log.error(e.getMessage(), e);
        }
        return responseEntity;
    }

}