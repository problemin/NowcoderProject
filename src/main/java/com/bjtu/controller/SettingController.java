package com.bjtu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjtu.service.WendaService;

@Controller
public class SettingController {
	@Autowired
	WendaService wendaService;
	
	@RequestMapping(path={"/setting"},method={RequestMethod.GET})
	@ResponseBody
	public String setting(){
		return "Setting OK. "+ wendaService.getMessage(1);
	}
}
