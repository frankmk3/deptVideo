package com.dept.video.server.service;

import com.dept.video.server.dto.PaginatedResponse;
import com.dept.video.server.dto.VideoInfo;
import com.dept.video.server.exception.EntityNotFoundException;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.exception.TooManyRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public interface VideoDetailInformation {

    PaginatedResponse<VideoInfo> searchByIds(List<String> ids, Pageable pageable) throws QueryException, EntityNotFoundException, TooManyRequestException;

}
