package com.dept.video.server.common;

import com.dept.video.server.dto.PaginatedResponse;
import com.dept.video.server.exception.QueryException;
import com.dept.video.server.parser.QueryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Query construction helpers
 */
@Component
public class QueryUtil {

    private final QueryParser queryParser;

    @Autowired
    public QueryUtil(QueryParser queryParser) {
        this.queryParser = queryParser;
    }

    public PaginatedResponse getPaginatedResponse(List content, long count, Pageable pageable) {
        final int totalPages = (int) Math.ceil((double) count / pageable.getPageSize());
        return PaginatedResponse.builder().totalPages(totalPages)
                .totalElements((int) count)
                .numberOfElements(content.size())
                .size(pageable.getPageSize())
                .number(pageable.getPageNumber())
                .content(content).build();
    }

    /**
     * Create a query base on string search.
     * The query string is parsed into a series of terms and operators.
     * The format is a key:value pair using the operators AND, OR.
     * The grouping is allowed using ()
     */
    public Query buildQuery(
            String q,
            String queryLanguage,
            Pageable pageable,
            Optional<String[]> orders,
            Optional<String> fields
    ) throws QueryException {
        Query queryFromLanguage;
        if ("mongo".equalsIgnoreCase(queryLanguage)) {
            queryFromLanguage = new BasicQuery(StringUtils.isEmpty(q) ? "{}" : q);
        } else if (StringUtils.isEmpty(queryLanguage) || "elasticsearch".equalsIgnoreCase(queryLanguage)) {
            queryFromLanguage = queryParser.buildQuery(q);
        } else {
            throw new IllegalArgumentException("Invalid query language");
        }
        Query query = queryFromLanguage;
        query.limit(pageable.getPageSize());
        query.skip(pageable.getPageSize() * pageable.getPageNumber());
        fields.ifPresent(s -> getFields(s).forEach(field -> query.fields().include(field)));
        orders.ifPresent(
                strings ->
                        Arrays.stream(strings).
                                filter(orderValue -> !StringUtils.isEmpty(orderValue)).
                                forEach(orderValue -> query.with(getSorter(orderValue)))
        );
        return query;
    }

    private List<String> getFields(String fields) {
        if (!StringUtils.isEmpty(fields)) {
            return Stream.of(fields.trim().split("\\s*,\\s*")).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private Sort getSorter(String sorter) {
        String[] sorterElements = sorter.split(":", 2);
        Sort.Direction sortType = Sort.Direction.DESC;
        if (sorterElements.length > 1) {
            sortType = sorterElements[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        }
        return new Sort(sortType, sorterElements[0]);

    }
}
