package com.goldouble.android.laundryday

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.goldouble.android.laundryday.databinding.ActivityLoginEmailBinding
import com.google.firebase.FirebaseException

class LoginEmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "로그인"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.buttonLogin.setOnClickListener {
            if (binding.editTextEmail.text.isEmpty() || binding.editTextPassword.text.isEmpty()) { //이메일, 패스워드 입력 확인
                Toast.makeText(it.context, "이메일과 비밀번호를 입력해주세요", Toast.LENGTH_LONG).show()
            } else {
                try {
                    //로딩창 표시, 터치  잠금
                    binding.loadingLayout.root.visibility = View.VISIBLE
                    window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    //로그인
                    kAuth.signInWithEmailAndPassword(
                        binding.editTextEmail.text.toString(),
                        binding.editTextPassword.text.toString()
                    ).addOnCompleteListener { //완료되면
                        //로딩창 제거, 터치 잠금 해제
                        binding.loadingLayout.root.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }.addOnSuccessListener { //성공하면
                        //preference에 로그인 정보 저장
                        val prefs = getSharedPreferences("login", Context.MODE_PRIVATE)
                        prefs.edit().putString("email", binding.editTextEmail.text.toString())
                            .putString("password", binding.editTextPassword.text.toString())
                            .apply()

                        startActivity(Intent(this, MainActivity::class.java))
                        finishAffinity()
                    }.addOnFailureListener { e -> //실패하면
                        //에러메시지 표시
                        Toast.makeText(it.context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: FirebaseException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}