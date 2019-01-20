package model.order;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Order {

	StringBuilder kiteEntry;
	{
		kiteEntry = new StringBuilder();
	}

	private boolean betweenExclusive(float x, float min, float max) {

		return x > min && x < max;
	}

	private float roundOFF(float value) {

		return (float) (((5 * (Math.round((value * 100) / 5.0)))) / 100.0);
	}

	private JSONObject basicBOOrder(String exchange, String tradingSymbol, int quantity, String transacType,
			String orderType, float entryPrice, float triggerPrice, float exitPrice, float slPrice, float trailingSL) {

		return new JSONObject().put("exchange", exchange).put("tradingsymbol", tradingSymbol).put("quantity", quantity)
				.put("transaction_type", transacType).put("order_type", orderType).put("price", roundOFF(entryPrice))
				.put("trigger_price", roundOFF(triggerPrice)).put("stoploss", roundOFF(slPrice))
				.put("squareoff", roundOFF(exitPrice)).put("variety", "bo")
				.put("trailing_stoploss", roundOFF(trailingSL));
	}

	private void template1(List<String> csvLine) {

		final int R1_INDEX = 32, R2_INDEX = 31, R3_INDEX = 30, CP_INDEX = 7, PP_INDEX = 33, TRADING_SYMBOL_INDEX = 3;
		float R1 = Float.parseFloat(csvLine.get(R1_INDEX)), R2 = Float.parseFloat(csvLine.get(R2_INDEX)),
				R3 = Float.parseFloat(csvLine.get(R3_INDEX)), currentPrice = Float.parseFloat(csvLine.get(CP_INDEX)),
				PP = Float.parseFloat(csvLine.get(PP_INDEX));

		// Current Price is between R1 and R2; Sell at R2

		if (!betweenExclusive(currentPrice, 20, 500)) {

			System.out
					.println("Price not in range. (" + csvLine.get(TRADING_SYMBOL_INDEX) + "): (" + currentPrice + ")");
			return;
		}
		if (betweenExclusive(currentPrice, R1, R2)) {

			// Place Basic Order
			kiteEntry.append("kite.add("
					+ basicBOOrder("NSE", csvLine.get(TRADING_SYMBOL_INDEX), 1, "SELL", "SL", R2, R2,
							(currentPrice / 171), (currentPrice / 171), 0.5f).toString()
					+ ");" + System.lineSeparator());
			;
		} else if (betweenExclusive(currentPrice, R2, R3)) {

			// Place Basic Order
			kiteEntry.append("kite.add(" + basicBOOrder("NSE", csvLine.get(TRADING_SYMBOL_INDEX), 1, "SELL", "SL", R3,
					R3, (currentPrice / 171), (currentPrice / 171), 0.5f) + ");" + System.lineSeparator());
		} else if (betweenExclusive(currentPrice, PP, R1)) {

			// Place Basic Order
			kiteEntry.append("kite.add(" + basicBOOrder("NSE", csvLine.get(TRADING_SYMBOL_INDEX), 1, "SELL", "SL", R1,
					R1, (currentPrice / 171), (currentPrice / 171), 0.5f) + ");" + System.lineSeparator());
		}
		// No mater what, Sell at PP
		if (currentPrice > PP) {

			kiteEntry.append("kite.add(" + basicBOOrder("NSE", csvLine.get(TRADING_SYMBOL_INDEX), 1, "SELL", "SL", PP,
					PP, (currentPrice / 171), (currentPrice / 171), 0.5f) + ");" + System.lineSeparator());
		}

		return;
	}

	public String csv2Json(ArrayList<List<String>> csvContent, int templateID) {

		switch (templateID) {

		case 1:

			csvContent.forEach(csvLine -> {

				template1(csvLine);
			});

			System.out.println("JSON Content: (" + kiteEntry + ")");
			break;
		default:
			System.out.println("Invalid Order Template");
			return null;
		}

		return kiteEntry.toString();
	}

	// Exchange Scrip Name Current High Low Quantity

}