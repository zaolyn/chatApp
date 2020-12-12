package idn.faza.chatapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import idn.faza.chatapp.R
import idn.faza.chatapp.model.Users
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import retrofit2.http.Url


class SettingsFragment : Fragment() {

    var userReference: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private val RequestCode = 438
    private var imageUrl: Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker: String? = ""
    private var socialChecker: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference =
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(Users::class.java)

                    if (context != null) {
                        view.username_settings.text = user!!.username
                        if (user.profile.isNotBlank()) Picasso.get().load(user.profile)
                            .into(view.profile_image)
                        if (user.cover.isNotBlank()) Picasso.get().load(user.cover)
                            .into(view.cover_setting)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        view.profile_image.setOnClickListener {
            pickImage()
        }

        view.cover_setting.setOnClickListener {
            coverChecker = "cover"
            pickImage()
        }

        view.facebook_link.setOnClickListener {
            socialChecker = "facebook"
            setSocialLinks()
        }

        view.twitter_link.setOnClickListener {
            socialChecker = "twitter"
            setSocialLinks()
        }

        view.instagram_link.setOnClickListener {
            socialChecker = "instagram"
            setSocialLinks()
        }


        return view
    }

    private fun setSocialLinks() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(requireContext(), R.style.Theme_AppCompat_DayNight_Dialog_Alert)

        if (socialChecker == "twitter") {
            builder.setTitle("Write URL")
        } else {
            builder.setTitle("Write username:")
        }
        val editText = EditText(context)

        if (socialChecker == "twitter") {
            editText.hint = "e.g www.Twitter.com"
        } else {
            editText.hint = "e.g fazaolyn"
        }
        builder.setView(editText)

        builder.setPositiveButton("create", DialogInterface.OnClickListener { dialog, which ->
            val str = editText.text.toString()

            if (str == "") {

                Toast.makeText(context, "please write something...", Toast.LENGTH_LONG).show()
            } else {
                saveSocialLink()
            }
        })
        builder.setNegativeButton("create", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })

        builder.show()
    }

    private fun saveSocialLink() {
        val mapSocial = HashMap<String, Any>()
        //  mapSocial["cover"] = url
        // userReference!!.updateChildren(mapSocial)

        when (socialChecker) {
            "facebook" -> {
                mapSocial["facebook"] = "https://www.facebook.com/"
            }
            "instagram" -> {
                mapSocial["instagram"] = "https://www.instagram.com/"
            }
            "twitter" -> {
                mapSocial["twitter"] = "https://www.twitter.com/"

            }
        }

        userReference!!.updateChildren(mapSocial).addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                Toast.makeText(context, "updated successfully.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null) {
            imageUrl = data.data
            Toast.makeText(context, "uploading...", Toast.LENGTH_SHORT).show()
            uploadImageToDatabase()

        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("image is uploading, please wait...")
        progressBar.show()

        if (imageUrl != null) {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUrl!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if (coverChecker == "cover") {
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = url
                        userReference!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    } else {

                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["profile"] = url
                        userReference!!.updateChildren(mapProfileImg)
                        coverChecker = ""

                    }
                    progressBar.dismiss()
                }
            }
        }
    }
}