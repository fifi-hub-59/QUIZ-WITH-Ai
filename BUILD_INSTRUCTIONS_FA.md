# دستورالعمل ساخت APK

## پیش‌نیازها

- **Java Development Kit (JDK) 17** یا بالاتر
- **Android SDK** (حداقل API level 24)
- **Git** برای کلون کردن repository

## ساخت APK به روش محلی (Local Build)

### مرحله 1: تنظیم محیط (Android Studio)

اگر فایل `.env` موارد حساس (secrets) دارد:

1. **در Android Studio:**
   - به `File > Project Structure > Modules > app > Build Types > release` بروید
   - در قسمت `Signing Config`، اطلاعات امضا (signing) را وارد کنید
   - یا می‌توانید متغیرهای محیطی در `local.properties` تعریف کنید

2. **ایجاد فایل `local.properties` در root directory:**
   ```properties
   sdk.dir=/path/to/your/android/sdk
   KEYSTORE_PATH=/path/to/your/keystore.jks
   STORE_PASSWORD=your_password
   KEY_PASSWORD=your_password
   ```

### مرحله 2: ساخت APK

```bash
# Clone repository
git clone https://github.com/fifi-hub-59/QUIZ-WITH-Ai.git
cd QUIZ-WITH-Ai

# ساخت Debug APK (آسان‌تر و سریع‌تر)
./gradlew assembleDebug

# ساخت Release APK (برای انتشار)
./gradlew assembleRelease
```

### مرحله 3: موقعیت APK

- **Debug APK:** `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK:** `app/build/outputs/apk/release/app-release.apk`

---

## ساخت اتوماتیک با GitHub Actions (CI/CD)

### مرحله 1: تنظیم Secrets در GitHub

1. به `Settings > Secrets and variables > Actions` بروید
2. اطلاعات حساس را به عنوان Secret اضافه کنید:

```
KEYSTORE_PATH = path/to/keystore.jks
STORE_PASSWORD = your_keystore_password
KEY_PASSWORD = your_key_password
```

### مرحله 2: اضافه کردن متغیرهای محیطی به Workflow

دستورالعمل فایل `build.gradle.kts` تک نویسی را به GitHub Secrets وصل می‌کند:

```kotlin
val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
val keystoreFile = file(keystorePath)
```

### مرحله 3: استقرار APK

APK ساخته شده به عنوان "Artifact" درون GitHub Actions موجود است:
- به `Actions` بروید
- آخرین workflow run را کلیک کنید
- `Artifacts` را کلیک کنید
- APK را دانلود کنید

---

## مشکل‌گیری (Troubleshooting)

### ❌ خطای "Permission denied" برای gradlew

**حل:** دستور زیر را در بخش Build اجرا کنید:
```bash
chmod +x gradlew
```

### ❌ خطای "Build failed"

**کنترل کنید:**
- ✅ JDK 17 نصب است
- ✅ `android/sdk/` موجود است
- ✅ `gradle.properties` درست پیکربندی شده
- ✅ مشکلات شبکه برای دانلود dependencies نیست

### ❌ خطا در `.env` یا Secrets

**راه حل:**
- `.env.example` در repository است
- `local.properties` را در root directory ایجاد کنید
- متغیرها را در GitHub Actions → Settings → Secrets اضافه کنید

---

## نکات مهم

| موضوع | توضیح |
|------|--------|
| **Debug APK** | برای تست و توسعه استفاده میشود (سریع‌تر) |
| **Release APK** | برای انتشار در Google Play (اندازه کمتر، سریع‌تر) |
| **Secrets** | هرگز رمز عبور را در repository commit نکنید |
| **Keystore** | برای امضای release APK الزامی است |

---

## لینک‌های مفید

- [Android Build Guide](https://developer.android.com/studio/build)
- [Gradle Documentation](https://gradle.org/releases/)
- [GitHub Actions Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)

