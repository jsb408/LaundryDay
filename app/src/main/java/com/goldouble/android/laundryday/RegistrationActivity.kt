package com.goldouble.android.laundryday

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("email")
        val type = intent.getStringExtra("type")

        val correctMap = hashMapOf(
                "email" to !email.isNullOrEmpty(),
                "password" to !type.isNullOrEmpty(),
                "confirm" to !type.isNullOrEmpty(),
                "nickname" to false
        )

        supportActionBar?.title = "회원가입"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(correctMap["email"]!!) {
            binding.editTextRegistrationEmail.setText(email)
            binding.editTextRegistrationEmail.isEnabled = false

            binding.editTextRegistrationPassword.isEnabled = false
            binding.editTextRegistrationPasswordConfirm.isEnabled = false
        }

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
            if(!Pattern.matches("^(?=.*[0-9])(?=.*[~!?@#$%^&*()-])(?=.*[a-zA-Z]).{8,}$", it.toString())) {
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
            } else if (binding.editTextRegistrationName.text.isNullOrBlank()) {
                binding.labelRegistrationNameInfo.setTextColor(getColor(R.color.registrationWarning))
                binding.labelRegistrationNameInfo.text = "닉네임을 입력해주세요."
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
                //로딩창 표시, 터치  잠금
                binding.loadingLayout.root.visibility = View.VISIBLE
                window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                if (type.isNullOrBlank()) {
                    kAuth.createUserWithEmailAndPassword(
                            editTextRegistrationEmail.text.toString(),
                            editTextRegistrationPassword.text.toString()
                    ).addOnCompleteListener { //완료되면
                        //로딩창 제거, 터치 잠금 해제
                        binding.loadingLayout.root.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }.addOnSuccessListener { //성공하면
                        createFirestore()
                    }.addOnFailureListener { e -> //실패하면
                        //에러메시지 표시
                        Toast.makeText(it.context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                } else createFirestore()
            }
        }
    }

    private fun createFirestore() {
        val user = kAuth.currentUser!!
        user.updateProfile(
                userProfileChangeRequest {
                    displayName = binding.editTextRegistrationName.text.toString()
                }).addOnSuccessListener { //성공하면
            kFirestore.collection(Table.MEMBERS.id).document(user.email!!).set(
                    hashMapOf(
                            "uid" to user.uid,
                            "nickname" to binding.editTextRegistrationName.text.toString(),
                            "type" to if (binding.radioGroupRegistrationType.checkedRadioButtonId == R.id.radioRegistrationNormal) "0001" else "0002",
                            "email" to user.email!!
                    )
            )
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}