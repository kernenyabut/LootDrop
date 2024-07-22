package ca.on.conestoga.kyabut.lootdrop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

public class CreditsActivity extends AppCompatActivity {
    //SharedPreferences
    private SharedPreferences sharedPreferences;
    private boolean darkTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        darkTheme = sharedPreferences.getBoolean("theme", false);

        //Sets theme
        if (!darkTheme)
        {
            setTheme(R.style.AppTheme);
        }
        else
        {
            setTheme(R.style.DarkAppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean ret = true;
        switch (item.getItemId())
        {
            case android.R.id.home:
                super.onBackPressed();
                break;
            default:
                ret = super.onOptionsItemSelected(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
