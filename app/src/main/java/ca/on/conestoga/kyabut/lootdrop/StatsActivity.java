package ca.on.conestoga.kyabut.lootdrop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StatsActivity extends AppCompatActivity {
    //SharedPreferences
    private SharedPreferences sharedPreferences;
    private boolean darkTheme;

    //TextView
    private TextView txtLootCratesOpenedTotal;
    private TextView txtHighestScoreTotal;
    private TextView txtFavouriteCrateName;
    private TextView txtLootReceivedTotal;
    private TextView txtBronzeCoinsAwardedTotal;
    private TextView txtSilverCoinsAwardedTotal;
    private TextView txtGoldCoinsAwardedTotal;
    private TextView txtDiamondGemsAwardedTotal;

    //Button
    private Button btnResetStats;

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
        setContentView(R.layout.activity_stats);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        btnResetStats = findViewById(R.id.btnResetStats);

        //When reset button is pressed
        btnResetStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LootDropApplication) getApplication()).resetTableStats();
                refreshStats();
            }
        });

        refreshStats();
    }

    //Refreshes stats
    private void refreshStats(){
        final LootDropApplication application;
        String chestName;
        int chestType;



        //Gets all viewIds
        txtLootCratesOpenedTotal = findViewById(R.id.txtLootCratesOpenedTotal);
        txtHighestScoreTotal = findViewById(R.id.txtHighestScoreTotal);
        txtFavouriteCrateName = findViewById(R.id.txtFavouriteCrateName);
        txtLootReceivedTotal = findViewById(R.id.txtLootReceivedTotal);
        txtBronzeCoinsAwardedTotal = findViewById(R.id.txtBronzeCoinsAwardedTotal);
        txtSilverCoinsAwardedTotal = findViewById(R.id.txtSilverCoinsAwardedTotal);
        txtGoldCoinsAwardedTotal = findViewById(R.id.txtGoldCoinsAwardedTotal);
        txtDiamondGemsAwardedTotal = findViewById(R.id.txtDiamondGemsAwardedTotal);

        application = ((LootDropApplication) getApplication());

        //Gets chest type
        chestType = application.getFavouriteLootCrate();

        //Determines which crate it is and returns the name of the chest
        switch (chestType)
        {
            case 1:
                chestName = "Common Chest";
                break;
            case 2:
                chestName = "Rare Chest";
                break;
            case 3:
                chestName = "Legendary Chest";
                break;
            default:
                chestName = "None";
                break;
        }

        //Sets number of crates opened
        txtLootCratesOpenedTotal.setText(
                "" + application.getLootCratesOpened()
        );

        //Sets highest score
        txtHighestScoreTotal.setText(
                "" + application.getHighestScore()
        );

        //Sets favourite crate name
        txtFavouriteCrateName.setText(
                "" + chestName
        );

        //Sets number of loot received
        txtLootReceivedTotal.setText(
                "" + application.getRewardsReceived()
        );

        //Sets number of bronze coins received
        txtBronzeCoinsAwardedTotal.setText(
                "" + application.getBronzeCoinAmount()
        );

        //Sets number of silver coins received
        txtSilverCoinsAwardedTotal.setText(
                "" + application.getSilverCoinAmount()
        );


        //Sets number of gold coins received
        txtGoldCoinsAwardedTotal.setText(
                "" + application.getGoldCoinAmount()
        );

        //Sets number of diamond gems received
        txtDiamondGemsAwardedTotal.setText(
                "" + application.getDiamondGemAmount()
        );

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
