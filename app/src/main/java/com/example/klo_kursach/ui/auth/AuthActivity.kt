package com.example.klo_kursach.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.klo_kursach.databinding.ActivityAuthBinding
import com.example.klo_kursach.ui.MainActivity
import com.example.klo_kursach.ui.common.applySystemBarsPadding

/** Активность авторизации: содержит фрагменты входа и регистрации (граф nav_auth). */
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarsPadding()
    }

    /** Переход в главную активность после входа / регистрации. */
    fun openMain() {
        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }
}
