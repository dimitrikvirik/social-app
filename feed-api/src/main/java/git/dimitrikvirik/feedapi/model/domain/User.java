package git.dimitrikvirik.feedapi.model.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

	@Id
	public String id;

	private String firstname;

	private String lastname;

	private String photo;

}
