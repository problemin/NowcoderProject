package com.bjtu.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class SensitiveService implements InitializingBean{

	private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		try{
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
			InputStreamReader read = new InputStreamReader(is);
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt;
			while((lineTxt = bufferedReader.readLine())!=null){
				addWord(lineTxt.trim());
			}
			bufferedReader.close();
		}catch(Exception e){
			logger.error("读取敏感词文件失败"+e.getMessage());
		}
	}
	
	
	private void addWord(String lineTxt){
		 TrieNode temp = rootNode;
		 for(int i=0;i<lineTxt.length();i++){
			 Character c = lineTxt.charAt(i);
			 
			 TrieNode node = temp.getSubNode(c);
			 if(node==null){
				 node = new TrieNode();
				 temp.addSubNode(c, node);
			 }
			 temp = node;
			 if(i == lineTxt.length()-1){
				 node.setkeywordEnd(true);
			 }
		 }
	}
	
	private class TrieNode{
		
		private boolean end = false;
		
		private Map<Character,TrieNode> subNodes = new HashMap<Character,TrieNode>();
	
		public void addSubNode(Character key,TrieNode node){
			subNodes.put(key, node);
		}
		
		TrieNode getSubNode(Character key){
			return subNodes.get(key);
		}
		
		boolean isKeyWordEnd(){ 
			return end;
		}
		
		void setkeywordEnd(boolean end){
			this.end = end;
		}
	}
	
	private TrieNode rootNode = new TrieNode();

	public String filter(String text){
		if(StringUtils.isBlank(text)){
			return text;
		}
		
		StringBuilder result = new StringBuilder();
		String replacement="***";
		TrieNode temp = rootNode;
		int begin=0;
		int position=0;
		while(position < text.length()){
			char c = text.charAt(position);
			if(isSymbol(c)){
				if(temp == rootNode){
					result.append(text.charAt(begin));
					++begin;
				}
				++position;
				continue;
			}
			temp = temp.getSubNode(c);
			if(temp==null){
				result.append(text.charAt(begin));
				position = begin + 1;
				begin = position;
				temp = rootNode;
			}else if(temp.isKeyWordEnd()){
				result.append(replacement);
				position = position + 1;
				begin = position;
				temp = rootNode;
			}else {
				++position;
			}
		}
		result.append(text.substring(begin));
		return result.toString();
	}
	
	private boolean isSymbol(char c){
		int ic = (int) c;
		return !CharUtils.isAsciiAlphanumeric(c)&&(ic < 0x2E80 ||ic > 0x9FFF);
	}
	public static void main(String[] args) {
		SensitiveService s = new SensitiveService();
		s.addWord("你妈逼");
		s.addWord("色情");
		System.out.println(s.filter("你好色 情"));
	}
}
