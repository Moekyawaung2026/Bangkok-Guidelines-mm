# Bangkok Go MM (กรุงเทพเดินทาง / ဘန်ကောက်သွားလာရေးလမ်းညွှန်)

> **“သင်ရှိနေသောနေရာမှ သွားလိုသောနေရာသို့ အလွယ်ဆုံး၊ အသက်သာဆုံး၊ လုံခြုံဆုံး လမ်းကြောင်းကို မြန်မာလို ရှာဖွေပါ။”**

Bangkok Go MM is a production-ready, mobile-first bilingual Bangkok public transportation platform tailored for Myanmar migrant workers living in Thailand, visitors, and Thai employers.

---

## 🌟 Key Highlights

- **Bilingual Interface**: Native Myanmar Unicode (မြန်မာယူနီကုဒ်စစ်စစ်) & Native Thai (ไทย) with English/Romanized pronunciation fallback.
- **Multimodal Route Planner (10 Modes)**:
  1. Cheapest (အသက်သာဆုံး - Bus combo)
  2. Fastest (အမြန်ဆုံး - BTS/MRT express)
  3. Least Walking (လမ်းလျှောက်အနည်းဆုံး)
  4. Fewest Transfers (အပြောင်းအရွှေ့အနည်းဆုံး)
  5. Accessible (ဘီးတပ်ကုလားထိုင်/ဓာတ်လှေကား အပြည့်အစုံ)
  6. Night Route (ညဘက် ၂၄ နာရီလိုင်း)
  7. Motorcycle Taxi + Train (ဆိုင်ကယ်ကယ်ရီ + ရထား)
  8. Bus Only (ဘတ်စ်ကားသီးသန့်)
  9. BTS/MRT Only (ရထားလိုင်းသီးသန့်)
  10. Taxi Only (မီတာတက္ကစီသီးသန့်)
- **Nearby Transport**: GPS detection with 500m, 1km, 3km, and 5km radius filters.
- **Bus Guide**: Detailed ordinary vs. AC vs. expressway buses, stop lists, and “ဘယ်ဘက်က မှတ်တိုင်မှာ စောင့်ရမလဲ” (Which side bus stop to wait).
- **Taxi & Motorcycle Taxi Safety**: Meter taxi request phrase (“เปิดมิเตอร์ด้วยครับ / မီတာဖွင့်ပေးပါ။”), 500 THB helmet fine warning, and driver verification.
- **Emergency Hotlines**: 1-Tap dialing for Police 191, Tourist Police 1155, Ambulance 1669, Fire 199, Land Transport 1584, and the Myanmar Embassy (+66 2 233 2237).
- **Thai Phrasebook**: 12 practical categories with audio text-to-speech (TTS) playback and large full-screen driver flashcards.
- **In-App AI Transport Assistant**: Understands Burmese, Thai, and English with offline transit fallback.
- **Offline Mode**: Local SQLite (Room Database) caching for saved places and routes.
- **Data Verification System**: Official source attribution (BMTA, BTS, MRT, SRT), confidence scores, and community reporting.

---

## 📱 Tech Stack

- **Android Client**: Kotlin, Jetpack Compose (Material Design 3)
- **Persistence**: Room Database (SQLite), Flow, Coroutines
- **Networking & AI**: OkHttp, Retrofit, Gemini 3.5 Flash via BuildConfig
- **Testing**: Robolectric, JUnit 4
- **Web & Backend Spec**: PostgreSQL, PostGIS, Prisma, TypeScript, GitHub Actions CI/CD

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or later / Gradle 9+
- JDK 17 or later

### Build & Run
```bash
gradle assembleDebug
gradle :app:testDebugUnitTest
```

### GitHub Owner
- **Dev-moe-kyawaung**

---

## 📄 License
Licensed under the Apache-2.0 License.
