package com.bjtu.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bjtu.model.Question;
import com.bjtu.model.ViewObject;
import com.bjtu.service.QuestionService;
import com.bjtu.service.UserService;

@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	QuestionService questionService;
	
	@Autowired 
	UserService userService;
	
	@RequestMapping(path={"/user/{userId}"},method=RequestMethod.GET)
	public String userIndex(Model model,@PathVariable("userId") int userId){
		System.out.println(userId);
		model.addAttribute("vos",getQuestions(userId,0,10));
		return "index";
	}
	@RequestMapping(path={"/","/index"},method={RequestMethod.GET})
	public String index(Model model){
		model.addAttribute("vos",getQuestions(0,0,10));
		return "index";
	}
	
	private List<ViewObject> getQuestions(int userId,int offset,int limit){
		List<Question> questionList = questionService.getLastestQuestions(userId, 0, 10);
		List<ViewObject> vos = new ArrayList<ViewObject>();
		for(Question question:questionList){
			ViewObject vo = new ViewObject();
			vo.set("question", question);
			vo.set("user", userService.getUser(question.getUserId()));
			vos.add(vo);
		}
		return vos;
	}
}
