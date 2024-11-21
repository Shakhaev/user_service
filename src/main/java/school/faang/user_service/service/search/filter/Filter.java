package school.faang.user_service.service.search.filter;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;

public interface Filter {
    void apply(BoolQuery.Builder boolQuery);
}
