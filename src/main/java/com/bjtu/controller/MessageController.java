package com.bjtu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjtu.model.HostHolder;
import com.bjtu.model.Message;
import com.bjtu.model.User;
import com.bjtu.model.ViewObject;
import com.bjtu.service.MessageService;
import com.bjtu.service.UserService;
import com.bjtu.util.WendaUtil;

@Controller
public class MessageController {
	private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

	@Autowired
	UserService userService;
	
	@Autowired
	HostHolder hostHolder;
	
	@Autowired
	MessageService messageService;
	
	@RequestMapping(value="/msg/addMessage",method={RequestMethod.POST})
	@ResponseBody
	public String addMessage(@RequestParam("toName")String toName,
			@RequestParam("content")String content){
		User fromUser = hostHolder.getUser();
		if(fromUser == null){
			return WendaUtil.getJSONString(999);
		}
		User toUser = userService.getUserByName(toName);
		if(toUser==null){
			return WendaUtil.getJSONString(1,"用户不存在");
		}
		Message message = new Message();
		message.setContent(content);
		message.setToId(toUser.getId());
		message.setFromId(fromUser.getId());
		message.setHasRead(0);
		message.setCreatedDate(new Date());
		message.setConversationId(message.getConversationId());
		if(messageService.addMessage(message)>0){
			return WendaUtil.getJSONString(0);
		}else{
			return WendaUtil.getJSONString(1, "发送失败");
		}
	}
	
	@RequestMapping(value="/msg/list",method={RequestMethod.GET})
	public String getConversationList(Model model){
		User fromUser = hostHolder.getUser();
		if(fromUser == null){
			return WendaUtil.getJSONString(999);
		}
		List<Message> messageList  = messageService.getConversationList(fromUser.getId(), 0, 10);
		List<ViewObject> vos = new ArrayList<ViewObject>();
		for(Message message:messageList){
			ViewObject vo = new ViewObject();
			vo.set("conversation", message);
			if(message.getFromId()==fromUser.getId()){
				int targetId = message.getToId();
				vo.set("user", userService.getUser(targetId));
			}else{
				vo.set("user", hostHolder.getUser());
			}
			vo.set("unread",messageService.getConversationUnreadCount(hostHolder.getUser().getId(), message.getConversationId()));
			vos.add(vo);	
		}
		model.addAttribute("conversations", vos);
		return "letter";
	}
	
	@RequestMapping(value="/msg/detail",method={RequestMethod.GET})
	public String getConversationDetail(Model model,
			@RequestParam("conversationId")String conversationId){
		List<Message> messageList  = messageService.getConversationDetail(conversationId, 0,10);
		List<ViewObject> vos = new ArrayList<ViewObject>();
		for(Message message:messageList){
			ViewObject vo = new ViewObject();
			vo.set("message", message);
			vo.set("user", userService.getUser(message.getFromId()));
			vos.add(vo);
		}
		model.addAttribute("messages", vos);
		return "letterDetail";
	}
}
