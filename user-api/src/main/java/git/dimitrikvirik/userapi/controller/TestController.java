package git.dimitrikvirik.userapi.controller;


import git.dimitrikvirik.user.api.TestApi;
import git.dimitrikvirik.user.model.TestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController implements TestApi {

	@Override
	public ResponseEntity<TestResponse> test() {
		TestResponse body = new TestResponse();
		body.setName("Dimitri");
		return ResponseEntity.ok(body);
	}
}
