package com.example.shareablelock


import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

        txtModifyProfile.setOnClickListener{
            Toast.makeText(this, "Modify Profile Picture", Toast.LENGTH_SHORT).show()
        }

        txtSetName.setOnClickListener { showEditTextDialog("Name", txtSetName.text.toString()) { newValue ->
            txtName.text = newValue
            txtUserName.text = newValue
        } }

        txtSetUserName.setOnClickListener { showEditTextDialog("Username", txtSetUserName.text.toString()) { newValue ->
            txtName.text = newValue
            txtUserName.text = newValue
        } }

        txtSetEmail.setOnClickListener { showEditTextDialog("Email", txtSetEmail.text.toString()) { newValue ->
            txtEmail.text = newValue
        } }

        txtSetPwd.setOnClickListener { showEditPasswordDialog() }

    }

    override fun onResume() {
        super.onResume()
        getUser()
        updateUI()
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
        val urlReplace = "http://10.0.2.2:8500/files/1716997621741_ea62fcf4-89c8-4755-88ee-0f93e43c770cexample.jpg"
        Glide.with(this)
            .load(urlReplace)
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
                        updateUI()
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

}