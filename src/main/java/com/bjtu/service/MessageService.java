package com.bjtu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bjtu.dao.MessageDAO;
import com.bjtu.model.Message;

@Service
public class MessageService {
	
	@Autowired
	MessageDAO messageDAO;
	
	@Autowired
	SensitiveService sensitiveService;
	
	public int addMessage(Message message){
		message.setContent(sensitiveService.filter(message.getContent()));
		return messageDAO.addMessage(message) > 0?message.getId():0;
	}
	
	public List<Message> getConversationDetail(String conversationId,int offset,int limit){
		return messageDAO.selectConversationDetail(conversationId, offset, limit);
	}
	
	public List<Message> getConversationList(int userId,int offset,int limit){
		return messageDAO.selectConversationList(userId, offset, limit);
	}
	
	public int getConversationUnreadCount(int userId,String conversationId){
		return messageDAO.getConversationUnreadCount(userId, conversationId);
	}
}
