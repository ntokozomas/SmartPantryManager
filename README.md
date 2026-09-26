# Smart Pantry Manager

A Java Android app that tracks the ingredients you have at home and suggests
recipes you can cook **using only what is already in your pantry**, to help
cut food waste.

> Mobile App Development 700 – Practical Assignment

## Features (planned)

- Add, edit and delete pantry items (name, quantity, unit, optional expiry date)
- Pantry list screen (RecyclerView bound to the database)
- Pre-loaded recipe collection (15–20 recipes)
- Suggested Recipes screen using strict matching: a recipe is only shown if
  every ingredient is in the pantry in at least the required quantity
- Recipe detail screen
- Settings screen

## Database choice

**SQLite via the Room persistence library.** _(Explain your reasons here.)_

## Tech

- Java 17, Android SDK 35 (min SDK 26)
- Room, RecyclerView, Material Components

## Setup and run

Requirements: JDK 17 and the Android SDK (Android Studio, or the command-line tools).

```bash
git clone <this-repo-url>
cd SmartPantryManager
./gradlew assembleDebug        # build
./gradlew installDebug         # install on a running emulator/device
```

Or open the folder in Android Studio and press **Run**.
