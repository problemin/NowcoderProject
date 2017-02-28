package com.bjtu.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bjtu.util.JedisAdapter;
import com.bjtu.util.RedisKeyUtil;

@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware{
	
	@Autowired 
	JedisAdapter jedisAdapter;
	private Map<EventType,List<EventHandler>> config = new HashMap<EventType,List<EventHandler>>();
	private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
	private ApplicationContext applicationContext;
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 *  
	 *  this method is for get all the EventHandler for each EventType,
	 *  so that we can use the result config map to deal with the event message
	 *  
	 *  first,we get all the class which implements EventHanlder
	 *  second,traversal the result to reorganize every EventHanlder sub-class to its EventType
	 */
	@Override
	public void afterPropertiesSet() throws Exception { 
		
		Map<String,EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
		//get 
		if(beans !=null){
			for(Map.Entry<String, EventHandler> entry:beans.entrySet()){
				List<EventType> eventTypes = entry.getValue().getSupportEventType();
				for(EventType type:eventTypes){
					if(!config.containsKey(type)){
						config.put(type, new ArrayList<EventHandler>());
					}
					config.get(type).add(entry.getValue());
				}
			}
		}
		logger.info("EventHanlder Dict 初始化完成");
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				while(true){
					String key = RedisKeyUtil.getEventQueueKey();
					//会造成阻塞
					List<String> events = jedisAdapter.brpop(0, key);
					for(String message : events){
						System.out.println(message);
						if(message.equals(key)) continue;
						
						EventModel eventModel = JSON.parseObject(message, EventModel.class);
						if(!config.containsKey(eventModel.getType())){
							logger.error("不能识别的事件类型");
						} 
						for(EventHandler handler:config.get(eventModel.getType())){
							handler.doHandle(eventModel);
						}
					}
				}
			}
			
		});
		
		thread.start();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	

}
