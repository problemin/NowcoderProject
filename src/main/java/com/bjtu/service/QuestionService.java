package com.bjtu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.bjtu.dao.QuestionDAO;
import com.bjtu.model.Question;

@Service
public class QuestionService {

	@Autowired
	private QuestionDAO questionDAO;
	
	@Autowired
	private SensitiveService sensitiveService;
	
	public Question selectById(int id){
		return questionDAO.selectById(id);
	}
	public int addQuetion(Question question){
		//敏感词过滤
		question.setContent(HtmlUtils.htmlEscape(question.getContent()));
		question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
		question.setContent(sensitiveService.filter(question.getContent()));
		question.setTitle(sensitiveService.filter(question.getTitle()));
		return questionDAO.addQuestion(question)>0?question.getId():0;
	}
	public List<Question> getLastestQuestions(int userId,int offset,int limit){
		return questionDAO.selectLatestQuestions(userId, offset, limit);
	}
	public boolean updateCommentCount(int id,int commentCount){
		return questionDAO.updateCommentCount(id, commentCount) > 0;
	}
}
