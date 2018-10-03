package com.kj.repo.boot.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmojiController {
	
	@RequestMapping("/emoji")
	public void emoji(HttpServletResponse reponse) throws IOException {
		reponse.setContentType("text/plain;charset=UTF-8");
		reponse.getWriter().println("⬇️⬇️⬇");
	}

}
