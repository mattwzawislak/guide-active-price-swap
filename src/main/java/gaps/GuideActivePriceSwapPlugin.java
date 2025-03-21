package gaps;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
		name = "Guide Active Price Swap",
		description = "Swaps the guide price for the active price on the Grand Exchange",
		tags = {"swap", "guide", "active", "ge", "grand exchange", "wiki", "price"}
)
public class GuideActivePriceSwapPlugin extends Plugin
{
	private static final int GRAND_EXCHANGE_OFFER_GUIDE_PRICE = 11;
	private static final String ActivePriceOption = "Active Price";

	@Inject
	private GrandExchangePriceManager priceManager;

	@Inject
	private Client client;

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded menuEntryAdded)
	{
		// Not adding menu entry options to the guide price button
		if (menuEntryAdded.getActionParam0() != GRAND_EXCHANGE_OFFER_GUIDE_PRICE ||
			menuEntryAdded.getActionParam1() != ComponentID.GRAND_EXCHANGE_OFFER_CONTAINER)
		{
			return;
		}

		// Only when adding the guide price menu entry
		if (!menuEntryAdded.getOption().equals("Guide price"))
		{
			return;
		}

		// Deprioritize the guide price entry if the offer is not the active price
		if (!this.priceManager.isOfferSetToActivePrice())
		{
			final MenuEntry guidePriceMenuEntry = menuEntryAdded.getMenuEntry();
			guidePriceMenuEntry.setDeprioritized(true);
		}

		// Add the active price menu entry
		final Menu menu = client.getMenu();
		final MenuEntry activePrice = menu.createMenuEntry(1);
		activePrice.setOption(ActivePriceOption);
		activePrice.setType(MenuAction.RUNELITE);
		activePrice.setParam0(GRAND_EXCHANGE_OFFER_GUIDE_PRICE);
		activePrice.setParam1(ComponentID.GRAND_EXCHANGE_OFFER_CONTAINER);
		activePrice.onClick(e -> priceManager.setActivePrice());
	}
}
