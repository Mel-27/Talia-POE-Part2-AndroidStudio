import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }


        val btnLight = findViewById<CardView>(R.id.btnLight)
        val btnDark  = findViewById<TextView>(R.id.btnDark)

        btnLight.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        btnDark.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}