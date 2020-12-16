package com.goldouble.android.laundryday

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.goldouble.android.laundryday.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.ktx.userProfileChangeRequest

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "회원가입"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.radioGroupRegistrationType.check(R.id.radioRegistrationNormal)

        binding.apply {
            buttonRegistration.setOnClickListener {
                when {
                    /* 유효성 검사 시작 */
                    editTextRegistrationEmail.text.isEmpty() || editTextRegistrationPassword.text.isEmpty() ||
                            editTextRegistrationPasswordConfirm.text.isEmpty() || editTextRegistrationName.text.isEmpty() ->
                        Toast.makeText(it.context, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show()
                    editTextRegistrationPassword.text.length < 8 ->
                        Toast.makeText(it.context, "기준에 맞게 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                    editTextRegistrationPassword.text.toString() != editTextRegistrationPasswordConfirm.text.toString() ->
                        Toast.makeText(it.context, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                    /* 유효성 검사 끝 */
                    else -> {
                        //로딩창 표시, 터치  잠금
                        binding.loadingLayout.root.visibility = View.VISIBLE
                        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                        kAuth.createUserWithEmailAndPassword(
                            editTextRegistrationEmail.text.toString(),
                            editTextRegistrationPassword.text.toString()
                        ).addOnCompleteListener { //완료되면
                            //로딩창 제거, 터치 잠금 해제
                            binding.loadingLayout.root.visibility = View.GONE
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }.addOnSuccessListener { user -> //성공하면
                            user.user!!.updateProfile(
                                userProfileChangeRequest {
                                    displayName = editTextRegistrationName.text.toString()
                                }).addOnSuccessListener { _ -> //성공하면
                                kFirestore.collection(kTable.MEMBERS.id).document(user.user!!.email!!).set(
                                    hashMapOf(
                                        "uid" to user.user!!.uid,
                                        "type" to if(binding.radioGroupRegistrationType.checkedRadioButtonId == R.id.radioRegistrationNormal) "0001" else "0002",
                                        "email" to user.user!!.email!!
                                    )
                                )
                                //preference에 로그인 정보 저장
                                val prefs = getSharedPreferences("login", Context.MODE_PRIVATE)
                                prefs.edit()
                                    .putString("email", editTextRegistrationEmail.text.toString())
                                    .putString("password", editTextRegistrationName.text.toString())
                                    .apply()

                                startActivity(Intent(baseContext, MainActivity::class.java))
                                finishAffinity()
                            }
                        }.addOnFailureListener { e -> //실패하면
                            //에러메시지 표시
                            Toast.makeText(it.context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}