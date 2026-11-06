# ساخت همیار جراحی

این پوشه از فایل ZIP اصلی استخراج شده و نام نمایشی برنامه به `hamyarjarahi` تغییر داده شده است. شناسه بسته همچنان `ir.clinic.secretary` است.

برای ساخت در GitHub، فایل‌های پروژه و workflow را روی مخزن قرار دهید و از بخش Actions، گردش کار **Build hamyarjarahi APK** را با Run workflow اجرا کنید. پس از موفقیت، artifact به نام `hamyarjarahi` شامل `hamyarjarahi.apk` قابل دانلود است. این نسخه با کلید آزمایشی debug امضا می‌شود؛ برای انتشار و به‌روزرسانی پایدار مناسب نیست، زیرا کلید هر محیط ساخت ممکن است متفاوت باشد.

برای ساخت محلی، Android Studio، Android SDK 34، Java 17 و Gradle 8.7 لازم است. در پوشه `android-app` دستور `gradle assembleDebug` را اجرا کنید. خروجی در `app/build/outputs/apk/debug/app-debug.apk` قرار می‌گیرد و می‌توانید نام فایل را به `hamyarjarahi.apk` تغییر دهید. ZIP اولیه فایل‌های اجرایی Gradle wrapper را ندارد، بنابراین از Gradle نصب‌شده استفاده کنید.

برای نسخه انتشار، یک کلید امضای خصوصی ماندگار متعلق به خودتان بسازید و از قابلیت Generate Signed Bundle / APK در Android Studio استفاده کنید. کلید را امن نگه دارید و در مخزن قرار ندهید؛ به‌روزرسانی برنامه باید با همان کلید امضا شود.
