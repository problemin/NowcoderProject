package com.bjtu.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bjtu.model.Comment;

@Mapper
public interface CommentDAO {

	String TABLE_NAME = "comment";
	String INSERT_FIELDS="user_id,entity_id,entity_type,content,created_date,status";
	String SELECT_FIELDS="id,"+INSERT_FIELDS;
	
	@Insert({"insert into ",TABLE_NAME," (",INSERT_FIELDS,
		") values (#{userId},#{entityId},#{entityType},#{content},#{createdDate},#{status})"})
	int addComment(Comment comment);
	
	@Select({"select ",SELECT_FIELDS," from ",TABLE_NAME,
		" where entity_id=#{entityId} and entity_type=#{entityType} order by created_date asc"})
	List<Comment> selectCommentByEntity(@Param("entityId") int entityId,@Param("entityType")int entityType);

	@Select({"select count(id) from ",TABLE_NAME," where entity_id=#{entityId} and entity_type=#{entityType}"})
	int  getCommentCount(@Param("entityId")int entityId,@Param("entityType")int entityType);

	@Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id=#{id}"})
	Comment  getCommentById(@Param("id")int id);
	
	@Update({"update ",TABLE_NAME,"set status =#{status} where id=#{id}"})
	int updateStatus(@Param("id") int id,@Param("status") int status);
}
