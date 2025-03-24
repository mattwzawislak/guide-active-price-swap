package gaps;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ScriptEvent;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class GrandExchangePriceManager
{
	private static final int VARBIT_GRAND_EXCHANGE_OFFER_PRICE = 4398;
	private static final int GRAND_EXCHANGE_REFRESH_UI_SCRIPT_ID = 779;
	private static final int GRAND_EXCHANGE_OFFER_CONFIRM = 30474269;

	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	public boolean isOfferSetToActivePrice()
	{
		final int activePrice = getActivePrice();
		final int offerPrice = getOfferPrice();
		return offerPrice == activePrice;
	}

	public void setActivePrice()
	{
		final int activePrice = getActivePrice();
		final int offerPrice = getOfferPrice();
		if (activePrice == -1)
		{
			log.trace("Asked to set active price but offer was not set");
			return;
		}

		if (activePrice == offerPrice)
		{
			log.trace("Offer price is already set to active price");
			return;
		}

		setOfferPrice(activePrice);

		final Widget grandExchangeContainer = client.getWidget(ComponentID.GRAND_EXCHANGE_OFFER_CONTAINER);
		final ScriptEvent scriptEvent = client.createScriptEvent(GRAND_EXCHANGE_REFRESH_UI_SCRIPT_ID,
				0,
				ComponentID.GRAND_EXCHANGE_OFFER_CONTAINER,
				0, // Item icon container
				6, // +1k
				50, // +1k
				20, // "Buy offer" / "Sell offer"
				21, // Buy icon / Sell icon
				22, // Item icon container
				23, // Item icon
				27, // Item name
				34, // Quantity number picker
				41, // Price number picker
				45, // Total price
				46, // Search button
				GRAND_EXCHANGE_OFFER_CONFIRM,
				58,
				7);
		scriptEvent.setSource(grandExchangeContainer);
		scriptEvent.setOp(1);
		scriptEvent.run();
	}

	private int getOfferItemId()
	{
		return client.getVarpValue(VarPlayer.CURRENT_GE_ITEM);
	}

	private int getOfferPrice()
	{
		return client.getVarbitValue(VARBIT_GRAND_EXCHANGE_OFFER_PRICE);
	}

	private void setOfferPrice(int offerPrice)
	{
		client.setVarbit(VARBIT_GRAND_EXCHANGE_OFFER_PRICE, offerPrice);
	}

	private int getActivePrice()
	{
		final int itemId = getOfferItemId();
		if (itemId == -1)
		{
			return -1;
		}

		return itemManager.getItemPrice(itemId);
	}
}
