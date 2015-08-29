package us.hexcoder.polyticks.controller.site;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import us.hexcoder.twirl.view.TwirlView;

/**
 * Created by 67726e on 8/28/15.
 */
@Controller
public class IndexController {
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public TwirlView getIndex() {
		return TwirlView.ok(html.Index.apply());
	}
}
