package us.hexcoder.polyticks.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.hexcoder.polyticks.model.CategoryModel;
import us.hexcoder.polyticks.service.CategoryService;

import java.util.List;

/**
 * Created by 67726e on 8/29/15.
 */
@RestController
@RequestMapping("/rest/categories")
public class CategoryRestController {
	@Autowired
	private CategoryService categoryService;

	@RequestMapping(method = RequestMethod.GET)
	public List<CategoryModel> getAll() {
		return categoryService.findAll();
	}
}
