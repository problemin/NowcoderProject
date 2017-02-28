package com.bjtu.async.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bjtu.async.EventHandler;
import com.bjtu.async.EventModel;
import com.bjtu.async.EventType;
import com.bjtu.model.Message;
import com.bjtu.model.User;
import com.bjtu.service.MessageService;
import com.bjtu.service.UserService;
import com.bjtu.util.WendaUtil;
@Component
public class LikeHandler implements EventHandler{

	@Autowired
	MessageService messageService;
	
	@Autowired
	UserService userService;
	
	@Override
	public void doHandle(EventModel model) {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.setFromId(WendaUtil.SYSTEM_USERID);
		message.setToId(model.getEntityOwnerId());
		message.setCreatedDate(new Date());
		User user = userService.getUser(model.getActorId());
		message.setContent("用户"+user.getName()+"赞了你的评论.http://localhost:8080/question/"+model.getExt("questionId"));
		messageService.addMessage(message);
	}

	@Override
	public List<EventType> getSupportEventType() {
		// TODO Auto-generated method stub
		return Arrays.asList(EventType.LIKE);
	}

}
