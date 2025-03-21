package gaps;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ScriptEvent;
import net.runelite.api.VarPlayer;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class GrandExchangePriceManager
{
	private static final int VARBIT_GRAND_EXCHANGE_OFFER_PRICE = 4398;
	private static final int GRAND_EXCHANGE_CHANGE_PRICE_SCRIPT_ID = 778;
	private static final int GRAND_EXCHANGE_OFFER_CONFIRM = 30474269;

	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	public boolean isOfferSetToActivePrice()
	{
		int activePrice = getActivePrice();
		int offerPrice = getOfferPrice();
		return offerPrice == activePrice;
	}

	public void setActivePrice()
	{
		int activePrice = getActivePrice();
		int offerPrice = getOfferPrice();
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

		// TODO: this doesn't work?
		final int delta = activePrice - offerPrice;
		client.runScript(
				GRAND_EXCHANGE_CHANGE_PRICE_SCRIPT_ID,
				0,
				delta,
				ScriptEvent.MENU_OP,
				ComponentID.GRAND_EXCHANGE_OFFER_CONTAINER,
				0,
				6,
				50,
				20,
				21,
				22,
				23,
				27,
				34,
				41,
				45,
				46,
				GRAND_EXCHANGE_OFFER_CONFIRM,
				58,
				7
		);
	}

	private int getOfferItemId()
	{
		return client.getVarpValue(VarPlayer.CURRENT_GE_ITEM);
	}

	private int getOfferPrice()
	{
		return client.getVarbitValue(VARBIT_GRAND_EXCHANGE_OFFER_PRICE);
	}

	private int getActivePrice()
	{
		int itemId = getOfferItemId();
		if (itemId == -1)
		{
			return -1;
		}

		return itemManager.getItemPrice(itemId);
	}
}
