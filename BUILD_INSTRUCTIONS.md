# دستورالعمل ساخت APK پروژه QUIZ-WITH-AI

## 📋 الزامات:
- Android SDK 35+ نصب شده
- Gradle 8.0+
- Java 17+

## 🔨 ساخت Debug APK:

```bash
./gradlew assembleDebug
```

APK خروجی در اینجا قرار می‌گیرد:
```
app/build/outputs/apk/debug/app-debug.apk
```

## 📦 ساخت Release APK:

### مرحله 1: ایجاد Keystore (اگر فایل موجود نیست):
```bash
keytool -genkey -v -keystore my-upload-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
```

### مرحله 2: تنظیم متغیرهای محیطی:
```bash
export KEYSTORE_PATH="/path/to/my-upload-key.jks"
export STORE_PASSWORD="your_store_password"
export KEY_PASSWORD="your_key_password"
```

### مرحله 3: ساخت Release APK:
```bash
./gradlew assembleRelease
```

APK خروجی در اینجا قرار می‌گیرد:
```
app/build/outputs/apk/release/app-release.apk
```

## ✅ تغييرات اعمال شده:

1. ✅ **Namespace اصلاح شد**: `com.example` → `com.aistudio.guesstheimage.wjvqrk`
2. ✅ **Activity Path اصلاح شد**: `com.example.MainActivity` → `com.aistudio.guesstheimage.wjvqrk.MainActivity`
3. ✅ **Gradle Properties بهینه شده**: برای ساخت سریع‌تر

## 🚀 نصب و اجرای APK:

### نصب Debug APK روی دستگاه:
```bash
./gradlew installDebug
```

### نصب Release APK روی دستگاه:
```bash
adb install app/build/outputs/apk/release/app-release.apk
```

## 🐛 حل مشکلات رایج:

| مشکل | حل |
|------|-----|
| `SDK not found` | `local.properties` میں `sdk.dir` را تنظیم کنید |
| `Gradle sync failed` | `./gradlew clean` و سپس مجدداً تلاش کنید |
| `Memory error` | حد حافظه در `gradle.properties` را بالا بریں |

## 📝 نکات مهم:

- پروژه از **Java 17** استفاده می‌کند
- **Android 12+** (API 31+) را پشتیبانی می‌کند
- **Compose** برای UI استفاده شده است
