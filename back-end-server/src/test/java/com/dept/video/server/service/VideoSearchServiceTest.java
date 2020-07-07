package com.dept.video.server.service;

import com.dept.video.server.dto.PaginatedResponse;
import com.dept.video.server.dto.VideoInfo;
import com.dept.video.server.dto.VideoSearchDetail;
import com.dept.video.server.exception.EntityNotFoundException;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.exception.TooManyRequestException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VideoSearchServiceTest {

    private static final int PAGE = 0;
    private static final int SIZE = 20;
    private VideoSearchService videoSearchService;
    private SearchVideoInformation searchVideoInformation;
    private VideoDetailInformation videoDetailInformation;

    @Before
    public void init() {
        searchVideoInformation = Mockito.mock(SearchVideoInformation.class);
        videoDetailInformation = Mockito.mock(VideoDetailInformation.class);
        videoSearchService = new VideoSearchService(searchVideoInformation, videoDetailInformation);
    }

    @Test
    public void whenSearchVideoInfoIsEmptyReturnsEmptySearch() throws EntityNotFoundException, QueryException, TooManyRequestException {
        Pageable pageable = PageRequest.of(PAGE, SIZE);
        ArrayList<Object> content = new ArrayList<>();
        PaginatedResponse paginatedResponse = PaginatedResponse.builder().content(content).build();
        String q = "video name";
        Mockito.when(searchVideoInformation.search(q, pageable)).thenReturn(paginatedResponse);

        PaginatedResponse search = videoSearchService.search(q, pageable);

        Assert.assertTrue(search.getContent().isEmpty());
    }

    @Test
    public void whenSearchVideoInfoHasVideosReturnsPopulatedVideoInfoSearch() throws EntityNotFoundException, QueryException, TooManyRequestException {
        List<String> ids = Arrays.asList("id1", "id2");
        Pageable pageable = PageRequest.of(PAGE, SIZE);
        PaginatedResponse paginatedResponse = getVideoSearchDetailsPaginatedResponse(ids);
        String q = "video name";
        Mockito.when(searchVideoInformation.search(q, pageable)).thenReturn(paginatedResponse);
        Mockito.when(videoDetailInformation.searchByIds(ArgumentMatchers.anyList(),
                ArgumentMatchers.isA(Pageable.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return getVideoInfoPaginatedResponse(invocation.getArgument(0));
            }
        });

        PaginatedResponse<VideoSearchDetail> search = videoSearchService.search(q, pageable);

        Assert.assertEquals(2, search.getContent().get(0).getVideos().size());
    }

    private PaginatedResponse getVideoSearchDetailsPaginatedResponse(List<String> ids) {
        ArrayList<Object> videoSearchDetailContent = new ArrayList<>();
        VideoSearchDetail videoSearchDetail = new VideoSearchDetail();
        videoSearchDetail.setVideos(ids.stream().map(key -> VideoInfo.builder().id(key).key(key).build()).collect(Collectors.toList()));
        videoSearchDetailContent.add(videoSearchDetail);
        return PaginatedResponse.builder().content(videoSearchDetailContent).build();
    }

    public PaginatedResponse getVideoInfoPaginatedResponse(List<String> ids) {
        return PaginatedResponse.builder().content(ids.stream().map(key -> VideoInfo.builder().key(key).id(key).build()).collect(Collectors.toList())).build();
    }

}