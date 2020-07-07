package com.dept.video.server.service;

import com.dept.video.server.common.ObjectUtility;
import com.dept.video.server.dto.PaginatedResponse;
import com.dept.video.server.dto.VideoInfo;
import com.dept.video.server.dto.VideoSearchDetail;
import com.dept.video.server.exception.EntityNotFoundException;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.exception.TooManyRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Search video service that use a video information source and a video details source to populate the searches
 */
@Slf4j
@Service
public class VideoSearchService {

    private final SearchVideoInformation searchVideoInformation;
    private final VideoDetailInformation videoDetailInformation;

    @Autowired
    public VideoSearchService(SearchVideoInformation searchVideoInformation, VideoDetailInformation videoDetailInformation) {
        this.searchVideoInformation = searchVideoInformation;
        this.videoDetailInformation = videoDetailInformation;
    }

    /**
     * Search video information
     */
    public PaginatedResponse search(String q, Pageable pageable) throws QueryException, EntityNotFoundException, TooManyRequestException {
        PaginatedResponse<VideoSearchDetail> videoInfoList = searchVideoInformation.search(q, pageable);
        List<VideoSearchDetail> updatedVideoInfoList = videoInfoList.getContent().parallelStream().map(v -> {
            try {
                return addVideoSourceData(v, pageable).get();
            } catch (InterruptedException | ExecutionException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        videoInfoList.setContent(updatedVideoInfoList);
        return videoInfoList;
    }

    /**
     * Enhance the video response
     */
    @Async("asyncExecutor")
    public CompletableFuture<VideoSearchDetail> addVideoSourceData(VideoSearchDetail videoSearchDetail, Pageable pageable) {
        Map<String, VideoInfo> videoInfoMap = videoSearchDetail.getVideos().stream().collect(Collectors.toMap(VideoInfo::getKey, videoInfo -> videoInfo));
        try {
            PaginatedResponse<VideoInfo> response = videoDetailInformation.searchByIds(new ArrayList<>(videoInfoMap.keySet()), pageable);
            Map<String, VideoInfo> updatedVideoInfoMap = response.getContent().stream().filter(Objects::nonNull)
                    .map(videoInfo ->
                            (VideoInfo) ObjectUtility.updateNoNullParameter(videoInfoMap.get(videoInfo.getKey()), videoInfo))
                    .collect(Collectors.toMap(VideoInfo::getKey, videoInfo -> videoInfo));
            videoInfoMap.putAll(updatedVideoInfoMap);
            videoSearchDetail.setVideos(new ArrayList<>(videoInfoMap.values()));
        } catch (QueryException | EntityNotFoundException | TooManyRequestException e) {
            log.error(e.getMessage(), e);
        }
        return CompletableFuture.completedFuture(videoSearchDetail);
    }

}