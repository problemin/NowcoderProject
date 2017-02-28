package com.bjtu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjtu.async.EntityType;
import com.bjtu.model.Comment;
import com.bjtu.model.HostHolder;
import com.bjtu.model.Question;
import com.bjtu.model.ViewObject;
import com.bjtu.service.CommentService;
import com.bjtu.service.LikeService;
import com.bjtu.service.QuestionService;
import com.bjtu.service.UserService;
import com.bjtu.util.WendaUtil;

@Controller
public class QuestionController {

	private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
	
	@Autowired
	QuestionService questionService;
	
	@Autowired
	HostHolder hostHolder;
	
	@Autowired
	UserService userService;
	
	@Autowired
	LikeService likeService;
	@Autowired
	CommentService commentService;
	@RequestMapping(value="/question/add",method={RequestMethod.POST})
	@ResponseBody
	public String addQuestion(@RequestParam("title") String title,
			@RequestParam("content")String content){
		try{
			Question question = new Question();
			question.setContent(content);
			question.setTitle(title);
			question.setCreatedDate(new Date());
			question.setCommentCount(0);
			if(hostHolder.getUser()==null){
				return WendaUtil.getJSONString(999);
			}else{
				question.setUserId(hostHolder.getUser().getId());
			}
			if(questionService.addQuetion(question)>0){
				return WendaUtil.getJSONString(0);
			}
		}catch(Exception e){
			logger.error("增加题目失败"+e.getMessage());
		}
		return WendaUtil.getJSONString(1,"失败");
	}
	
	@RequestMapping(value="/question/{qid}")
	public String questionDetail(Model model,@PathVariable("qid")int qid){
		Question question = questionService.selectById(qid);
		model.addAttribute("question", question);
		model.addAttribute("user", userService.getUser(question.getUserId()));
		List<Comment> comments = commentService.getCommentByEntity(qid, EntityType.ENTITY_QUESTION);
		List<ViewObject> vos = new ArrayList<ViewObject>();
		for(Comment comment:comments){
			ViewObject vo = new ViewObject();
			List<Comment> subComments = commentService.getCommentByEntity(comment.getId(), EntityType.ENTITY_COMMENT);
			vo.set("mainComment", comment);
			List<ViewObject> subList = new ArrayList<ViewObject>();
			for(Comment subComment:subComments){
				ViewObject subVo = new ViewObject();
				subVo.set("comment", subComment);
				if(hostHolder.getUser() == null){
					subVo.set("liked", 0);
				}else{
					subVo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, subComment.getId()));
				}
				subVo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, subComment.getId()));
				subVo.set("user", userService.getUser(subComment.getUserId()));
				subList.add(subVo);
			}
			vo.set("subComments", subList);
			if(hostHolder.getUser() == null){
				vo.set("liked", 0);
			}else{
				vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, comment.getId()));
			}
			vo.set("reply",commentService.countComment(comment.getId(), EntityType.ENTITY_COMMENT));
			vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
			vo.set("user", userService.getUser(comment.getUserId()));
			vos.add(vo);
		}
		model.addAttribute("comments", vos);
		return "detail";
	}
}
