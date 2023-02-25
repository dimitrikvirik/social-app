package git.dimitrikvirik.notificationapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
	@GetMapping("/test")
	public String getNotificationsLive() {
		return "index";
	}

}
