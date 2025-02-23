package cat.community.nyangmunity.image.batch.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ImageBatchServiceTest {

	@Autowired
	private  ImageBatchService imageBatchService;

	@Test
	@DisplayName("배치 작업이 정상적으로 작동한다")
	public void testBatch() {
		imageBatchService.runImageBatch();
	}

}