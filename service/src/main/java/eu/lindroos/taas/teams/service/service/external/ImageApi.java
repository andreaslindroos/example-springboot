package eu.lindroos.taas.teams.service.service.external;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * Created by Andreas on 21.4.2019
 */
// This interface extends the endpoint provided through a library (external project), which allows us to easily add calls to external services.
@FeignClient(url = "${services.url.image-service}", name = "imageApi") // Base URL imported from properties
public interface ImageApi extends eu.lindroos.ladder.images.api.ImageApi {
}
