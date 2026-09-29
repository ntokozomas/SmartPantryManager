package com.ntokozo.smartpantry.util;

import com.ntokozo.smartpantry.logic.IngredientNormalizer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Picks a cute food emoji for an ingredient name.
 * Order matters: more specific words are checked first (e.g. "pineapple" before "apple").
 */
public final class EmojiHelper {

    private static final String DEFAULT_EMOJI = "🧺";
    private static final Map<String, String> KEYWORDS = new LinkedHashMap<>();

    static {
        KEYWORDS.put("eggplant", "🍆");
        KEYWORDS.put("egg", "🥚");
        KEYWORDS.put("tomato", "🍅");
        KEYWORDS.put("sweet potato", "🍠");
        KEYWORDS.put("potato", "🥔");
        KEYWORDS.put("onion", "🧅");
        KEYWORDS.put("garlic", "🧄");
        KEYWORDS.put("carrot", "🥕");
        KEYWORDS.put("bell pepper", "🫑");
        KEYWORDS.put("chili", "🌶️");
        KEYWORDS.put("broccoli", "🥦");
        KEYWORDS.put("lettuce", "🥬");
        KEYWORDS.put("spinach", "🥬");
        KEYWORDS.put("cabbage", "🥬");
        KEYWORDS.put("cucumber", "🥒");
        KEYWORDS.put("maize", "🌽");
        KEYWORDS.put("corn", "🌽");
        KEYWORDS.put("mushroom", "🍄");
        KEYWORDS.put("avocado", "🥑");
        KEYWORDS.put("chicken", "🍗");
        KEYWORDS.put("steak", "🥩");
        KEYWORDS.put("beef", "🥩");
        KEYWORDS.put("mince", "🥩");
        KEYWORDS.put("mutton", "🍖");
        KEYWORDS.put("lamb", "🍖");
        KEYWORDS.put("yeast", "🍞");
        KEYWORDS.put("bacon", "🥓");
        KEYWORDS.put("pork", "🥓");
        KEYWORDS.put("sausage", "🌭");
        KEYWORDS.put("fish", "🐟");
        KEYWORDS.put("tuna", "🐟");
        KEYWORDS.put("prawn", "🦐");
        KEYWORDS.put("shrimp", "🦐");
        KEYWORDS.put("cheese", "🧀");
        KEYWORDS.put("peanut", "🥜");
        KEYWORDS.put("coconut", "🥥");
        KEYWORDS.put("butter", "🧈");
        KEYWORDS.put("milk", "🥛");
        KEYWORDS.put("yoghurt", "🍨");
        KEYWORDS.put("bread", "🍞");
        KEYWORDS.put("rice", "🍚");
        KEYWORDS.put("pasta", "🍝");
        KEYWORDS.put("noodle", "🍜");
        KEYWORDS.put("flour", "🌾");
        KEYWORDS.put("oat", "🥣");
        KEYWORDS.put("sugar", "🍬");
        KEYWORDS.put("honey", "🍯");
        KEYWORDS.put("salt", "🧂");
        KEYWORDS.put("oil", "🫒");
        KEYWORDS.put("banana", "🍌");
        KEYWORDS.put("pineapple", "🍍");
        KEYWORDS.put("apple", "🍎");
        KEYWORDS.put("strawberry", "🍓");
        KEYWORDS.put("lemon", "🍋");
        KEYWORDS.put("orange", "🍊");
        KEYWORDS.put("grape", "🍇");
        KEYWORDS.put("peach", "🍑");
        KEYWORDS.put("mango", "🥭");
        KEYWORDS.put("cherry", "🍒");
        KEYWORDS.put("bean", "🥫");
        KEYWORDS.put("nut", "🥜");
        KEYWORDS.put("chocolate", "🍫");
        KEYWORDS.put("curry", "🍛");
        KEYWORDS.put("sauce", "🥫");
        KEYWORDS.put("coffee", "☕");
        KEYWORDS.put("tea", "🍵");
    }

    private EmojiHelper() {
    }

    public static String forIngredient(String name) {
        String normalized = IngredientNormalizer.normalize(name);
        for (Map.Entry<String, String> entry : KEYWORDS.entrySet()) {
            if (normalized.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return DEFAULT_EMOJI;
    }
}
