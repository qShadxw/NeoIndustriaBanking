package uk.co.tmdavies.nibanking.items;

import com.tterrag.registrate.util.entry.ItemEntry;
import uk.co.tmdavies.nibanking.NIBanking;

import java.util.HashMap;

public class NIItems {
    // System Items
    public static final HashMap<String, ItemEntry<?>> NIBANKING_SYSTEM_ITEMS = new HashMap<>() {{
        put("base_coin", NIBanking.REGISTRATE.item("base_coin", prop -> new CoinItem(0, prop)).register());
        put("debit_card", NIBanking.REGISTRATE.item("debit_card", CardItem::new).register());
    }};

    // Items
    public static final HashMap<String, ItemEntry<?>> NIBANKING_ITEMS = new HashMap<>() {{
        put("copper_coin", NIBanking.REGISTRATE.item("copper_coin", prop -> new CoinItem(1, prop)).register());
        put("iron_coin", NIBanking.REGISTRATE.item("iron_coin", prop -> new CoinItem(5, prop)).register());
        put("zinc_coin", NIBanking.REGISTRATE.item("zinc_coin", prop -> new CoinItem(10, prop)).register());
        put("gold_coin", NIBanking.REGISTRATE.item("gold_coin", prop -> new CoinItem(25, prop)).register());
        put("netherite_coin", NIBanking.REGISTRATE.item("netherite_coin", prop -> new CoinItem(50, prop)).register());
    }};

    public static void register() {}
}
