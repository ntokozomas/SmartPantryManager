# 🧺 Smart Pantry Manager

A Java Android app that tracks the ingredients you have at home and suggests
recipes you can cook **using only what is already in your pantry**, to help
cut food waste. 💖

> Mobile App Development 700 – Practical Assignment

## ✨ Features

- **Pantry management (full CRUD):** add, view, edit and delete ingredients
  (name, quantity, unit, optional expiry date) with input validation
- **Pantry list:** RecyclerView with a custom adapter, food emojis, and
  expiry highlighting (yellow = expiring within 3 days, red = expired)
- **Recipe book:** 20 recipes pre-loaded into the database on first launch
- **Suggested recipes (strict matching):** a recipe is shown only if *every*
  ingredient is in the pantry in *at least* the required quantity
  - Names are normalised (`Tomatoes` = `tomato`, `Scallions` = `spring onion`)
  - Units are converted (`1 kg` = `1000 g`, `2 tbsp` = `30 ml`)
  - A friendly message is shown when nothing matches
- **Almost there (bonus):** a separate, clearly labelled list of recipes missing
  exactly one ingredient (can be switched off in Settings)
- **Recipe detail:** full ingredient list (✅ have / ❌ missing) and method
- **Settings:** expiry highlighting, "almost there" toggle, clear pantry

## 🗄️ Database choice: SQLite (Room)

_(Write your own justification here, e.g. works fully offline, no account or
network needed, data stays on the device, Room checks SQL queries at compile time.)_

Tables: `pantry_items`, `recipes`, `recipe_ingredients`
(one recipe → many ingredients via a foreign key).

## 🧱 Project structure

```
app/src/main/java/com/ntokozo/smartpantry/
├── MainActivity.java              # 🧺 Pantry list
├── AddEditItemActivity.java       # ✏️ Add / edit ingredient
├── SuggestedRecipesActivity.java  # 🍳 Strict suggestions + almost there
├── RecipeDetailActivity.java      # 🧾 Recipe detail
├── SettingsActivity.java          # ⚙️ Settings
├── adapter/                       # RecyclerView adapters
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
