package com.mycity.place.serviceImpl;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WebClientMediaService {

	private final WebClient webClient;

	public WebClientMediaService(WebClient.Builder builder) {
		this.webClient = builder.baseUrl("http://localhost:8094").build(); // or use service discovery
	}


	public void uploadImageForPlace(MultipartFile image, long placeId, String placeName, String placeCategory, String imageName) {
		try {
			MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
			bodyBuilder.part("image", new ByteArrayResource(image.getBytes()) {
				@Override
				public String getFilename() {
					return image.getOriginalFilename();
				}
			}).contentType(MediaType.parseMediaType(image.getContentType()));

			bodyBuilder.part("placeId", placeId);
			bodyBuilder.part("placeName", placeName);
			bodyBuilder.part("category", placeCategory);
			bodyBuilder.part("imageName", imageName);

			webClient.post().uri("/image/upload") // Media service endpoint
					.contentType(MediaType.MULTIPART_FORM_DATA).bodyValue(bodyBuilder.build()).retrieve()
					.bodyToMono(String.class).subscribe(response -> System.out.println("Image uploaded: " + response),
							error -> System.err.println("Upload failed: " + error.getMessage()));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
