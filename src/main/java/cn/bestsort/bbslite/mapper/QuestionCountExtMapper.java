package cn.bestsort.bbslite.mapper;


import cn.bestsort.bbslite.pojo.model.QuestionCount;

public interface QuestionCountExtMapper {
    void incViewCount(QuestionCount record);
    int updateCommentCount(QuestionCount record);
    int updateFollowCount(QuestionCount record);
}
