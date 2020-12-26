package com.goldouble.android.laundryday

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.goldouble.android.laundryday.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.ktx.userProfileChangeRequest
import java.util.regex.Pattern

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding

    private val correctMap = hashMapOf(
            "email" to false,
            "password" to false,
            "confirm" to false,
            "nickname" to false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "회원가입"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.editTextRegistrationEmail.addTextChangedListener {
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches()) {
                binding.labelRegistrationEmailInfo.setTextColor(getColor(R.color.registrationWarning))
                binding.labelRegistrationEmailInfo.text = "잘못된 형식입니다."
                correctMap["email"] = false
            } else {
                binding.labelRegistrationEmailInfo.setTextColor(getColor(R.color.registrationPass))
                binding.labelRegistrationEmailInfo.text = "올바른 형식입니다."
                correctMap["email"] = true
            }
            binding.buttonRegistration.isEnabled = !correctMap.containsValue(false)
        }

        binding.editTextRegistrationPassword.addTextChangedListener {
            if(!Pattern.matches("^(?=.*[0-9])(?=.*[~!?@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,}$", it.toString())) {
                binding.labelRegistrationPasswordInfo.setTextColor(getColor(R.color.registrationWarning))
                binding.labelRegistrationPasswordInfo.text = "잘못된 형식입니다."
                correctMap["password"] = false
            } else {
                binding.labelRegistrationPasswordInfo.setTextColor(getColor(R.color.registrationPass))
                binding.labelRegistrationPasswordInfo.text = "올바른 형식입니다."
                correctMap["password"] = true
            }
            binding.buttonRegistration.isEnabled = !correctMap.containsValue(false)
        }

        binding.editTextRegistrationPasswordConfirm.addTextChangedListener {
            if(it.toString() != binding.editTextRegistrationPassword.text.toString()) {
                binding.labelRegistrationPasswordConfirmInfo.setTextColor(getColor(R.color.registrationWarning))
                binding.labelRegistrationPasswordConfirmInfo.text = "비밀번호가 일치하지 않습니다."
                correctMap["confirm"] = false
            } else {
                binding.labelRegistrationPasswordConfirmInfo.setTextColor(getColor(R.color.registrationPass))
                binding.labelRegistrationPasswordConfirmInfo.text = "비밀번호가 일치합니다."
                correctMap["confirm"] = true
            }
            binding.buttonRegistration.isEnabled = !correctMap.containsValue(false)
        }

        binding.editTextRegistrationName.addTextChangedListener {
            if(binding.editTextRegistrationName.text.length > 10) {
                binding.labelRegistrationNameInfo.setTextColor(getColor(R.color.registrationWarning))
                binding.labelRegistrationNameInfo.text = "닉네임이 너무 깁니다."
                correctMap["nickname"] = false
            } else {
                binding.labelRegistrationNameInfo.setTextColor(getColor(R.color.registrationPass))
                binding.labelRegistrationNameInfo.text = "사용 가능한 닉네임입니다."
                correctMap["nickname"] = true
            }
            binding.buttonRegistration.isEnabled = !correctMap.containsValue(false)
        }

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
                                kFirestore.collection(Table.MEMBERS.id).document(user.user!!.email!!).set(
                                    hashMapOf(
                                        "uid" to user.user!!.uid,
                                        "nickname" to binding.editTextRegistrationName.text.toString(),
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