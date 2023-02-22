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
public class FeedUser implements UserResource {

	@Id
	public String id;

	private String firstname;

	private String lastname;

	private String photo;

	@Override
	public String getUserId() {
		return id;
	}
}
