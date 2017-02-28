package com.bjtu.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bjtu.dao.LoginTicketDAO;
import com.bjtu.dao.UserDAO;
import com.bjtu.model.LoginTicket;
import com.bjtu.model.User;
import com.bjtu.util.WendaUtil;

@Service
public class UserService {

	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private LoginTicketDAO loginTicketDAO;
	
	public Map<String,String> register(String username,String password){
		Map<String,String> map = new HashMap<String,String>();
		if(StringUtils.isBlank(username)){
			map.put("msg", "用户名不能为空");
			return map;
		}
		if(StringUtils.isBlank(password)){
			map.put("msg", "密码不能为空");
			return map;
		}
		User user = userDAO.selectByName(username);
		if(user!=null){
			map.put("msg", "用户名已经被注册");
			return map;
		}
		
		user = new User();
		user.setName(username);
		user.setSalt(UUID.randomUUID().toString().replaceAll("-", ""));
		user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
		user.setPassword(WendaUtil.MD5(password+user.getSalt()));
		
		if(userDAO.addUser(user)>0){
			String ticket = addLoginTicket(user.getId());
			map.put("ticket", ticket);
		}
		return map;
	}
	
	public Map<String,String> login(String username,String password){
		Map<String,String> map = new HashMap<String,String>();
		if(StringUtils.isBlank(username)){
			map.put("msg", "用户名不能为空");
			return map;
		}
		if(StringUtils.isBlank(password)){
			map.put("msg", "密码不能为空");
			return map;
		}
		User user = userDAO.selectByName(username);
		if(user==null){
			map.put("msg", "用户名不存在");
			return map;
		}
		
		if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
			map.put("msg", "密码错误");
			return map;
		}
		
		String ticket = addLoginTicket(user.getId());
		map.put("ticket", ticket);
		return map;
	}
	
	public String addLoginTicket(int userId){
		LoginTicket loginTicket = new LoginTicket();
		loginTicket.setUserId(userId);
		Date now = new Date();
		now.setTime(now.getTime()+3600*24*100);
		loginTicket.setExpired(now);
		loginTicket.setStatus(0);
		loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
		loginTicketDAO.addTicket(loginTicket);
		return loginTicket.getTicket();
	}
	
	public void logout(String ticket){
		loginTicketDAO.updateStatus(ticket, 1);
	}
	public User getUser(int id){
		return userDAO.selectById(id);
	}
	public User getUserByName(String name){
		return userDAO.selectByName(name);
	}
}
