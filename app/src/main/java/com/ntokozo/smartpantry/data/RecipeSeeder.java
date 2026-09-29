package com.ntokozo.smartpantry.data;

import java.util.Arrays;
import java.util.List;

/**
 * Pre-loads the recipe book (22 recipes) the first time the database is created.
 * Units follow one convention so matching works well:
 * countable things use "pcs", solids use "g", liquids use "ml".
 */
public final class RecipeSeeder {

    private RecipeSeeder() {
    }

    public static void seedIfEmpty(RecipeDao dao) {
        if (dao.count() > 0) {
            return; // already seeded
        }

        add(dao, "🍳", "Cheesy Omelette", "Fluffy eggs hugging a blanket of melted cheese.",
                steps("Whisk the eggs and milk together.",
                        "Pour into a hot, lightly greased pan and cook on medium heat.",
                        "Sprinkle cheese over one half, fold, and slide onto a plate."),
                ing("Eggs", 3, "pcs"), ing("Cheese", 50, "g"), ing("Milk", 30, "ml"));

        add(dao, "🥞", "Fluffy Pancakes", "A stack of soft pancakes for lazy mornings.",
                steps("Mix the flour and sugar in a bowl.",
                        "Whisk in the eggs and milk until smooth.",
                        "Cook ladles of batter in a hot pan until bubbles form, then flip."),
                ing("Flour", 200, "g"), ing("Eggs", 2, "pcs"), ing("Milk", 300, "ml"),
                ing("Sugar", 20, "g"));

        add(dao, "🍝", "Simple Tomato Pasta", "Cosy pasta in a quick garlicky tomato sauce.",
                steps("Boil the pasta in salted water until al dente.",
                        "Fry the chopped onion and garlic until soft.",
                        "Add chopped tomatoes and simmer for 10 minutes.",
                        "Toss the pasta through the sauce."),
                ing("Pasta", 200, "g"), ing("Tomatoes", 3, "pcs"), ing("Garlic", 2, "pcs"),
                ing("Onion", 1, "pcs"));

        add(dao, "🥪", "Grilled Cheese Sandwich", "Golden, crispy and oh-so-melty.",
                steps("Butter one side of each bread slice.",
                        "Place cheese between the unbuttered sides.",
                        "Toast in a pan on both sides until golden and melted."),
                ing("Bread", 2, "pcs"), ing("Cheese", 60, "g"), ing("Butter", 15, "g"));

        add(dao, "🍌", "Banana Smoothie", "A creamy, sweet sip of sunshine.",
                steps("Peel and slice the bananas.",
                        "Blend with the milk and honey until smooth.",
                        "Pour into a glass and enjoy straight away."),
                ing("Bananas", 2, "pcs"), ing("Milk", 250, "ml"), ing("Honey", 15, "ml"));

        add(dao, "🍚", "Egg Fried Rice", "The best way to rescue leftover rice.",
                steps("Scramble the eggs in a hot pan and set aside.",
                        "Fry the diced carrot for 2 minutes.",
                        "Add the rice and stir-fry until hot.",
                        "Stir in the eggs and sliced spring onions."),
                ing("Rice", 200, "g"), ing("Eggs", 2, "pcs"), ing("Carrot", 1, "pcs"),
                ing("Spring onions", 2, "pcs"));

        add(dao, "🥔", "Crispy Potato Wedges", "Crunchy outside, fluffy inside.",
                steps("Cut the potatoes into wedges.",
                        "Toss with oil and crushed garlic.",
                        "Roast at 200°C for 35-40 minutes, turning halfway."),
                ing("Potatoes", 4, "pcs"), ing("Oil", 30, "ml"), ing("Garlic", 1, "pcs"));

        add(dao, "🍗", "Lemon Garlic Chicken", "Zesty, juicy chicken with barely any effort.",
                steps("Rub the chicken with crushed garlic and lemon juice.",
                        "Leave to marinate for 15 minutes.",
                        "Pan-fry or roast until cooked through."),
                ing("Chicken", 500, "g"), ing("Lemon", 1, "pcs"), ing("Garlic", 3, "pcs"));

        add(dao, "🥗", "Fresh Garden Salad", "Crunchy, colourful and ready in minutes.",
                steps("Wash and tear the lettuce.",
                        "Slice the tomatoes and cucumber.",
                        "Toss everything together in a big bowl."),
                ing("Lettuce", 1, "pcs"), ing("Tomatoes", 2, "pcs"), ing("Cucumber", 1, "pcs"));

        add(dao, "🍲", "Chakalaka", "A proudly South African spicy veggie relish.",
                steps("Fry the chopped onion until soft.",
                        "Add grated carrots and diced bell pepper; cook for 5 minutes.",
                        "Stir in the chopped tomatoes and baked beans.",
                        "Simmer for 10 minutes and serve with pap."),
                ing("Onion", 1, "pcs"), ing("Carrots", 2, "pcs"), ing("Bell pepper", 1, "pcs"),
                ing("Tomatoes", 2, "pcs"), ing("Baked beans", 400, "g"));

        add(dao, "🌽", "Creamy Pap", "Smooth, comforting maize meal porridge.",
                steps("Bring the milk and a cup of water to a gentle boil.",
                        "Slowly whisk in the maize meal.",
                        "Cover and cook on low for 20 minutes, stirring now and then.",
                        "Stir in the butter before serving."),
                ing("Maize meal", 250, "g"), ing("Milk", 250, "ml"), ing("Butter", 15, "g"));

        add(dao, "🍞", "Banana Bread", "The sweetest use for spotty bananas.",
                steps("Mash the bananas and mix with melted butter.",
                        "Beat in the eggs and sugar.",
                        "Fold in the flour.",
                        "Bake in a loaf tin at 180°C for about 55 minutes."),
                ing("Bananas", 3, "pcs"), ing("Flour", 250, "g"), ing("Sugar", 100, "g"),
                ing("Eggs", 2, "pcs"), ing("Butter", 100, "g"));

        add(dao, "🥣", "Honey Banana Oats", "A warm hug of a breakfast.",
                steps("Simmer the oats in the milk for 5 minutes, stirring.",
                        "Slice the banana on top.",
                        "Drizzle with honey."),
                ing("Oats", 80, "g"), ing("Milk", 250, "ml"), ing("Honey", 15, "ml"),
                ing("Banana", 1, "pcs"));

        add(dao, "🍄", "Garlic Mushroom Toast", "Buttery mushrooms piled on crunchy toast.",
                steps("Toast the bread.",
                        "Fry the sliced mushrooms and garlic in butter until golden.",
                        "Spoon the mushrooms over the toast."),
                ing("Mushrooms", 150, "g"), ing("Bread", 2, "pcs"), ing("Butter", 15, "g"),
                ing("Garlic", 1, "pcs"));

        add(dao, "🍛", "Chicken Curry & Rice", "Warm, fragrant and perfect for sharing.",
                steps("Cook the rice.",
                        "Fry the onion until golden, then add the curry powder.",
                        "Add the chicken pieces and brown them.",
                        "Add chopped tomatoes and simmer for 20 minutes. Serve with rice."),
                ing("Chicken", 500, "g"), ing("Onion", 1, "pcs"), ing("Tomatoes", 2, "pcs"),
                ing("Curry powder", 15, "g"), ing("Rice", 200, "g"));

        add(dao, "🥔", "Buttery Mashed Potatoes", "Cloud-soft mash, the ultimate comfort food.",
                steps("Peel and boil the potatoes until tender.",
                        "Drain and mash with butter.",
                        "Beat in warm milk until creamy."),
                ing("Potatoes", 5, "pcs"), ing("Milk", 100, "ml"), ing("Butter", 30, "g"));

        add(dao, "🥦", "Rainbow Veggie Stir-Fry", "Crunchy veggies in a savoury glaze.",
                steps("Slice the carrots and bell pepper; cut the broccoli into florets.",
                        "Stir-fry the garlic for 30 seconds.",
                        "Add the vegetables and cook for 5 minutes.",
                        "Splash in the soy sauce and toss."),
                ing("Carrots", 2, "pcs"), ing("Bell pepper", 1, "pcs"), ing("Broccoli", 200, "g"),
                ing("Soy sauce", 30, "ml"), ing("Garlic", 2, "pcs"));

        add(dao, "🍓", "Strawberry Yoghurt Parfait", "Pretty layers of pink goodness.",
                steps("Slice the strawberries.",
                        "Layer yoghurt, oats and strawberries in a glass.",
                        "Finish with a drizzle of honey."),
                ing("Strawberries", 150, "g"), ing("Yoghurt", 200, "g"), ing("Oats", 40, "g"),
                ing("Honey", 10, "ml"));

        add(dao, "🧀", "Mac & Cheese", "Gooey, cheesy and impossible to share.",
                steps("Boil the pasta until just tender.",
                        "Melt the butter, then stir in the milk and heat gently.",
                        "Add the cheese and stir until smooth.",
                        "Mix the pasta into the sauce."),
                ing("Pasta", 250, "g"), ing("Cheese", 150, "g"), ing("Milk", 300, "ml"),
                ing("Butter", 30, "g"));

        add(dao, "🍅", "Shakshuka", "Eggs poached in a bubbly pepper and tomato sauce.",
                steps("Fry the onion, garlic and sliced bell pepper until soft.",
                        "Add chopped tomatoes and simmer until thick.",
                        "Make little wells and crack in the eggs.",
                        "Cover and cook until the eggs are set."),
                ing("Eggs", 4, "pcs"), ing("Tomatoes", 4, "pcs"), ing("Onion", 1, "pcs"),
                ing("Bell pepper", 1, "pcs"), ing("Garlic", 2, "pcs"));

        add(dao, "🍛", "Durban Bunny Chow", "A hollowed-out loaf filled with spicy mutton curry. Proudly Durban!",
                steps("Fry the chopped onion until golden, then add the garlic and curry powder.",
                        "Add the mutton pieces and brown them all over.",
                        "Add the chopped tomatoes and a cup of water; simmer for 1 hour.",
                        "Add the diced potatoes and cook until soft and the curry is thick.",
                        "Cut the loaf in half, scoop out the soft middle and fill with curry.",
                        "Serve with the scooped-out bread on top for dipping."),
                ing("Mutton", 500, "g"), ing("Potatoes", 2, "pcs"), ing("Onion", 1, "pcs"),
                ing("Tomatoes", 2, "pcs"), ing("Garlic", 2, "pcs"), ing("Curry powder", 20, "g"),
                ing("Bread loaf", 1, "pcs"));

        add(dao, "🥯", "Plain Vetkoek", "Golden, fluffy fried dough - perfect with jam, cheese or mince.",
                steps("Mix the flour, yeast, sugar and salt in a big bowl.",
                        "Add warm water bit by bit and knead into a soft dough.",
                        "Cover and leave in a warm place to rise for about 1 hour.",
                        "Shape into balls and let them rest for 10 minutes.",
                        "Deep-fry in hot oil until golden brown on both sides.",
                        "Drain on paper towel and enjoy warm."),
                ing("Flour", 500, "g"), ing("Yeast", 10, "g"), ing("Sugar", 15, "g"),
                ing("Salt", 5, "g"), ing("Oil", 500, "ml"));
    }

    private static void add(RecipeDao dao, String emoji, String name, String description,
                            String steps, RecipeIngredient... ingredients) {
        Recipe recipe = new Recipe(name, emoji, description, steps);
        List<RecipeIngredient> list = Arrays.asList(ingredients);
        dao.insertRecipeWithIngredients(recipe, list);
    }

    private static String steps(String... steps) {
        return String.join("\n", steps);
    }

    private static RecipeIngredient ing(String name, double quantity, String unit) {
        return new RecipeIngredient(name, quantity, unit);
    }
}
