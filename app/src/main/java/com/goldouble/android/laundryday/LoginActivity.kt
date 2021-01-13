package com.goldouble.android.laundryday

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.goldouble.android.laundryday.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val googleLoginLauncher = registerForActivityResult(GoogleLoginResultContract()) { idToken ->
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        kAuth.signInWithCredential(credential).addOnSuccessListener {
            Log.d("LOGIN", it.user?.email.toString())
            kFirestore.collection(Table.MEMBERS.id).document(it.user!!.email!!).get().addOnCompleteListener { task ->
                if(task.result.exists()) finish()
                else signInWithEmailLauncher.launch(Intent(this, LoginEmailActivity::class.java)
                        .putExtra("email", it.user!!.email!!)
                        .putExtra("type", "google"))
            }
        }
    }
    private val signInWithEmailLauncher = registerForActivityResult(SignInWithEmailContract()) { user ->
        user?.let { finish() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "로그인"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.buttonLoginKakao.setOnClickListener {
            Toast.makeText(this, "준비중입니다", Toast.LENGTH_SHORT).show()
        }

        binding.buttonLoginNaver.setOnClickListener {
            Toast.makeText(this, "준비중입니다", Toast.LENGTH_SHORT).show()
        }

        binding.buttonLoginGoogle.setOnClickListener {
            googleLoginLauncher.launch(googleSignInClient.signInIntent)
        }

        binding.buttonLoginEmail.setOnClickListener {
            signInWithEmailLauncher.launch(Intent(this, LoginEmailActivity::class.java))
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

    inner class GoogleLoginResultContract : ActivityResultContract<Intent, String>() {
        override fun createIntent(context: Context, input: Intent): Intent = input

        override fun parseResult(resultCode: Int, intent: Intent?): String {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Google Login", "firebaseAuthWithGoogle: " + account.id)
                return account.idToken!!
            } catch(e: ApiException) {
                e.printStackTrace()
            }
            return ""
        }
    }

    inner class SignInWithEmailContract : ActivityResultContract<Intent, FirebaseUser?>() {
        override fun createIntent(context: Context, input: Intent): Intent = input

        override fun parseResult(resultCode: Int, intent: Intent?): FirebaseUser? {
            return kAuth.currentUser
        }
    }
}