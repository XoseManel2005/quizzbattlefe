import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("quizzPrefs", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit().putString("AUTH_TOKEN", token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString("AUTH_TOKEN", null)
    }

    fun clearToken() {
        prefs.edit().clear().apply()
    }
}
