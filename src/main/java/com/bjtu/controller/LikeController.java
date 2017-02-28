package com.bjtu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjtu.async.EntityType;
import com.bjtu.async.EventModel;
import com.bjtu.async.EventProducer;
import com.bjtu.async.EventType;
import com.bjtu.model.Comment;
import com.bjtu.model.HostHolder;
import com.bjtu.service.CommentService;
import com.bjtu.service.LikeService;
import com.bjtu.util.WendaUtil;

@Controller
public class LikeController {

	private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

	@Autowired
	HostHolder hostHolder;
	
	@Autowired
	LikeService likeService;
	
	@Autowired
	EventProducer eventProducer;
	
	@Autowired
	CommentService commentService;
	@RequestMapping(path={"/like"},method=RequestMethod.POST)
	@ResponseBody
	public String like(Model model,
			@RequestParam("commentId") int commentId){
		if(hostHolder.getUser() == null){
			return WendaUtil.getJSONString(999);
		}
		
		Comment comment = commentService.getCommentById(commentId);
		eventProducer.fireEvent(new EventModel(EventType.LIKE)
		.setActorId(hostHolder.getUser().getId()).setEntityId(commentId).setEntityOwnerId(comment.getUserId())
		.setEntityType(EntityType.ENTITY_COMMENT).setExt("questionId",String.valueOf(comment.getEntityId()))); 
		long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
		return WendaUtil.getJSONString(0, String.valueOf(likeCount));
	}
	
	@RequestMapping(path={"/dislike"},method=RequestMethod.POST)
	@ResponseBody
	public String disLike(Model model,
			@RequestParam("commentId") int commentId){
		if(hostHolder.getUser() == null){
			return WendaUtil.getJSONString(999);
		}
		long likeCount = likeService.dislike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
		return WendaUtil.getJSONString(0, String.valueOf(likeCount));
	}
	
	
}
