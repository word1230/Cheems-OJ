package com.cheems.coj.esdao;

import com.cheems.coj.model.dto.post.PostEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 帖子 ES 操作
 *
 * @author cheems
 */
public interface PostEsDao extends ElasticsearchRepository<PostEsDTO, Long> {
}
