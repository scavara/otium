# Otium 📺

![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-blue) ![Platform](https://img.shields.io/badge/Android%20TV-green) ![Material 3](https://img.shields.io/badge/UI-Material%203%20(TV)-8E75B2) ![Unsplash](https://img.shields.io/badge/Images-Unsplash%20API-black) ![Built with Gemini](https://img.shields.io/badge/Built%20with-Gemini-8E75B2) ![License](https://img.shields.io/badge/License-MIT-green)

**Otium** is an ambient background application for Android TV designed to turn your television into a serene window of inspiration. Using Jetpack Compose for TV and high-resolution nature photography, it cycles through dynamic backgrounds and motivational quotes with professional-grade performance and persistence.

> 💡 **Built with AI:** This entire project—from the Kotlin implementation and network layer to the caching strategies—was developed in collaboration with **Google Gemini**.

---

## ✨ Key Features (Phase 2)

Unlike basic "slideshow" apps, Otium focuses on a seamless, high-performance TV experience:

* **Dynamic Backgrounds:** Integrates the **Unsplash API** to fetch high-resolution, landscape nature photography on the fly.
* **Live Quote Integration:** Pulls real-time, witty quotes from the [StatusQuo Backend](https://statusquo-1c0c04fdc62e.herokuapp.com/).
* **Silent Pre-fetching:** Implements a dual-state buffer logic; while you view the current scene, the app silently downloads and caches the next image to guarantee a zero-latency transition.
* **Robust Caching:** Powered by **Coil**, the app aggressively manages local TV storage (100MB limit) to save bandwidth and ensure offline resilience.
* **Customizable Experience:** A TV-native settings dialog allows users to toggle quotes and cycle through different text sizes and screen positions.
* **Persistent Settings:** Uses **Jetpack DataStore** to remember user preferences across app reboots.

---

## 📸 Demo
*(Add a screenshot of your Otium app running in the Television 4K AVD here)*

---

## 🏗️ System Architecture

Otium uses a modern Android architecture with a focus on non-blocking I/O and efficient memory management for low-spec TV hardware.

```mermaid
graph TD
    TV[Android TV Device] -->|LaunchedEffect| Timer[30s Master Timer]
    Timer -->|Request| Unsplash[Unsplash API]
    Timer -->|Request| SQuo[StatusQuo Heroku API]
    
    Unsplash -->|Image URL| Coil[Coil Image Loader]
    Coil -->|Prefetch & Cache| Disk[(Local Disk Cache)]
    
    SQuo -->|Quote JSON| State[UI State]
    Disk -->|Instant Load| Screen[AmbientScreen Composable]
    
    User((User)) -->|D-Pad Select| Settings[Options Dialog]
    Settings -->|Save| DStore[(Jetpack DataStore)]
    DStore -->|Observe| Screen