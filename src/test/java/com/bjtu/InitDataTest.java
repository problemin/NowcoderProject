package com.bjtu;

import java.util.Date;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.bjtu.dao.QuestionDAO;
import com.bjtu.dao.UserDAO;
import com.bjtu.model.Question;
import com.bjtu.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)
@WebAppConfiguration
@Sql("/init-schema.sql")
public class InitDataTest {

	@Autowired
	UserDAO userDAO;
	
	@Autowired
	QuestionDAO questionDAO;
	@Test
	public void initDatabase() {
		Random random = new Random();
		
		for(int i=0;i<11;i++){
			User user = new User();
	        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
	        user.setName(String.format("USER%d", i));
	        user.setPassword("");
	        user.setSalt("");
	        userDAO.addUser(user);
	        
	        user.setPassword("xxx");
	        userDAO.updatePassword(user);
	        
	        Question question = new Question();
	        question.setCommentCount(i);
	        Date date = new Date();
	        date.setTime(date.getTime());
	        question.setCreatedDate(date);
	        question.setUserId(i+1); 
	        question.setTitle(String.format("ZMTITLE{%d}", i));
	        question.setContent(String.format("aasdasdasd Content:", i));
	        questionDAO.addQuestion(question);
		}
		
		Assert.assertEquals("xxx", userDAO.selectById(1).getPassword());
		userDAO.deleteById(1);
		Assert.assertNull(userDAO.selectById(1));
		
		System.out.println(questionDAO.selectLatestQuestions(0, 0, 10));
	}

}
