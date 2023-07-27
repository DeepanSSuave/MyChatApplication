package com.example.mychatapplication.di

import android.content.SharedPreferences
import com.example.mychatapplication.model.repositary.CurrentUser
import com.google.gson.Gson
import javax.inject.Inject

class ChatPreferenceManager @Inject constructor(private val sharedPref : SharedPreferences) {

        private val editor: SharedPreferences.Editor = sharedPref.edit()

        fun set(key: String, value: String) {
            editor.putString(key, value)
                .apply()
        }

        fun set(key: String, value: Int) {
            editor.putInt(key, value)
                .apply()
        }

        fun set(key: String, value: Boolean) {
            editor.putBoolean(key, value)
                .apply()
        }

        fun set(key: String, value: Float) {
            editor.putFloat(key, value)
                .apply()
        }

        fun getInt(key: String) = sharedPref.getInt(key, 0)

        fun getString(key: String): String {
            sharedPref.getString(key, null).let {
                if (it.isNullOrEmpty() && key == "AccessToken") {
                    return sharedPref.getString("RefreshToken",null).toString()
                }
                return it?:""
            }
        }

        fun getBoolean(key: String) = sharedPref.getBoolean(key, false)

        fun removeItem (key : String) = editor.remove(key).apply()

        fun clear() {
            editor.clear()
                .apply()
        }

        fun getCurrentUser(): CurrentUser {
            val stringUser = sharedPref.getString("CurrentUser",null)
            return Gson().fromJson(stringUser, CurrentUser::class.java)
        }

        fun setCurrentUser(user: String?) {
            set("CurrentUser", Gson().toJson(user))
        }


}