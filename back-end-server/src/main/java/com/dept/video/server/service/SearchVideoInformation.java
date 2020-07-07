package com.dept.video.server.service;

import com.dept.video.server.dto.PaginatedResponse;
import com.dept.video.server.dto.VideoSearchDetail;
import com.dept.video.server.exception.EntityNotFoundException;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.exception.TooManyRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public interface SearchVideoInformation {

    PaginatedResponse<VideoSearchDetail> search(String q, Pageable pageable) throws QueryException, EntityNotFoundException, TooManyRequestException;

}
