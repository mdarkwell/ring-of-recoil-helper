package com.darkwell.rorh;

import net.runelite.api.coords.WorldPoint;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Constants {
	public static final String PLUGIN_GROUP = "ringofrecoilhelper";

	public static final String PLUGIN_CONFIG_DATA_KEY = "internals.saveData";

	public static final String NOTIFY_LOW_CHARGE = "%,d charges left in your %s.";

	public static final String NOTIFY_LOW_CHARGE_SINGLE = "%,d charge left in your %s.";

	public static final String WARNING_NO_RING = "You do not have a recoil-effect ring equipped!";

	public static final String WARNING_RING = "You have a %s equipped, with %,d total charges.";

	public static final String WARNING_IMMUNE = "Warning: %s is immune to the effects of a %s!";

	public static final Color FLASH_COLOR = new Color(1f, 0f, 0f, 0.2f);
	public static final Color TRANSPARENT = new Color(1f, 1f, 1f, 0f);

	public static final int INVENTORY_CONTAINER = 93;
	public static final int EQUIPMENT_CONTAINER = 94;

	public static final int RECOIL_FULL_RECHARGE = 40;

	public static final int RING_OF_RECOIL_ID = 2550;

	public static final HashSet<Integer> RING_OF_SUFFERING_IDS = new HashSet<>(Set.of(2550, 26762, 20657, 25248, 20655));

	public static final Pattern RECOIL_CHARGE_REGEX = Pattern.compile("You can inflict (\\d+) more points of damage before a ring will shatter.");

	public static final Pattern RECOIL_ONE_CHARGE_REGEX = Pattern.compile("You can inflict one more point of damage before a ring will shatter\\.");

	public static final Pattern RECOIL_SHATTER_REGEX = Pattern.compile("(Your Ring of Recoil has shattered\\.|The ring shatters\\. Your next ring of recoil will start afresh from (\\d+) damage points\\.)");

	public static final Pattern RECOIL_DEGRADE_REGEX = Pattern.compile("You can inflict (\\d+) more points of damage before a ring will shatter\\.");

	public static final Pattern SUFFERING_CHARGE_REGEX = Pattern.compile("Your ring currently has (\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?) recoil charges remaining.");

	public static final Pattern RECOIL_FULLY_CHARGED_REGEX = Pattern.compile("The ring is fully charged\\. There would be no point in breaking it\\.");

	public static final Pattern SUFFERING_RECHARGE_REGEX = Pattern.compile("You load your ring with .*?\\. It now has (\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?) recoil charges\\.");

	public static final Pattern RECOIL_BREAK_CHARGES = Pattern.compile("Status: (\\d+) damage .*? left\\.");
	public static final int RECOIL_BREAK_CHAT_WIDGET = 219;

	public static final HashMap<Integer, String> AREA_REGIONS = new HashMap<>(Map.of(
		7, "areaGwd",
		8, "areaSucc",
		44, "areaMoons",
		10020, "areaBarbAssault",
		10030, "areaCerberus",
		10141, "areaSmoke",
		10101, "areaLithkren",
		10099, "areaLithkren"
	));

	public static final HashMap<String, WorldPoint> WARNING_TILES = new HashMap<>(Map.ofEntries(
		Map.entry("areaBarbAssault", new WorldPoint(2534, 3572, 0)),
		Map.entry("areaCerberus", new WorldPoint(1310, 1251, 0)),
		Map.entry("areaCorpBeast", new WorldPoint(2972, 4384, 2)),
		Map.entry("areaDrakes", new WorldPoint(1311, 3807, 0)),
		Map.entry("areaFightCave", new WorldPoint(2438, 5169, 0)),
		Map.entry("areaGg", new WorldPoint(3428, 3543, 2)),
		Map.entry("areaGwd", new WorldPoint(2899, 3713, 0)),
		Map.entry("areaInferno", new WorldPoint(2496, 5120, 0)),
		Map.entry("areaKq", new WorldPoint(3227, 3108, 0)),
		Map.entry("areaLithkren", new WorldPoint(3549, 10482, 0)),
		Map.entry("areaMimic", new WorldPoint(1645, 3569, 1)),
		Map.entry("areaMithrilDragons", new WorldPoint(2512, 3508, 0)),
		Map.entry("areaMoons", new WorldPoint(1439, 9598, 1)),
		Map.entry("areaMuspah", new WorldPoint(2908, 10317, 0)),
		Map.entry("areaSarachnis", new WorldPoint(1841, 9912, 0)),
		Map.entry("areaScorpia", new WorldPoint(3232, 3950, 0)),
		Map.entry("areaSucc", new WorldPoint(3039, 6433, 0)),
		Map.entry("areaSmoke", new WorldPoint(2412, 3060, 0)),
		Map.entry("areaVardorvis", new WorldPoint(1118, 3428, 0)),
		Map.entry("areaYama", new WorldPoint(1439, 10079, 0)),
		Map.entry("areaZulrah", new WorldPoint(2215, 3057, 0))
	));

	public static final HashSet<Integer> IMMUNE_NPCS = new HashSet<>(Set.of(
		6609, // Callisto
		11992, // Artio
		11482, 11483, // Champion of Scabaras
		8917, 8918, 8919, 8920, // Fragment of Seren
		8097, 8098, 8178, 8179, 8094, 8095, 8177, 8096, // Galvek
		8583, 11192, // Hespori
		10936, 10938, // Judge of Yama (A Kingdom Divided)
		11492, // Menaphite Akh
		11278, 11279, 11280, 11281, 11282, // Nex
		378, 9425, 9426, 9427, 9428, 9429, 9430, 9431, 9432, 9433, 9460, // The Nightmare
		9454, 9455, // Nightmare's Husks
		377, 9423, 9416, 9417, 9418, 9419, 9420, 9421, 9422, 9424, 11153, 11155, // Phosani's Nightmare
		9466, 9467, // Phosani's Nightmare's Husks
		7286, // Skotizo
		4797, 4798, // Slug Prince
		4416, 4417, 3240, // Undead Tree
		11177, 3745, 3746, 3747, 3744, 8241, 8242, 8243, 8244, 8245, 8246, 8247, 8248, // Ranis Drakan
		3733, 8238, 9566, 5056, 9567, 9568, 9569, 9570, 9571, // Vanstrom Klause
		9632, 3734, 3735, 3736, 3737, 3738, 3739, 8239, 8240, // Vanstrom Klause
		16204, 9579, // Lowerniel Drakan
		6610, 6504, // Venenatis
		11998, // Spindel
		12001, // Spindel's Spiderling
		6611, 6612, // Vet'ion
		5054, 12108, 6614, // Skeleton Hellhound
		11993, 11994, // Calvar'ion
		8059, 8508, 8060, 8061, // Vorkath
		12335, // Wighted Leech
		10954, 10955, 10951, 10953, 10956 // Xamphur
	));
}
