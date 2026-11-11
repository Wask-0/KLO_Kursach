package com.example.klo_kursach.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.ColorInt
import com.example.klo_kursach.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Цвет предмета из массива ресурсов R.array.subject_colors. */
@ColorInt
fun Context.subjectColor(index: Int): Int {
    val colors = resources.obtainTypedArray(R.array.subject_colors)
    val color = colors.getColor(Math.floorMod(index, colors.length()), 0)
    colors.recycle()
    return color
}

fun Long.formatDate(): String =
    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(this))

fun Context.appVersion(): String = try {
    val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        @Suppress("DEPRECATION")
        packageManager.getPackageInfo(packageName, 0)
    }
    info.versionName ?: "1.0"
} catch (_: PackageManager.NameNotFoundException) {
    "1.0"
}

/** Ключ для передачи текста (OCR / ИИ) обратно в редактор через SavedStateHandle. */
const val RESULT_INSERT_TEXT = "insert_text"
