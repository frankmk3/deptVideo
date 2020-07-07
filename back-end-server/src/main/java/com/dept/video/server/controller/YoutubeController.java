package com.dept.video.server.controller;

import com.dept.video.server.common.Constants;
import com.dept.video.server.common.MessagesUtility;
import com.dept.video.server.dto.PaginatedResponse;
import com.dept.video.server.dto.Response;
import com.dept.video.server.exception.EntityNotFoundException;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.exception.TooManyRequestException;
import com.dept.video.server.service.YoutubeService;
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

import java.util.Arrays;
import java.util.Optional;

import static com.dept.video.server.common.Constants.*;

@Slf4j
@Controller
@RequestMapping("/youtube")
public class YoutubeController {

    private static final String LABEL = "Youtube";

    private final Constants constants;

    private final YoutubeService youtubeService;

    private final MessagesUtility messagesUtility;

    @Autowired
    public YoutubeController(Constants constants, YoutubeService youtubeService, MessagesUtility messagesUtility) {
        this.constants = constants;
        this.youtubeService = youtubeService;
        this.messagesUtility = messagesUtility;
    }

    /**
     * Get video from Youtube
     *
     * @return a paged response with status 200 and the resultant entity collection.
     * In case of bad query parameter, 400. In case of too many requests, 409. In case of bad credentials, 401
     */
    @ApiOperation(value = "Get videos", tags = LABEL)
    @ApiImplicitParams({@ApiImplicitParam(name = AUTHORIZATION,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getVideos(@RequestParam(name = "q") String q,
                                    @RequestParam(name = "order", required = false) final String[] order,
                                    @RequestParam(name = "fields", required = false) String fields) {
        ResponseEntity<String> responseEntity;
        Pageable pageable = PageRequest.of(constants.getPaginatorPage(), constants.getPaginatorSize());
        try {
            PaginatedResponse searchResults = youtubeService.search(
                    q,
                    Optional.ofNullable(order),
                    Optional.ofNullable(fields),
                    pageable,
                    null
            );
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

    /**
     * Get specific video information from Youtube
     *
     * @return a response with status 200 and the resultant entity.  In case of too many requests, 409.
     * In case wrong entity id, 404. In case of bad credentials, 401;
     */
    @ApiOperation(value = "Get videos  by id", tags = LABEL)
    @ApiImplicitParams({@ApiImplicitParam(name = AUTHORIZATION,
            value = AUTHORIZATION_TOKEN,
            dataType = STRING,
            paramType = HEADER)})
    @GetMapping("/searchById")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getVideosByIds(@RequestParam(name = "ids") String[] id,
                                         @RequestParam(name = "order", required = false) final String[] order,
                                         @RequestParam(name = "fields", required = false) String fields,
                                         @RequestParam(name = "page", required = false) final String pageToken,
                                         @RequestParam(name = "size", required = false, defaultValue = "-1") final int size) {
        ResponseEntity<String> responseEntity;
        int requestSize = size <= 0 ? constants.getPaginatorSize() : size;
        Pageable pageable = PageRequest.of(constants.getPaginatorPage(), requestSize);
        try {
            PaginatedResponse searchResults = youtubeService.searchByIds(
                    Arrays.asList(id),
                    Optional.ofNullable(order),
                    Optional.ofNullable(fields),
                    pageable,
                    pageToken
            );
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