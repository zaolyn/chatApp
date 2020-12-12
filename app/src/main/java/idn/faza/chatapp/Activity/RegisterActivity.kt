package idn.faza.chatapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import idn.faza.chatapp.R
import idn.faza.chatapp.model.Users
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        text_masuk.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        btn_register.setOnClickListener {
            val username = search_user.text.toString()
            val email = email_reg.text.toString()
            val password = password_reg.text.toString()
            registerUser(username, email, password)
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun registerUser(username: String, email: String, password: String) {
        mAuth = FirebaseAuth.getInstance()

        if (username.isNotBlank() && email.isNotBlank()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        userId = mAuth.currentUser?.uid.toString()
                        refUsers =
                            FirebaseDatabase.getInstance().reference.child("Users").child(userId)

                        val newUser = Users(
                            uid = userId, username = username,
                            search = username.toLowerCase(Locale.ROOT), status = "online"

                        )
                        refUsers.setValue(newUser).addOnCompleteListener() {
                            if (it.isSuccessful) {
                                val intentMain = Intent(this, MainActivity::class.java)
                                intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intentMain)
                                toast("user baru telah dibuat.")
                                finish()

                            } else {
                                it.exception?.message?.let {
                                    toast("Gagal mendaftarkan user.\nError : $it")
                                }
                            }
                        }
                    } else {
                        it.exception?.message?.let {
                            toast("Gagal mendaftarkan user.\nError : $it")
                        }
                    }
                }
            return
        }
        val message =
            if (username.isBlank()) "Username" else if (email.isBlank()) "Email" else "Password"
        toast("\$ message tidak boleh kosong")
    }
}









