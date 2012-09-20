package pl.nadworny.cocktail.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY-HH-mm-ss-SSS");

	public String getGenerateTime() {
		return sdf.format(new Date());
	}
}
