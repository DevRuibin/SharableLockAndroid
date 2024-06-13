package com.example.shareablelock

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.concurrent.locks.Lock

class LockDetails : AppCompatActivity() {
    private val TAG = "LockDetails"
    private lateinit var lockName: TextView
    private lateinit var imgLock: ImageView
    private lateinit var txtModifyLockImage: TextView
    private lateinit var txtName: TextView
    private lateinit var txtChangeName: TextView
    private lateinit var txtOwner:TextView
    private lateinit var txtStatus: TextView
    private lateinit var txtBattery: TextView
    private lateinit var txtGpsInterval: TextView
    private lateinit var txtGpsIntervalSet: TextView
    private lateinit var txtBatteryInterval: TextView
    private lateinit var txtBatteryIntervalSet: TextView
    private lateinit var txtNoShareUsers: TextView
    private lateinit var recycleView: RecyclerView
    private lateinit var btnShare: Button
    private lateinit var btnLocation: Button
    private lateinit var btnDelete: Button
    private lateinit var apiService: ApiService
    private lateinit var fileApiService: ApiService
    private lateinit var adapter: SharedUserAdapter
    private var lockId: Long = -1
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            handleImageUri(uri, lockId)
        } else {
            // Handle the case where the user didn't pick an image
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lock_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
        // get the lock id from the intent
        lockId = intent.getLongExtra("lockId", -1)
        Log.d(TAG, "Lock id is $lockId")
        apiService = RetrofitHelper.getApiService(this)
        fileApiService = RetrofitHelper.getFileAPIService(this)
        getLockById(lockId)
        setListeners()



    }

    private fun initView() {
        lockName = findViewById(R.id.lockName)
        imgLock = findViewById(R.id.imgLock)
        txtModifyLockImage = findViewById(R.id.txtModifyLockImage)
        txtName = findViewById(R.id.txtName)
        txtChangeName = findViewById(R.id.txtChangeName)
        txtOwner = findViewById(R.id.txtOwner)
        txtStatus = findViewById(R.id.txtStatus)
        txtBattery = findViewById(R.id.txtBattery)
        txtGpsInterval = findViewById(R.id.txtGpsInterval)
        txtGpsIntervalSet = findViewById(R.id.txtGpsIntervalSet)
        txtBatteryInterval = findViewById(R.id.txtBatteryInterval)
        txtBatteryIntervalSet = findViewById(R.id.txtBatteryIntervalSet)
        txtNoShareUsers = findViewById(R.id.txtNoShareUsers)
        recycleView = findViewById(R.id.recycleView)
        btnShare = findViewById(R.id.btnShare)
        btnLocation = findViewById(R.id.btnLocation)
        btnDelete = findViewById(R.id.btnDelete)
    }

    private fun getLockById(lockId: Long): Unit {
        apiService.getLockById(lockId).enqueue(object : Callback<LockModel> {
            override fun onResponse(call: Call<LockModel>, response: Response<LockModel>) {
                if (response.isSuccessful) {
                    updateView(response.body()!!)
                }else{
                    Log.e(TAG, "Error getting lock by id: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<LockModel>, t: Throwable) {
                Log.e(TAG, "Error getting lock by id", t)
            }
        })
    }

    private fun updateView(lockModel: LockModel){
        lockName.text = lockModel.name
        txtName.text = lockModel.name
        txtStatus.text = if (lockModel.online) "Online" else "Offline"
        txtBattery.text = lockModel.reportBattery.toString()
        txtGpsInterval.text = lockModel.reportLocation.toString()
        txtGpsIntervalSet.text = lockModel.reportLocation.toString()
        txtBatteryInterval.text = lockModel.reportBattery.toString()
        txtBatteryIntervalSet.text = lockModel.reportBattery.toString()
        Glide.with(this).load(lockModel.picture).into(imgLock)
        apiService.getUser(lockModel.ownerId!!).enqueue(object : Callback<UserModel>{
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if(response.isSuccessful){
                    txtOwner.text = response.body()?.username
                }else{
                    Log.d(TAG, "Error getting user by id: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                Log.d(TAG, "Error getting user by id", t)
            }
        })

        adapter = SharedUserAdapter(this, lockModel.id!!)
        val userIds: List<Long> = lockModel.users
        if(userIds.isEmpty()){
            txtNoShareUsers.text = "No shared users"
            txtNoShareUsers.visibility = TextView.VISIBLE
            recycleView.visibility = RecyclerView.GONE
        }else {
            txtNoShareUsers.visibility = TextView.GONE
            recycleView.visibility = RecyclerView.VISIBLE
            val sharedUsers = mutableListOf<UserModel>()
            for (userId in userIds) {
                apiService.getUser(userId).enqueue(object : Callback<UserModel> {
                    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                        if (response.isSuccessful) {
                            sharedUsers.add(response.body()!!)
                            adapter.sharedUsers = sharedUsers
                        } else {
                            Log.d(TAG, "Error getting user by id: ${response.errorBody()}")
                        }
                    }

                    override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        Log.d(TAG, "Error getting user by id", t)
                    }
                })
            }
            recycleView.adapter = adapter
            recycleView.layoutManager = LinearLayoutManager(this)
        }

    }

    private fun setListeners(){
        // update picture
        txtModifyLockImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        // change lock name

        txtChangeName.setOnClickListener { showEditTextDialog("Name", txtName.text.toString()) { newValue ->
            txtName.text = newValue
            val lockPatchRequest = LockPatchRequest(eventType = EventType.CHANGE_NAME, name = newValue)
            apiService.updateLock(lockId, lockPatchRequest).enqueue(object : Callback<LockModel>{
                override fun onResponse(call: Call<LockModel>, response: Response<LockModel>) {
                    if(response.isSuccessful){
                        lockName.text = newValue
                        Toast.makeText(this@LockDetails, "Name updated", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d(TAG, "Error updating lock name: ${response.errorBody()}")
                        Toast.makeText(this@LockDetails, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LockModel>, t: Throwable) {
                    Toast.makeText(this@LockDetails, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            })


        } }

        // change report battery interval

        txtBatteryIntervalSet.setOnClickListener {
            showEditTextDialog("Battery Interval", txtBatteryInterval.text.toString()) { newValue ->
                txtBatteryInterval.text = newValue
                val lockPatchRequest = LockPatchRequest(eventType = EventType.CHANGE_REPORT_BATTERY, reportBattery = newValue.toInt())
                apiService.updateLock(lockId, lockPatchRequest).enqueue(object : Callback<LockModel>{
                    override fun onResponse(call: Call<LockModel>, response: Response<LockModel>) {
                        if(response.isSuccessful){
                            txtBatteryIntervalSet.text = newValue
                            Toast.makeText(this@LockDetails, "Battery interval updated", Toast.LENGTH_SHORT).show()
                        }else{
                            Log.d(TAG, "Error updating battery interval: ${response.errorBody()}")
                            Toast.makeText(this@LockDetails, "An error occurred", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LockModel>, t: Throwable) {
                        Toast.makeText(this@LockDetails, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        // change report gps interval

        txtGpsIntervalSet.setOnClickListener {
            showEditTextDialog("GPS Interval", txtGpsInterval.text.toString()) { newValue ->
                txtGpsInterval.text = newValue
                val lockPatchRequest = LockPatchRequest(eventType = EventType.CHANGE_REPORT_LOCATION, reportLocation = newValue.toInt())
                apiService.updateLock(lockId, lockPatchRequest).enqueue(object : Callback<LockModel>{
                    override fun onResponse(call: Call<LockModel>, response: Response<LockModel>) {
                        if(response.isSuccessful){
                            txtGpsIntervalSet.text = newValue
                            Toast.makeText(this@LockDetails, "GPS interval updated", Toast.LENGTH_SHORT).show()
                        }else{
                            Log.d(TAG, "Error updating GPS interval: ${response.errorBody()}")
                            Toast.makeText(this@LockDetails, "An error occurred", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LockModel>, t: Throwable) {
                        Toast.makeText(this@LockDetails, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        // share lock

        btnShare.setOnClickListener {
            showEditTextDialog("Shared User", "") { userEmail ->
                apiService.findByEmail(userEmail).enqueue(object : Callback<UserModel>{
                    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                        if(response.isSuccessful){
                            val sharedUser = response.body()!!
                            val lockPatchRequest = LockPatchRequest(eventType = EventType.ADD_USER, userId = sharedUser.id)
                            apiService.updateLock(lockId, lockPatchRequest).enqueue(object : Callback<LockModel>{
                                override fun onResponse(call: Call<LockModel>, response: Response<LockModel>) {
                                    if(response.isSuccessful){
                                        Toast.makeText(this@LockDetails, "Lock shared", Toast.LENGTH_SHORT).show()
                                        adapter.sharedUsers += sharedUser
                                        adapter.notifyItemInserted(adapter.sharedUsers.size - 1)
                                    }else{
                                        Log.d(TAG, "Error sharing lock: ${response.errorBody()}")
                                        Toast.makeText(this@LockDetails, "An error occurred", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<LockModel>, t: Throwable) {
                                    Toast.makeText(this@LockDetails, "An error occurred", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }else{
                            Toast.makeText(this@LockDetails, "User not found", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        Toast.makeText(this@LockDetails, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })


            }
        }

        // view location
        btnLocation.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("lockId", lockId)
            startActivity(intent)
        }

        // delete lock
        btnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Lock")
            builder.setMessage("Are you sure you want to delete this lock?")
            builder.setPositiveButton("Yes"){ dialog, _ ->
                apiService.deleteLock(lockId).enqueue(object : Callback<LockModel>{
                    override fun onResponse(call: Call<LockModel>, response: Response<LockModel>) {
                        if(response.isSuccessful){
                            Toast.makeText(this@LockDetails, "Lock deleted", Toast.LENGTH_SHORT).show()
                            finish()
                        }else{
                            Log.d(TAG, "Error deleting lock: ${response.errorBody()}")
                            Toast.makeText(this@LockDetails, "An error occurred", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LockModel>, t: Throwable) {
                        Toast.makeText(this@LockDetails, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
                dialog.dismiss()
            }
            builder.setNegativeButton("No"){ dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        }

    }

    private fun handleImageUri(uri: Uri, lockId: Long) {
        val filePath = getRealPathFromURI(this, uri)
        val file = File(filePath!!)
        uploadFile(file, object : FileUploadCallback {
            override fun onSuccess(fileName: String) {
                val url = StringBuilder(getString(R.string.file_server_ip)).append("/").append(fileName).toString()
                val lockPatchRequest = LockPatchRequest(eventType = EventType.CHANGE_PICTURE, picture = url)

                apiService.updateLock(lockId, lockPatchRequest).enqueue(object : Callback<LockModel>{
                    override fun onResponse(call: Call<LockModel>, response: Response<LockModel>) {
                        if(response.isSuccessful){
                            imgLock.setImageURI(uri)
                            Toast.makeText(this@LockDetails, "Photo updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d(TAG, "Error updating lock picture: ${response.errorBody()}")
                            Toast.makeText(this@LockDetails, "An error occurred", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LockModel>, t: Throwable) {
                        Toast.makeText(this@LockDetails, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(this@LockDetails, "File upload failed: $errorMessage", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this@LockDetails, "File uploaded successfully", Toast.LENGTH_SHORT).show()
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


    private fun showEditTextDialog(title: String, currentValue: String, onSave: (String) -> Unit){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit $title")
        val view = layoutInflater.inflate(R.layout.dialog_edit_text, null)
        val editText = view.findViewById<EditText>(R.id.editTextDialog)
        editText.setText(currentValue)
        builder.setView(view)
        builder.setPositiveButton("Save"){ dialog, _ ->
            onSave(editText.text.toString())
            dialog.dismiss()

        }
        builder.setNegativeButton("Cancel"){ dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

}