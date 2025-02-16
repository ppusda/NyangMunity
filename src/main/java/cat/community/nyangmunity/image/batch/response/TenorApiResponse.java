package cat.community.nyangmunity.image.batch.response;

import java.util.List;

public record TenorApiResponse(
        List<TenorResponse> results
) {

}