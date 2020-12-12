package idn.faza.chatapp.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import idn.faza.chatapp.R
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()

        text_buat_akun.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_login.setOnClickListener {
            mAuth = FirebaseAuth.getInstance()

            val email = email_log.text.toString()
            val password = password_log.text.toString()
            loginUser(email, password)
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun loginUser(email: String, password: String) {
        if (email.isNotBlank() && password.isNotBlank()) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intentMain = Intent(this, MainActivity::class.java)
                        intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intentMain)
                        toast("user berhasil login")
                        finish()

                    } else {
                        it.exception?.message?.let {
                            toast("gagal login user.\nError : $it")
                        }
                    }
                }
            return
        }
        val message =
            if (email.isBlank()) "Email" else "Password"
        toast("$message tidak boleh kosong")
    }
}
