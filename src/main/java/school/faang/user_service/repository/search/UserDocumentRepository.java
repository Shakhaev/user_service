package school.faang.user_service.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import school.faang.user_service.model.search.user.UserDocument;

import java.util.List;

public interface UserDocumentRepository extends ElasticsearchRepository<UserDocument, String> {
    List<UserDocument> findAllByUserIdIn(List<Long> promotedResourceIds);
}
