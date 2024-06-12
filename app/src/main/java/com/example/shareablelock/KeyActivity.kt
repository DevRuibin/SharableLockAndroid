package com.example.shareablelock

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class KeyActivity : AppCompatActivity() {
    private val TAG = "KeyActivity"
    private lateinit var nfcStatusTextView: TextView
    private lateinit var nfcDataTextView: TextView
    private lateinit var noNfcTextView: TextView
    private lateinit var applyNfcButton: Button
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var companyNameTextView: TextView
    private lateinit var scanButton: Button
    private lateinit var cardLayout: RelativeLayout
    private lateinit var userModel: UserModel
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var pendingIntent: PendingIntent
    private lateinit var intentFiltersArray: Array<IntentFilter>
    private lateinit var techListsArray: Array<Array<String>>
    lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_key)
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
                R.id.nav_profile -> {
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_notifications -> {
                    val intent = Intent(this, MessageActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_key -> {
                    finish()
                }
            }
            true
        }
        initViews()
        applyNfcButton.setOnClickListener {
            applyNFC()
            haveNfc()
        }
        if(haveNfc()){
            userEmailTextView.text = userModel.email
            userNameTextView.text = userModel.username
            companyNameTextView.text = getString(R.string.app_name)
            scanButton.setOnClickListener {
                Log.d(TAG, "Scan button clicked")
                val card: NfcModel = PreferenceHelper.getNfc(this)!!

            }

        }


    }

    private fun applyNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            nfcStatusTextView.text = "NFC is not available on this device."
            return
        }
        if (!nfcAdapter!!.isEnabled) {
            nfcStatusTextView.text = "NFC is disabled."
        }
        val nfcModel = createNfcData()
        PreferenceHelper.saveNfc(this, nfcModel)
        Toast.makeText(this, "NFC applied successfully", Toast.LENGTH_SHORT).show()

    }

    private fun initViews() {
        nfcStatusTextView = findViewById(R.id.nfc_status)
        nfcDataTextView = findViewById(R.id.nfc_data)
        noNfcTextView = findViewById(R.id.noNfc)
        applyNfcButton = findViewById(R.id.applyNfc)
        userNameTextView = findViewById(R.id.text_user_name)
        userEmailTextView = findViewById(R.id.text_user_email)
        companyNameTextView = findViewById(R.id.text_company_name)
        cardLayout = findViewById(R.id.hasCard)
        scanButton = findViewById(R.id.btnScan)
        userModel = PreferenceHelper.getUser(this)!!
    }

    private fun haveNfc(): Boolean {
        val nfcModel = PreferenceHelper.getNfc(this)
        nfcModel?.let{
            applyNfcButton.visibility = RelativeLayout.GONE
            noNfcTextView.visibility = RelativeLayout.GONE
            cardLayout.visibility = RelativeLayout.VISIBLE
            return true
        }?: run{
            cardLayout.visibility = RelativeLayout.GONE
            applyNfcButton.visibility = RelativeLayout.VISIBLE
            noNfcTextView.visibility = RelativeLayout.VISIBLE
            return false
        }
    }

    private fun createNfcData(): NfcModel{
        val token = "Bearer " + "kkkcladsnkcnewi" // todo use real tokne
        return NfcModel(userModel.id!!, token)
    }
}