package pl.nadworny.cocktail.fileprocessing;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class FileProcessor {

	private Logger logger = Logger.getLogger(FileProcessor.class);

	private static int countMojitos = 0;
	private static int countPlantersPunch = 0;
	private static int countSeaBreeze = 0;
	private static int countScrewdrivers = 0;

	public String process(File file) throws Exception {
		String destinationChannel = null;

		try {

			StringTokenizer st = new StringTokenizer(file.getName(), "-.");
			String orderPrefix = st.nextToken();
			String sequence = st.nextToken();

			Properties cocktail = new Properties();
			FileInputStream is = FileUtils.openInputStream(file);
			cocktail.load(is);
			is.close();

			String cocktailName = cocktail.getProperty("cocktailName");
			int cocktailCount = Integer.parseInt(cocktail.getProperty("cocktailCount"));
			String orderedAt = cocktail.getProperty("orderedAt");

			logger.trace("The order with the sequence number " + sequence + " has been picked up for processing.");

			Calendar cal = new GregorianCalendar();
			cal.setTime(new Date());
			if (cal.get(Calendar.HOUR_OF_DAY) > 4 && cal.get(Calendar.HOUR_OF_DAY) < 18) {
				logger.trace("The order with the sequence number " + sequence + " and the order time stamp " + orderedAt
						+ " has been ignored.");
				destinationChannel = "filesOutChannelIgnored";
				return destinationChannel;
			}

			destinationChannel = "filesOutChannel";

			int showSummary = 0;
			switch (CocktailNameEnum.valueOf(cocktailName)) {
			case MOJITO:
				showSummary = showCocktailSummay(countMojitos, cocktailCount);
				countMojitos += cocktailCount;
				break;
			case PLANTERS_PUNCH:
				showSummary = showCocktailSummay(countPlantersPunch, cocktailCount);
				countPlantersPunch += cocktailCount;
				break;
			case SCREWDRIVER:
				showSummary = showCocktailSummay(countScrewdrivers, cocktailCount);
				countScrewdrivers += cocktailCount;
				break;
			case SEA_BREEZE:
				showSummary = showCocktailSummay(countSeaBreeze, cocktailCount);
				countSeaBreeze += cocktailCount;
				break;
			default:
				logger.trace("The order with the sequence number " + sequence + " and cocktail name " + cocktailName
						+ " has been rejected.");
				destinationChannel = "filesOutChannelRejected";
				return destinationChannel;
			}

			logger.info("The waiting will soon be over, your order " + sequence + " is processed.");
			logger.trace("The order with the sequence number " + sequence + " has been successfully processed.");
			logger.info("Cheers! " + cocktailCount + " " + cocktailName + " drink(s) for order " + sequence
					+ " is/are ready to serve.");

			if (showSummary > 0)
				logger.info(showSummary + " " + cocktailName + "s have been served.");
		} catch (Exception ex) {
			logger.error("Order rejected.", ex);
			destinationChannel = "filesOutChannelRejected";
		}

		return destinationChannel;
	}

	private int showCocktailSummay(int current, int cocktailCount) {
		for (int i = current; i < current + cocktailCount; i++) {
			if (i % 5 == 0)
				return i;
		}
		return 0;
	}

	public enum CocktailNameEnum {
		MOJITO, SEA_BREEZE, PLANTERS_PUNCH, SCREWDRIVER
	}
}
