package com.goldouble.android.laundryday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.goldouble.android.laundryday.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "로그인"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.buttonLoginKakao.setOnClickListener {
            Toast.makeText(this, "준비중입니다", Toast.LENGTH_SHORT).show()
        }

        binding.buttonLoginNaver.setOnClickListener {
            Toast.makeText(this, "준비중입니다", Toast.LENGTH_SHORT).show()
        }

        binding.buttonLoginGoogle.setOnClickListener {
            Toast.makeText(this, "준비중입니다", Toast.LENGTH_SHORT).show()
        }

        binding.buttonLoginEmail.setOnClickListener {
            startActivity(Intent(this, LoginEmailActivity::class.java))
        }

        binding.labelLoginRegistration.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}