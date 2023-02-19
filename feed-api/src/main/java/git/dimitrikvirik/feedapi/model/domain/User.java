package git.dimitrikvirik.feedapi.model.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "user", createIndex = false)
public class User {

	@Id
	public String id;

}
