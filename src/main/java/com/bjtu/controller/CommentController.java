package com.bjtu.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bjtu.async.EntityType;
import com.bjtu.model.Comment;
import com.bjtu.model.HostHolder;
import com.bjtu.service.CommentService;
import com.bjtu.service.QuestionService;
import com.bjtu.util.WendaUtil;

@Controller
public class CommentController {

	private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
	@Autowired
	CommentService commentService;
	@Autowired
	HostHolder hostHolder;
	@Autowired
	QuestionService questionService;
	@RequestMapping(path={"/addComment"},method = {RequestMethod.POST})
	public String addComment(@RequestParam("entityType") String entityType,
			@RequestParam("entityId") int entityId,
			@RequestParam("content") String content,
			@RequestParam("questionId") int questionId){
		try{
			Comment comment = new Comment();
			comment.setContent(content);
			if(hostHolder.getUser() != null){
				comment.setUserId(hostHolder.getUser().getId());
			}else{
				return "redirect:/reglogin?next=/question/"+questionId;
			}
			comment.setCreatedDate(new Date());
			int type = 0;
			if("question".equals(entityType)){
				type = EntityType.ENTITY_QUESTION;
			}
			if("comment".equals(entityType)){
				type = EntityType.ENTITY_COMMENT;
			}
			comment.setEntityType(type);
			comment.setEntityId(entityId);
			commentService.addComment(comment);
			questionService.updateCommentCount(comment.getEntityId(), commentService.countComment(entityId,type));
		}catch(Exception e){
			logger.error("添加评论失败 "+e.getMessage());
		}
		return "redirect:/question/" + questionId;
	}
	
	
}
