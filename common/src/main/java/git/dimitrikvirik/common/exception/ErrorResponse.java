package git.dimitrikvirik.common.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-02-19T22:47:08.943026600+04:00[Asia/Tbilisi]")
@lombok.Data @lombok.NoArgsConstructor @lombok.AllArgsConstructor @lombok.Builder
public class ErrorResponse {
	@JsonProperty("message")
	private String message;
	@JsonProperty("application")
	private String application;
	@JsonProperty("methodName")
	private String methodName;
	@JsonProperty("exceptionName")
	private String exceptionName;
	@JsonProperty("path")
	private String path;
	@JsonProperty("status")
	private String status;
}

