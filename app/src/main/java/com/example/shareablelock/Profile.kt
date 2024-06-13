package com.example.shareablelock


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class Profile : AppCompatActivity() {
    lateinit var apiService: ApiService
    lateinit var user: UserModel
    lateinit var imgProfile: ImageView
    lateinit var txtModifyProfile: TextView
    lateinit var txtName: TextView
    lateinit var txtUserName: TextView
    lateinit var txtEmail: TextView
    lateinit var txtPwd: TextView
    lateinit var txtSetName: TextView
    lateinit var txtSetUserName: TextView
    lateinit var txtSetEmail: TextView
    lateinit var txtSetPwd: TextView
    lateinit var btnLogout: TextView
    private lateinit var bottomNavigationView: BottomNavigationView
    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            handleImageUri(uri)
        } else {
            // Handle the case where the user didn't pick an image
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_locks
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_locks -> {
                    val intent = Intent(this, Locks::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.imgProfile -> {
                    finish()
                }
                R.id.nav_notifications -> {
                    val intent = Intent(this, MessageActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_key -> {
                    val intent = Intent(this, KeyActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
        val user = PreferenceHelper.getUser(this)
        if(user == null){
            logout()
        }
        this.user = user!!
        apiService = RetrofitHelper.getApiService(this)
        initViews()
        btnLogout.setOnClickListener {
            logout()
        }

        getUser()
//        updateUI()

        txtModifyProfile.setOnClickListener{
            openPhotoPicker()

        }

        txtSetName.setOnClickListener { showEditTextDialog("Name", txtName.text.toString()) { newValue ->
            txtName.text = newValue
            txtUserName.text = newValue
        } }

        txtSetUserName.setOnClickListener { showEditTextDialog("Username", txtUserName.text.toString()) { newValue ->
            txtName.text = newValue
            txtUserName.text = newValue
        } }

        txtSetEmail.setOnClickListener { showEditTextDialog("Email", txtEmail.text.toString()) { newValue ->
            txtEmail.text = newValue
        } }

        txtSetPwd.setOnClickListener { showEditPasswordDialog() }

    }

    override fun onResume() {
        super.onResume()
        getUser()
//        updateUI()
    }

    private fun getUser(){
        user.id?.let { userId ->
            apiService.getUser(userId).enqueue(object : Callback<UserModel> {
                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                    if(response.isSuccessful){
                        val userModel = response.body()
                        userModel?.let{
                            PreferenceHelper.saveUser(this@Profile, it)
                            user = it
                            updateUI()
                        }?: run{
                            Toast.makeText(this@Profile, "no user login information", Toast.LENGTH_SHORT).show()
                            logout()
                        }
                    }
                }

                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                    Toast.makeText(this@Profile, "An error occurred", Toast.LENGTH_SHORT).show()
                    logout()
                }
            })
        }?:run{
            Toast.makeText(this, "no user login information", Toast.LENGTH_SHORT).show()
            logout()
        }
    }
    private fun updateUI(){
        txtName.text = user.username
        txtUserName.text = StringBuilder("@").append(user.username)
        txtEmail.text = user.email
        txtPwd.text = "********"
        println("The avatar is ${user.avatar}")
//        val urlReplace = "http://10.0.2.2:8500/files/1716997621741_ea62fcf4-89c8-4755-88ee-0f93e43c770cexample.jpg"
        Glide.with(this)
            .load(user.avatar)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background) // Placeholder image
                    .error(R.drawable.outline_perm_identity_24) // Error image in case of loading failure
            )
            .into(imgProfile)
    }
    private fun initViews(){
        imgProfile = findViewById(R.id.imgProfile)
        txtModifyProfile = findViewById(R.id.txtModifyProfile)
        txtName = findViewById(R.id.txtName)
        txtUserName = findViewById(R.id.txtUserName)
        txtEmail = findViewById(R.id.txtEmail)
        txtPwd = findViewById(R.id.txtPwd)
        txtSetName = findViewById(R.id.txtSetName)
        txtSetUserName = findViewById(R.id.txtSetUserName)
        txtSetEmail = findViewById(R.id.txtSetEmail)
        txtSetPwd = findViewById(R.id.txtSetPwd)
        btnLogout = findViewById(R.id.btnLogout)
    }
    private fun logout(){
        PreferenceHelper.clearUser(this)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showEditTextDialog(title: String, currentValue: String, onSave: (String) -> Unit){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit $title")
        val view = layoutInflater.inflate(R.layout.dialog_edit_text, null)
        val editText = view.findViewById<EditText>(R.id.editTextDialog)
        editText.setText(currentValue)
        builder.setView(view)
        builder.setPositiveButton("Save"){ dialog, _ ->
            val value = editText.text.toString()
            val userNew = when(title){
                "Name" -> user.copy(username = value)
                "Username" -> user.copy(username = value)
                "Email" -> user.copy(email = value)
                else -> {
                    println("failed to copy") ; user}
            }
            println("The user is $user, the userNew is $userNew")
            apiService.updateUser(userNew.id!!, userNew).enqueue(object : Callback<UserModel>{
                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                    if(response.isSuccessful){
                        onSave(value)
                        Toast.makeText(this@Profile, "$title updated", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }else{
                        Toast.makeText(this@Profile, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                    Toast.makeText(this@Profile, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            })

        }
        builder.setNegativeButton("Cancel"){ dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun showEditPasswordDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Password")
        val view = layoutInflater.inflate(R.layout.dialog_edit_password, null)
        val editNewPwd = view.findViewById<EditText>(R.id.editPasswordDialog)
        val editConfirmPwd = view.findViewById<EditText>(R.id.editConformPwdDialog)
        val editCode = view.findViewById<EditText>(R.id.editCodeDialog)
        val btnGetCode = view.findViewById<TextView>(R.id.btnGetCode)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)

        btnGetCode.setOnClickListener {
            apiService.requestCode(user.id!!).enqueue(object : Callback<Unit>{
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if(response.isSuccessful){
                        Toast.makeText(this@Profile, "Code sent to your email", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@Profile, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(this@Profile, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnSubmit.setOnClickListener {
            val newPwd = editNewPwd.text.toString()
            val confirmPwd = editConfirmPwd.text.toString()
            val code = editCode.text.toString()
            if(newPwd != confirmPwd){
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val passwordUpdateModel = PasswordUpdateModel(newPwd, code)
            apiService.updatePassword(user.id!!, passwordUpdateModel).enqueue(object : Callback<UserModel>{
                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                    if(response.isSuccessful){
                        Toast.makeText(this@Profile, "Password updated", Toast.LENGTH_SHORT).show()
                        // todo: close the dialog
                        getUser()
//                        updateUI()
                    }else{
                        println(response.errorBody()?.string())
                        Toast.makeText(this@Profile, "check the code", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                    Toast.makeText(this@Profile, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            })

        }

        builder.setView(view)
        builder.setNegativeButton("Cancel"){ dialog, _ ->
            dialog.dismiss()
        }


        builder.create().show()
    }

    private fun openPhotoPicker() {
        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
    }

    private fun handleImageUri(uri: Uri) {
        val filePath = getRealPathFromURI(this, uri)
        val file = File(filePath!!)
        uploadFile(file, object : FileUploadCallback {
            override fun onSuccess(fileName: String) {
                val url = StringBuilder(getString(R.string.file_server_ip)).append("/").append(fileName).toString()
                val userNew = user.copy(avatar = url)

                apiService.updateUser(userNew.id!!, userNew).enqueue(object : Callback<UserModel>{
                    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                        if(response.isSuccessful){
                            imgProfile.setImageURI(uri)
                            Toast.makeText(this@Profile, "Photo updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@Profile, "An error occurred", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        Toast.makeText(this@Profile, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(this@Profile, "File upload failed: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        })

    }


    private fun uploadFile(file: File, callback: FileUploadCallback) {
        val requestFile = RequestBody.create(MediaType.get("image/jpeg"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val apiService = RetrofitHelper.getFileAPIService(this)
        val call = apiService.uploadFile(body)

        call.enqueue(object : Callback<ProfilePhotoName> {
            override fun onResponse(call: Call<ProfilePhotoName>, response: Response<ProfilePhotoName>) {
                if (response.isSuccessful) {
                    val fileName = response.body()?.fileName
                    if (fileName != null) {
                        Toast.makeText(this@Profile, "File uploaded successfully", Toast.LENGTH_SHORT).show()
                        callback.onSuccess(fileName)
                    } else {
                        callback.onFailure("No file name returned")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    callback.onFailure(errorMessage)
                }
            }

            override fun onFailure(call: Call<ProfilePhotoName>, t: Throwable) {
                val errorMessage = t.message ?: "Unknown error"
                callback.onFailure(errorMessage)
            }
        })
    }





}


fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        cursor?.getString(column_index!!)
    } finally {
        cursor?.close()
    }
}


