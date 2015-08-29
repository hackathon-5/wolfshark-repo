package us.hexcoder.polyticks.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.hexcoder.polyticks.dto.UserDTO;
import us.hexcoder.polyticks.exception.ResourceNotFoundException;
import us.hexcoder.polyticks.service.UserService;

/**
 * Created by 67726e on 8/29/15.
 */
@RestController
@RequestMapping("/rest/users")
public class UserRestService extends AbstractRestController {
	@Autowired
	private UserService userService;

	@RequestMapping(value = "/current", method = RequestMethod.GET)
	public UserDTO getCurrentUser() {
		return userService.findCurrentUserDTO()
				.orElseThrow(ResourceNotFoundException::new);
	}
}
