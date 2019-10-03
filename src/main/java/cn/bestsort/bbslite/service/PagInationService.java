package cn.bestsort.bbslite.service;

import cn.bestsort.bbslite.pojo.dto.PagInationDto;
import cn.bestsort.bbslite.pojo.dto.QuestionDto;
import cn.bestsort.bbslite.mapper.QuestionExtMapper;
import cn.bestsort.bbslite.mapper.QuestionMapper;
import cn.bestsort.bbslite.mapper.TopicExtMapper;
import cn.bestsort.bbslite.mapper.UserMapper;
import cn.bestsort.bbslite.enums.CustomizeErrorCodeEnum;
import cn.bestsort.bbslite.exception.CustomizeException;
import cn.bestsort.bbslite.pojo.model.Question;
import cn.bestsort.bbslite.pojo.model.QuestionExample;
import cn.bestsort.bbslite.pojo.model.Topic;
import cn.bestsort.bbslite.pojo.model.User;
import org.apache.ibatis.session.RowBounds;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName QuestionService
 * @Description 问题处理(按条件搜索,所有,根据Id查找等)
 * @Author bestsort
 * @Date C19-8-28 下午6:30
 * @Version 1.0
 */
@Service
@CacheConfig(cacheNames = "PagInation")
public class PagInationService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    TopicExtMapper topicExtMapper;
    @Autowired
    QuestionExtMapper questionExtMapper;
    private Integer totalCount;

    @Autowired
    UserService userService;
    @Cacheable(keyGenerator = "keyGenerator")
    public PagInationDto listBySearch(String search, Integer page, Integer size){
        PagInationDto result;
        if(search.isEmpty()){
            totalCount = (int)questionMapper.countByExample(new QuestionExample());
            result = getPagInation(new QuestionExample(),page,size);
        }
        else{
            String order = "gmt_create desc";
            List<Question> questions = questionExtMapper.listBySearch(search,order);
            totalCount = questions.size();
            result = getPagInation(questions,page,size);
        }
        return result;
    }
    @Cacheable(keyGenerator = "keyGenerator")
    public PagInationDto listByPage(Integer page, Integer size, String topic){
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andTopicEqualTo(topic);
        totalCount = (int)questionMapper.countByExample(new QuestionExample());
        return getPagInation(questionExample,page,size);
    }
    @Cacheable(keyGenerator = "keyGenerator")
    public PagInationDto listByUserId(Long userId , Integer page, Integer size) {
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
        totalCount = (int)questionMapper.countByExample(questionExample);
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        return getPagInation(example,page,size);
    }


    /**
     * 将问题分页
     */
    @NotNull

    private PagInationDto getPagInation(QuestionExample example, Integer page, Integer size){
        int offset = size * (page - 1);
        //限制访问合法
        page = Math.min(totalCount/size + (totalCount%size==0? 0 : 1),page);
        page = Math.max(page,1);
        //按创建时间降序排序
        example.setOrderByClause("gmt_create desc");
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(
                example,
                new RowBounds(offset,size));
        return getPagInationDTO(questions, page, size);
    }
    private PagInationDto getPagInation(List<Question> questions, Integer page, Integer size){
        //限制访问合法
        page = Math.min(totalCount/size + (totalCount%size==0? 0 : 1),page);
        page = Math.max(page,1);
        return getPagInationDTO(questions, page, size);
    }

    @NotNull
    private PagInationDto getPagInationDTO(List<Question> questions, Integer page, Integer size) {
        List<QuestionDto> questionDTOList = new ArrayList<>();
        PagInationDto pagInationDTO = new PagInationDto();
        for (Question question : questions) {
            User user = userService.getById(question.getCreator());
            QuestionDto questionDTO = new QuestionDto();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        pagInationDTO.setQuestions(questionDTOList);
        pagInationDTO.setPagination(totalCount,page,size);
        return pagInationDTO;
    }
}