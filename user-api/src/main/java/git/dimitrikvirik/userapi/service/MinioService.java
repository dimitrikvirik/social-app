package git.dimitrikvirik.userapi.service;

import git.dimitrikvirik.userapi.utils.ImageUtils;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioService {

	private final MinioClient minioClient;


	@Value("${minio.bucket}")
	private String bucket;

	@Value("${minio.url}")
	private String url;

	public String upload(MultipartFile file) {
		try {
			String contentType = file.getContentType();
			assert contentType != null;
			if (!contentType.startsWith("image")) {
				throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "File is not an image");
			}

			String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
			File tempFile = File.createTempFile(filename, ".tmp");

			ImageUtils.rescale(tempFile.getAbsolutePath(), 360, 7, file);
			UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder().contentType(contentType).filename(tempFile.getAbsolutePath()).object(filename).bucket(bucket).build();

			minioClient.uploadObject(uploadObjectArgs);
			tempFile.delete();
			return url + "/" + bucket + "/" + filename;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Minio error");
		}
	}


}
