<div align="center">

<a href="https://androai.io/webinar/register">
  <img src="https://img.shields.io/badge/🚀%20FREE%20WEBINAR-Build%20Your%20Own%20Android%20Apps%20Like%20This-FF4E7A?style=for-the-badge&labelColor=5B21B6" alt="Register for the AndroAI Webinar" />
</a>

### 🎓 Want to build Android apps like this one?

**Join the free live webinar by [AndroAI](https://androai.io) and learn how to build, design and publish your own Android apps, step by step.**

<a href="https://androai.io/webinar/register"><b>👉 Register for the Webinar Now: androai.io/webinar/register 👈</b></a>

[🌐 Website](https://androai.io) &nbsp;•&nbsp; [📚 Courses](https://androai.io/courses) &nbsp;•&nbsp; [🛒 Source Code Market](https://androai.io/market)

---

# ✊ ✋ ✌️ Rock · Paper · Scissors

**A beautiful, animated Rock Paper Scissors game for Android, built with Kotlin and Jetpack Compose.**

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange?style=flat-square)
![Material 3](https://img.shields.io/badge/Design-Material%203-EA4C89?style=flat-square)

</div>

---

## 📸 Screenshots

<div align="center">

| Home | Rock… Paper… Shoot! | Round Result | Match Over |
|:---:|:---:|:---:|:---:|
| <img src="screenshots/01_home.png" width="200" alt="Home screen" /> | <img src="screenshots/02_shaking.png" width="200" alt="Shaking animation" /> | <img src="screenshots/03_result.png" width="200" alt="Round result" /> | <img src="screenshots/04_match_over.png" width="200" alt="Match over dialog" /> |

</div>

---

## ✨ Features

- 🎨 **Animated gradient background** with a glassmorphism UI
- ✊ **"Rock… Paper… Shoot!" shake animation** before every reveal
- 🏆 **First to 5 wins**: live scoreboard with progress dots for You vs CPU
- 🔥 **Win streak** tracking
- 🎉 **Round result banner**: green for a win, red for a loss, yellow for a draw
- 🤖 **Match-over dialog** with final score and a one-tap **Play Again**
- 🔄 **Reset** at any time
- 📱 Edge-to-edge layout, works on phones of any size, and keeps the score on screen rotation
- ✅ Unit tests for the game rules

---

## 📥 How to Download This Source Code

### Option 1: Download ZIP (easiest, no Git needed)

1. Click the green **`<> Code`** button at the top of this page.
2. Click **Download ZIP**.
3. Extract (unzip) the downloaded file.

Or download directly: **[⬇️ rock_scissor_paper_game-main.zip](https://github.com/vvsdevs/rock_scissor_paper_game/archive/refs/heads/main.zip)**

### Option 2: Clone with Git

```bash
git clone https://github.com/vvsdevs/rock_scissor_paper_game.git
cd rock_scissor_paper_game
```

---

## ▶️ How to Run the App

1. Install the latest **[Android Studio](https://developer.android.com/studio)**.
2. Open Android Studio → **File → Open** → select the downloaded project folder.
3. Wait for **Gradle Sync** to finish (the first time can take a few minutes).
4. Connect an Android phone (with **USB debugging** turned on) or start an **emulator**.
5. Press the green **▶ Run** button.

Build an APK from the command line:

```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

---

## 🎮 How to Play

1. Tap **Rock**, **Paper**, or **Scissors**.
2. Watch the countdown: *Rock… Paper… Shoot!*
3. The CPU picks its move at random.
   - 🪨 Rock beats ✂️ Scissors
   - ✂️ Scissors beats 📄 Paper
   - 📄 Paper beats 🪨 Rock
4. The first to **5 points** wins the match!

---

## 🛠️ Tech Stack

| | |
|---|---|
| **Language** | Kotlin |
| **UI** | Jetpack Compose + Material 3 |
| **Animations** | Compose Animation APIs + Canvas |
| **Min / Target SDK** | 24 / 37 |
| **Build** | Gradle Kotlin DSL + Version Catalog |

## 📂 Project Structure

```
app/src/main/java/com/game/rockscissors/
├── MainActivity.kt        # Game screen, animations, game logic
└── ui/theme/              # Colors, typography, theme
app/src/test/java/com/game/rockscissors/
└── GameRulesTest.kt       # Unit tests for win/lose/draw rules
```

---

<div align="center">

## 🚀 Learn to Build Apps Like This!

This project was built at an **[AndroAI](https://androai.io)** live session.
Want to build your **own Android apps and games** from scratch, even as a beginner?

<a href="https://androai.io/webinar/register">
  <img src="https://img.shields.io/badge/👉%20Register%20for%20the%20Free%20Webinar-androai.io-7C3AED?style=for-the-badge" alt="Register for the Webinar" />
</a>

⭐ **If you liked this project, please give it a star!** ⭐

Made with ❤️ by **[AndroAI](https://androai.io)**

</div>
