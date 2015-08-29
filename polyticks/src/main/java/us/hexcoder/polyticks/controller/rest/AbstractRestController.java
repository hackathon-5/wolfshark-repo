package us.hexcoder.polyticks.controller.rest;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import us.hexcoder.polyticks.exception.ResourceNotFoundException;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by 67726e on 8/28/15.
 */
@RestController
public abstract class AbstractRestController {
	@ExceptionHandler(ResourceNotFoundException.class)
	public void handleResourceNotFound(HttpServletResponse response) {
		response.setStatus(404);
	}
}
