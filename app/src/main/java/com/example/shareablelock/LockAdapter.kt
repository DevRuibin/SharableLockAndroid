package com.example.shareablelock

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LockAdapter(val context: Context): RecyclerView.Adapter<LockAdapter.LockViewHolder>(){
    private val TAG: String = "LockAdapter"
    inner class LockViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var lockName: TextView = itemView.findViewById<TextView>(R.id.lock_name)
        var lockImage: ImageView = itemView.findViewById<ImageView>(R.id.lock_image)
        var lockStatus: TextView = itemView.findViewById<TextView>(R.id.lock_status)
        var lockStatusValue: TextView = itemView.findViewById<TextView>(R.id.lock_status_value)
        var lockBattery: TextView = itemView.findViewById<TextView>(R.id.lock_battery)
        var lockBatteryValue: TextView = itemView.findViewById<TextView>(R.id.lock_battery_value)
        var lockOwner: TextView = itemView.findViewById<TextView>(R.id.lock_owner)
        var lockOwnerValue: TextView = itemView.findViewById<TextView>(R.id.lock_owner_value)
        var id: Long = -1


        init {
            itemView.setOnClickListener {
                Log.d(TAG, "LockViewHolder: Clicked")
                Log.d(TAG, "LockViewHolder: $id")
                // TODO: Open lock details with lock id
                val intent = Intent(context, LockDetails::class.java)
                intent.putExtra("lockId", id)
                context.startActivity(intent)
            }
        }
    }

    private var _locks: List<LockModel> = ArrayList()
    var locks: List<LockModel>
        get() = _locks
        set(value) {
            _locks = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LockViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false)
        return LockViewHolder(view)
    }

    override fun onBindViewHolder(holder: LockViewHolder, position: Int) {
        Log.d(TAG, "OnBindViewHolder: Called")
        holder.lockName.text = locks[position].name
        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(locks[position].picture)
            .into(holder.lockImage)

        holder.lockStatusValue.text = locks[position].locked.toString()
        holder.lockBatteryValue.text = locks[position].power.toString()
        holder.id = locks[position].id!!
        val userId = locks[position].ownerId
        val apiService = RetrofitHelper.getApiService(context)
        apiService.getUser(userId!!).enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if(response.isSuccessful){
                    holder.lockOwnerValue.text = response.body()?.username
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }

        })
    }

    override fun getItemCount(): Int  = locks.size
}