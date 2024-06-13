package com.example.shareablelock

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Response

class SharedUserAdapter(val context: Context, private val lockId: Long):RecyclerView.Adapter<SharedUserAdapter.SharedUserViewHolder>()  {
    private val TAG = "SharedUserAdapter"

    inner class SharedUserViewHolder(itemView: View, lockId: Long): RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        private val deleteButton: TextView = itemView.findViewById(R.id.deleteButton)
        private val apiService = RetrofitHelper.getApiService(context)
        init {
            deleteButton.setOnClickListener {
                Log.d(TAG, "SharedUserViewHolder: Clicked")
                val sharedUser = sharedUsers[adapterPosition]
                val userId = sharedUser.id
                val lockPatchRequest = LockPatchRequest(EventType.REMOVE_USER, userId = userId)
                apiService.updateLock(lockId, lockPatchRequest).enqueue(object : retrofit2.Callback<LockModel> {
                    override fun onResponse(call: Call<LockModel>, response: Response<LockModel>) {
                        if (response.isSuccessful) {
                            Log.d(TAG, "onResponse: User removed")
                            sharedUsers = sharedUsers.filter { it.id != userId }
                        } else {
                            Log.d(TAG, "onResponse: ${response.errorBody()}")
                        }
                    }

                    override fun onFailure(call: Call<LockModel>, t: Throwable) {
                        Log.d(TAG, "onFailure: ${t.message}")
                    }
                })

            }
        }
    }

    private var _sharedUsers: List<UserModel> = ArrayList()
    var sharedUsers: List<UserModel>
        get() = _sharedUsers
        set(value) {
            _sharedUsers = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SharedUserViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_share_user, parent, false)
        return SharedUserViewHolder(view, lockId)
    }

    override fun onBindViewHolder(holder: SharedUserViewHolder, position: Int) {
        val sharedUser = sharedUsers[position]
        holder.userName.text = sharedUser.username

    }

    override fun getItemCount(): Int = sharedUsers.size
}