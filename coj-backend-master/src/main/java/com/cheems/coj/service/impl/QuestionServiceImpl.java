package com.cheems.coj.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheems.coj.common.ErrorCode;
import com.cheems.coj.constant.CommonConstant;
import com.cheems.coj.exception.BusinessException;
import com.cheems.coj.exception.ThrowUtils;
import com.cheems.coj.mapper.QuestionMapper;
import com.cheems.coj.model.dto.question.QuestionQueryRequest;
import com.cheems.coj.model.entity.Question;
import com.cheems.coj.model.entity.User;
import com.cheems.coj.model.vo.QuestionVO;
import com.cheems.coj.model.vo.UserVO;
import com.cheems.coj.service.QuestionService;
import com.cheems.coj.service.UserService;
import com.cheems.coj.utils.SqlUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author cheems
 * @description 针对表【question(题目)】的数据库操作Service实现
 */
@Slf4j
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    private static final String QUESTION_LIST_CACHE_KEY_PREFIX = "coj:question:list:public:";
    private static final String QUESTION_LIST_CACHE_VERSION_KEY = "coj:question:list:public:version";
    private static final long QUESTION_LIST_EMPTY_CACHE_TTL_SECONDS = 20L;
    private static final long QUESTION_LIST_CACHE_TTL_SECONDS = 60L;


    private final static Gson GSON = new Gson();

    /**
     * 校验题目是否合法
     *
     * @param question
     * @param add
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUserVO(userVO);
        return questionVO;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUserVO(userService.getUserVO(user));
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    @Override
    public Page<QuestionVO> listQuestionVOByPageWithCache(QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {

        QuestionQueryRequest normalizedRequest = normalizeQuestionQueryRequest(questionQueryRequest);
        String key = buildQuestionListCacheKey(normalizedRequest);

        //先查缓存
        try {
            String value = stringRedisTemplate.opsForValue().get(key);
            Type type = new TypeToken<Page<QuestionVO>>() {
            }.getType();
            if (StringUtils.isNotBlank(value)) {
                Page<QuestionVO> cachedPage = GSON.fromJson(value, type);
                if (cachedPage != null) {
                    log.info("从缓存获取题目列表，key={}", key);
                    return cachedPage;
                }
            }
        } catch (Exception e) {
            log.warn("题目列表读取失败，key={}", key, e);
        }

        //查库
        long current = normalizedRequest.getCurrent();
        long pageSize = normalizedRequest.getPageSize();

        Page<Question> page = this.page(new Page<>(current, pageSize), getQueryWrapper(normalizedRequest));
        Page<QuestionVO> questionVOPage = getQuestionVOPage(page, request);

        //回写缓存
        try {
            long ttl = CollectionUtil.isNotEmpty(questionVOPage.getRecords()) ? QUESTION_LIST_CACHE_TTL_SECONDS : QUESTION_LIST_EMPTY_CACHE_TTL_SECONDS;
            stringRedisTemplate.opsForValue().set(key, GSON.toJson(questionVOPage), ttl, TimeUnit.SECONDS);
            log.info("写入缓存，key={},ttl={}", key, ttl);
        } catch (Exception e) {
            log.warn("写入缓存失败，key={}", key, e);
        }

        //返回结果
        return questionVOPage;
    }

    @Override
    public void bumpQuestionListCacheVersion() {
        stringRedisTemplate.opsForValue().increment(QUESTION_LIST_CACHE_VERSION_KEY);
    }


    private long getQuestionListCacheVersion() {
        String version = stringRedisTemplate.opsForValue().get(QUESTION_LIST_CACHE_VERSION_KEY);
        if (version == null) {
            stringRedisTemplate.opsForValue().set(QUESTION_LIST_CACHE_VERSION_KEY, "1");
            return 1L;
        }
        return Long.parseLong(version);
    }

    private String buildQuestionListCacheKey(QuestionQueryRequest request) {

        String rawKey = "id=" + request.getId()
                + ";title=" + StringUtils.defaultString(request.getTitle())
                + ";content=" + StringUtils.defaultString(request.getContent())
                + ";tags=" + request.getTags()
                + ";answer=" + StringUtils.defaultString(request.getAnswer())
                + ";userId=" + request.getUserId()
                + ";current=" + request.getCurrent()
                + ";pageSize=" + request.getPageSize()
                + ";sortField=" + StringUtils.defaultString(request.getSortField())
                + ";sortOrder=" + StringUtils.defaultString(request.getSortOrder());
        long questionListCacheVersion = getQuestionListCacheVersion();

        return QUESTION_LIST_CACHE_KEY_PREFIX + questionListCacheVersion + ":" + DigestUtil.md5Hex(rawKey);

    }

    /**
     * 请求归一化。  避免语义相同但写法不同的请求生成不同的缓存key
     *
     * @param questionQueryRequest
     * @return
     */
    private QuestionQueryRequest normalizeQuestionQueryRequest(QuestionQueryRequest questionQueryRequest) {


        QuestionQueryRequest normalized = new QuestionQueryRequest();
        if (questionQueryRequest == null) {
            normalized.setCurrent(1);
            normalized.setPageSize(10);
            normalized.setSortOrder(CommonConstant.SORT_ORDER_ASC);
            return normalized;
        }


        String title = StringUtils.trimToNull(questionQueryRequest.getTitle());
        List<String> tags = questionQueryRequest.getTags();
        if (tags != null) {
            tags = tags.stream()
                    .map(StringUtils::trimToNull)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
        }


        normalized.setId(questionQueryRequest.getId());
        normalized.setTitle(title);
        normalized.setContent(questionQueryRequest.getContent());
        normalized.setTags(tags);
        normalized.setAnswer(questionQueryRequest.getAnswer());
        normalized.setUserId(questionQueryRequest.getUserId());
        normalized.setCurrent(questionQueryRequest.getCurrent() > 0 ? questionQueryRequest.getCurrent() : 1);
        normalized.setPageSize(questionQueryRequest.getPageSize() > 0 ? questionQueryRequest.getPageSize() : 10);
        normalized.setSortField(StringUtils.trimToNull(questionQueryRequest.getSortField()));
        normalized.setSortOrder(StringUtils.isBlank(questionQueryRequest.getSortOrder()) ? CommonConstant.SORT_ORDER_ASC : questionQueryRequest.getSortOrder());

        return normalized;


    }


}




