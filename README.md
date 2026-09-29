# 🧺 Smart Pantry Manager

Is a Java Android app that tracks the ingredients you have at home and based on that, it will suggest recipes you can cook **using only what is already in your pantry**, to help cut food waste. 💖

> This assignment is a school project for the module; Mobile App Development 700 – Practical Assignment

## ✨ Features

- **Pantry management (full CRUD):** You can add, view, edit and delete ingredients
  (name, quantity, unit, optional expiry date) with input validation
- **Pantry list:** RecyclerView with a custom adapter, food emojis, and
  expiry highlighting (yellow = expiring within 3 days, red = expired), so that you can easily track the condition of the food items you have in the pantry
- **Recipe book:** It has 22 recipes pre-loaded into the database on first launch
- **Suggested recipes (strict matching):** a recipe is shown only if *every*
  ingredient is in the pantry in *at least* the required quantity
  - Names are normalised (`Tomatoes` = `tomato`, `Scallions` = `spring onion`)
  - Units are converted (`1 kg` = `1000 g`, `2 tbsp` = `30 ml`)
  - A friendly message is shown when nothing matches
- **Almost there (bonus):** a separate, clearly labelled list of recipes missing
  exactly one ingredient, that way there's more room for recipe and combo possibilities (can be switched off in Settings)
- **Recipe detail:** full ingredient list (✅ have / ❌ missing) and method
- **Recipe Book screen:** browse all 22 recipes, each showing whether it's ready to cook or how many ingredients are missing
- **Splash screen:** animated intro shown when the app opens
- **Settings:** expiry highlighting, "almost there" toggle, clear pantry

## 🗄️ Database choice: SQLite (Room)

- I chose SQLite(with Room) for my database because, it is personal and the data is kept locally, each persons pantry is individual to them, and doesn't have to be shared with other users, so it doesn't have to be cloud-based like Firebase or PostgreSQL.
- It works offline, in the event that you quickly want to check your pantry or in the grocery store as you're shopping, and you don't have an internet connection, Sqlite lives on the phone itself, so it'll always work.
- It doesn't need any extra setup or accounts, users don't have to log-in, and backend wise its also an easy set up, no servers needed, or REST APIs, the database already comes with the device.
- The data shape works best with sqlite, the ingredients and recipes have one-to-many relationship, and sqlite is a relational database, and a foreign key links each ingredient to its recipe, so deleting a recipe also removes its ingredients automatically, its a cascading effect.
- Room makes the code safer and cleaner, any sql queries are checked during the building of the app, so typo and things of that nature are caught before the app even runs, this removes the need for the repetitive code of the older SQLLiteOpenHelper approach.
- In comparison to databases that have servers and more extensive set-up, this option does come with a trade-off, the user's pantry data stays on one device. There's no backup or sync, so if something happens to the phone, that data will be lost.

Tables: `pantry_items`, `recipes`, `recipe_ingredients`
(one recipe → many ingredients via a foreign key).

## 🧱 Project structure

```
app/src/main/java/com/ntokozo/smartpantry/
├── SplashActivity.java            # 🌸 Animated splash screen (launcher)
├── MainActivity.java              # 🧺 Pantry list
├── AddEditItemActivity.java       # ✏️ Add / edit ingredient
├── SuggestedRecipesActivity.java  # 🍳 Strict suggestions + almost there
├── RecipeBookActivity.java        # 📖 Browse all recipes
├── RecipeDetailActivity.java      # 🧾 Recipe detail
├── SettingsActivity.java          # ⚙️ Settings
├── adapter/                       # RecyclerView adapters (pantry + recipes)
├── data/                          # Room entities, DAOs, database, recipe seeder
├── logic/                         # Strict-matching rule (pure Java, unit tested)
└── util/                          # Emoji picker, formatting, settings
```

## 🛠️ Tech

- Java 17, Android SDK 35 (min SDK 26 / Android 8.0)
- Room (SQLite), RecyclerView, Material Components 3

## ▶️ Setup and run

Requirements: JDK 17 and the Android SDK (Android Studio, or the command-line tools).

```bash
git clone https://github.com/ntokozomas/SmartPantryManager.git
cd SmartPantryManager
./gradlew installDebug          # build and install on a running emulator/device
./gradlew testDebugUnitTest     # run the strict-matching unit tests
```

Or open the folder in Android Studio and press **Run**.
