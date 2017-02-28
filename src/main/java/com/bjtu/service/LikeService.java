package com.bjtu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bjtu.util.JedisAdapter;
import com.bjtu.util.RedisKeyUtil;

@Service
public class LikeService {

	@Autowired
	JedisAdapter jedisAapter;
	
	public long getLikeCount(int entityType,int entityId){
		String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
		return jedisAapter.scard(likeKey);
	}
	
	public int getLikeStatus(int userId,int entityType,int entityId){
		String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
		if(jedisAapter.sismember(likeKey, String.valueOf(userId))){
			return 1;
		}
		String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
		return jedisAapter.sismember(disLikeKey, String.valueOf(userId))?-1:0;
	}
	
	public long like(int userId,int entityType,int entityId){
		String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
		jedisAapter.sadd(likeKey, String.valueOf(userId));
		
		String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
		jedisAapter.srem(disLikeKey, String.valueOf(userId));
		return jedisAapter.scard(likeKey);
		
	}
	
	public long dislike(int userId,int entityType,int entityId){
		String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
		jedisAapter.sadd(disLikeKey, String.valueOf(userId));
		
		String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
		jedisAapter.srem(likeKey, String.valueOf(userId));
		return jedisAapter.scard(likeKey);
	}
}
