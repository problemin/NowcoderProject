package com.bjtu.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bjtu.model.Question;

@Mapper
public interface QuestionDAO {
	
	String TABEL_NAME = "question";
	String INSERT_FIELDS="title,content,created_date,user_id,comment_count";
	String SELECT_FIELDS="id,"+INSERT_FIELDS;
	
	@Insert({"insert into ",TABEL_NAME," (",INSERT_FIELDS,
		") values(#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
	int addQuestion(Question question);
	
	List<Question> selectLatestQuestions(@Param("userId") int userId,
			@Param("offset") int offset,
			@Param("limit") int limit);
	
	@Select({"select ",SELECT_FIELDS," from "," question where id=#{id}"})
	Question selectById(int id);
	
	@Update({"update question set comment_count=#{commentCount} where id=#{id}"})
	int updateCommentCount(@Param("id") int id,@Param("commentCount")int commentCount);
}
