package com.bjtu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.bjtu.dao.CommentDAO;
import com.bjtu.model.Comment;

@Service
public class CommentService {
	@Autowired
	CommentDAO commentDAO;
	
	@Autowired
	SensitiveService sensitiveService;
	public List<Comment> getCommentByEntity(int entityId,int entityType){
		return commentDAO.selectCommentByEntity(entityId, entityType);
	}
	
	public int addComment(Comment comment){
		comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
		comment.setContent(sensitiveService.filter(comment.getContent()));
		return commentDAO.addComment(comment) > 0? comment.getId():0;
	}
	public boolean deleteComment(int commentId){
		return commentDAO.updateStatus(commentId, 1) > 0;
	}
	
	public int countComment(int entityId,int entityType){
		return commentDAO.getCommentCount(entityId, entityType);
	}
	
	public Comment getCommentById(int id){
		return commentDAO.getCommentById(id);
	}
}
