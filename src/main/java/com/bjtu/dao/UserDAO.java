package com.bjtu.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bjtu.model.User;

@Mapper
public interface UserDAO {
	
	String TABEL_NAME = "user";
	String INSERT_FIELDS="name,password,salt,head_url";
	String SELECT_FIELDS="id,"+INSERT_FIELDS;
	
	@Insert({"insert into ",TABEL_NAME," (",INSERT_FIELDS,
		") values(#{name},#{password},#{salt},#{headUrl})"})
	int addUser(User user);
	
	@Select({"select ", SELECT_FIELDS," from ", TABEL_NAME," where id = #{id}"})
	User selectById(int id);
	
	@Update({"update ",TABEL_NAME,"set password=#{password}"})
	void updatePassword(User user);
	
	@Delete({"delete from",TABEL_NAME,"where id=#{id}"})
	void deleteById(int id);
	
	@Select({"select ",SELECT_FIELDS," from",TABEL_NAME,"where name = #{name}"})
	User selectByName(String username);
	
}
