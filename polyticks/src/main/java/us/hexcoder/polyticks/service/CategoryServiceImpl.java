package us.hexcoder.polyticks.service;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.hexcoder.polyticks.model.CategoryModel;

import java.util.List;

import static us.hexcoder.polyticks.jooq.Tables.*;

/**
 * Created by 67726e on 8/29/15.
 */
@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
	@Autowired
	private DSLContext context;

	@Override
	public List<CategoryModel> findAll() {
		return context.selectFrom(CATEGORIES)
				.fetchInto(CategoryModel.class);
	}
}
