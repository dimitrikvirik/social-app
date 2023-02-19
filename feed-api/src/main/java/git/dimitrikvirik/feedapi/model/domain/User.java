package git.dimitrikvirik.feedapi.model.domain;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {

	@Id
	public String id;

	private String firstname;

	private String lastname;

	private String photo;

}
