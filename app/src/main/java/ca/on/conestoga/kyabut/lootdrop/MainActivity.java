package ca.on.conestoga.kyabut.lootdrop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.Timer;
import java.util.TimerTask;

/*
    App Name: Loot Drop
    Authors: Kernen Yabut and James Parks
    Date Creation: 2020-11-16
 */

public class MainActivity extends AppCompatActivity {
    //Variables
    private boolean gameOver = false;
    private boolean gameFinished = false;
    private int currencyTotal = 100;
    private int scoreTotal = 0;
    private static final int MIN = 1;
    private static final int MAX = 100;
    private int numberGenerated = 0;
    private int chestChosen = 0;
    private int reward = 0;

    //Prices for the chests
    private static final int COMMONCHESTPRICE = 3;
    private static final int RARECHESTPRICE = 5;
    private static final int LEGENDARYCHESTPRICE = 10;

    //Buttons
    private Button btnCommonChest;
    private Button btnRareChest;
    private Button btnLegendaryChest;

    //Text
    private TextView txtCurrency;
    private TextView txtStatus;
    private TextView txtReward;
    private TextView txtScoreTotal;

    //Image
    private ImageView imgCurrentImage;

    //Boolean
    private boolean saveState;
    private boolean creatingActivity = false;
    private boolean darkTheme;
    private boolean themeChanged;
    private boolean saveStateChanged;
    private boolean purchaseMade;

    //Context
    Context context;

    //SharedPreferences
    private SharedPreferences sharedPreferences;

    //Timer
    private Timer timerForNotification = null;
    private Timer timerForUnlockingButtons = null;

    //Floating Action Buttons
    FloatingActionButton fabAdd, fabSettings, fabAbout;

    //Textviews for floating button labels
    TextView txtOpenSettings;
    TextView txtOpenAbout;

    // to check whether sub FAB buttons are visible or not.
    Boolean isAllFabsVisible;

    //Generates a random number for the reward
    private static int GenerateReward (int min, int max) {
        return(min + (int)Math.floor(Math.random() * (max - min + 1)));
    }

    //On create
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final AppCompatActivity SELF = this;
        context=this;
        creatingActivity = true;

        //Determines if the player presses a button
        View.OnClickListener btnChestListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Resets variables
                numberGenerated = 0;

                //If player chooses common chest
                if(R.id.btnCommonChest == view.getId())
                {
                    numberGenerated = GenerateReward(MIN, MAX);
                    chestChosen = 1;
                }
                //If player chooses rare chest
                else if(R.id.btnRareChest == view.getId())
                {
                    numberGenerated = GenerateReward(MIN, MAX);
                    chestChosen = 2;
                }

                //If player chooses legendary chest
                else
                {
                    numberGenerated = GenerateReward(MIN, MAX);
                    chestChosen = 3;
                }

                //Determines if they can keep playing or not
                OutOfMoney();

                //If they are out of money - game over
                if (!gameOver )
                {
                    //Resets timer
                    DisplayToast(true);

                    //Calls GameProcess
                    GameProcess(SELF);

                    //Displays toast to encourage player to keep playing after 3 seconds (5 - 2 seconds for
                    //    animation)
                    DisplayToast(true);
                }
                else
                {
                    Snackbar.make(findViewById(R.id.mainLayout),  R.string.gameOver, Snackbar.LENGTH_LONG).show();
                }
            }
        };
        //////////////////////////////////// | MAIN | ////////////////////////////////////

        /*Initial setup for option*/
        //Sets current theme
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        darkTheme = sharedPreferences.getBoolean("theme", false);
        themeChanged = darkTheme;

        //Sets save state
        saveState = sharedPreferences.getBoolean( "saveState", false);
        saveStateChanged = saveState;

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
        setContentView(R.layout.activity_main);

        //Init values
        btnCommonChest = findViewById(R.id.btnCommonChest);
        btnRareChest = findViewById(R.id.btnRareChest);
        btnLegendaryChest = findViewById(R.id.btnLegendaryChest);
        txtCurrency = findViewById(R.id.txtCurrency);
        txtStatus = findViewById(R.id.txtStatus);
        txtReward = findViewById(R.id.txtReward);
        txtScoreTotal = findViewById(R.id.txtScoreTotal);
        imgCurrentImage = findViewById(R.id.imgCurrentImage);

        //Sets game
        txtStatus.setText(R.string.status);
        txtCurrency.setText(String.valueOf(currencyTotal));
        txtScoreTotal.setText(String.valueOf(scoreTotal));
        txtReward.setVisibility(View.GONE);

        //When any button is pressed
        btnCommonChest.setOnClickListener(btnChestListener);
        btnRareChest.setOnClickListener(btnChestListener);
        btnLegendaryChest.setOnClickListener(btnChestListener);

        //Initializes sharedPreferences
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);

        //////////////////////////////////// | FAB | ////////////////////////////////////
        // Register all the FABs with their IDs
        fabAdd = findViewById(R.id.add_fab);
        fabSettings = findViewById(R.id.settings_fab);
        fabAbout = findViewById(R.id.about_fab);

        // Also register the action name text, of all the FABs.
        txtOpenSettings = findViewById(R.id.txt_Open_Settings);
        txtOpenAbout = findViewById(R.id.txt_Open_About);

        //Initially sets them as gone
        fabSettings.setVisibility(View.GONE);
        txtOpenSettings.setVisibility(View.GONE);
        fabAbout.setVisibility(View.GONE);
        txtOpenAbout.setVisibility(View.GONE);

        //Sets to false
        isAllFabsVisible = false;

        //When FAB button is pressed
        fabAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    //If FAB is visible
                    if (!isAllFabsVisible) {

                        fabSettings.show();
                        fabAbout.show();
                        txtOpenSettings.setVisibility(View.VISIBLE);
                        txtOpenAbout.setVisibility(View.VISIBLE);

                        isAllFabsVisible = true;
                    }
                    //If FAB is not visible
                    else {
                        fabSettings.hide();
                        fabAbout.hide();
                        txtOpenSettings.setVisibility(View.GONE);
                        txtOpenAbout.setVisibility(View.GONE);

                        isAllFabsVisible = false;
                    }
                    }
                });

        //When Open Settings is pressed
        fabSettings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);
                    }
                });

        //When Open About is pressed
        fabAbout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                        startActivity(intent);
                    }
                });
    }

    //Determines if the player can continue playing
    private void OutOfMoney() {
        //If they are out of money - if they cannot afford to buy anymore chests, game is over
        if ((currencyTotal >= 0 && currencyTotal <= 2) || currencyTotal < 0)
        {
            gameOver = true;
        }
    }

    //Deducts from currency
    private void DeductCurrency() {
        //Determines what chest they pressed
        if (chestChosen == 1)
        {
            //Determines if they have enough funds to buy the chest
            if(currencyTotal >= COMMONCHESTPRICE)
            {
                currencyTotal = currencyTotal - COMMONCHESTPRICE;
                purchaseMade = true;
            }

            else
                Snackbar.make(findViewById(R.id.mainLayout),  R.string.notEnoughFunds, Snackbar.LENGTH_LONG).show();
        }
        else if(chestChosen == 2)
        {
            //Determines if they have enough funds to buy the chest
            if(currencyTotal >= RARECHESTPRICE)
            {
                currencyTotal = currencyTotal - RARECHESTPRICE;
                purchaseMade = true;
            }
            else
                Snackbar.make(findViewById(R.id.mainLayout),  R.string.notEnoughFunds, Snackbar.LENGTH_LONG).show();
        }
        else
        {
            //Determines if they have enough funds to buy the chest
            if(currencyTotal >= LEGENDARYCHESTPRICE)
            {
                currencyTotal = currencyTotal - LEGENDARYCHESTPRICE;
                purchaseMade = true;
            }
            else
                Snackbar.make(findViewById(R.id.mainLayout),  R.string.notEnoughFunds, Snackbar.LENGTH_LONG).show();
        }

        txtCurrency.setText(String.valueOf(currencyTotal));
    }

    //Lock/unlock buttons
    private void LockButtons(boolean lockButtons)
    {
        if (lockButtons)
        {
            btnCommonChest.setEnabled(false);
            btnRareChest.setEnabled(false);
            btnLegendaryChest.setEnabled(false);
        }
        else
        {
            btnCommonChest.setEnabled(true);
            btnRareChest.setEnabled(true);
            btnLegendaryChest.setEnabled(true);
        }
    }

    //Determines reward
    private int DetermineReward(int chestChosen, int numberGenerated)
    {
        /*
            Drop Rates:
                Common Chest:
                    >Nothing - 15%
                    >Bronze - 45%
                    >Silver - 35%
                    >Gold - 4%
                    >Diamond - 1%
                Rare Chest:
                    >Nothing - 10%
                    >Bronze - 35%
                    >Silver - 40%
                    >Gold - 12%
                    >Diamond - 3%
                Legendary Chest:
                    >Nothing - 5%
                    >Bronze - 25%
                    >Silver - 43%
                    >Gold - 20%
                    >Diamond - 7%
        */

        //If player chooses common chest
        if(chestChosen == 1)
        {
            //Drop rate min/max - common
            final int NOTHINGMIN = 0;
            final int NOTHINGMAX = 15;
            final int BRONZEMIN = 16;
            final int BRONZEMAX = 60;
            final int SILVERMIN = 61;
            final int SILVERMAX = 95;
            final int GOLDMIN = 96;
            final int GOLDMAX = 99;

            //Gets nothing
            if (numberGenerated >= NOTHINGMIN && numberGenerated <= NOTHINGMAX)
            {
                return 1;
            }
            //Gets bronze
            else if (numberGenerated >= BRONZEMIN && numberGenerated <= BRONZEMAX)
            {
                return 2;
            }
            //Gets silver
            else if (numberGenerated >= SILVERMIN && numberGenerated <= SILVERMAX)
            {
                return 3;
            }
            //Gets gold
            else if (numberGenerated >= GOLDMIN && numberGenerated <= GOLDMAX)
            {
                return 4;
            }
            //Gets diamond
            else
            {
                return 5;
            }
        }
        //If player chooses rare chest
        else if(chestChosen == 2)
        {
            //Drop rate min/max - rare
            final int NOTHINGMIN = 0;
            final int NOTHINGMAX = 10;
            final int BRONZEMIN = 11;
            final int BRONZEMAX = 45;
            final int SILVERMIN = 46;
            final int SILVERMAX = 85;
            final int GOLDMIN = 86;
            final int GOLDMAX = 95;

            //Gets nothing
            if (numberGenerated >= NOTHINGMIN && numberGenerated <= NOTHINGMAX)
            {
                return 1;
            }
            //Gets bronze
            else if (numberGenerated >= BRONZEMIN && numberGenerated <= BRONZEMAX)
            {
                return 2;
            }
            //Gets silver
            else if (numberGenerated >= SILVERMIN && numberGenerated <= SILVERMAX)
            {
                return 3;
            }
            //Gets gold
            else if (numberGenerated >= GOLDMIN && numberGenerated <= GOLDMAX)
            {
                return 4;
            }
            //Gets diamond
            else
            {
                return 5;
            }
        }

        //If player chooses legendary chest
        else
        {
            //Drop rate min/max - legendary
            final int NOTHINGMIN = 0;
            final int NOTHINGMAX = 5;
            final int BRONZEMIN = 6;
            final int BRONZEMAX = 30;
            final int SILVERMIN = 31;
            final int SILVERMAX = 73;
            final int GOLDMIN = 74;
            final int GOLDMAX = 93;

            //Gets nothing
            if (numberGenerated >= NOTHINGMIN && numberGenerated <= NOTHINGMAX)
            {
                return 1;
            }
            //Gets bronze
            else if (numberGenerated >= BRONZEMIN && numberGenerated <= BRONZEMAX)
            {
                return 2;
            }
            //Gets silver
            else if (numberGenerated >= SILVERMIN && numberGenerated <= SILVERMAX)
            {
                return 3;
            }
            //Gets gold
            else if (numberGenerated >= GOLDMIN && numberGenerated <= GOLDMAX)
            {
                return 4;
            }
            //Gets diamond
            else
            {
                return 5;
            }
        }
    }

    //Sets reward animation text
    private void SetReward()
    {
        txtStatus.setText(R.string.reward);

        //Sets picture of reward to the respective reward generated
        if (reward == 1)
        {
            imgCurrentImage.setVisibility(View.INVISIBLE);
            txtReward.setText(R.string.rewardNothing);
        }
        else if (reward == 2)
        {
            imgCurrentImage.setImageResource(R.drawable.ic_bronze);
            txtReward.setText(R.string.rewardBronze);
        }
        else if (reward == 3)
        {
            imgCurrentImage.setImageResource(R.drawable.ic_silver);
            txtReward.setText(R.string.rewardSilver);
        }
        else if (reward == 4) {
            imgCurrentImage.setImageResource(R.drawable.ic_gold);
            txtReward.setText(R.string.rewardGold);
        }
        else if (reward == 5)
        {
            imgCurrentImage.setImageResource(R.drawable.ic_diamond);
            txtReward.setText(R.string.rewardDiamond);
        }

        //Sets txtReward to be visible
        txtReward.setVisibility(View.VISIBLE);
    }

    //Adds to score
    private void AddToScore()
    {
        //Variables
        final int BRONZEVALUE = 100;
        final int SILVERVALUE = 250;
        final int GOLDVALUE = 1000;
        final int DIAMONDVALUE = 5000;

        //Adds to score depending on what they got
        if (reward == 2)
        {
            scoreTotal += BRONZEVALUE;
        }
        else if (reward == 3)
        {
            scoreTotal += SILVERVALUE;
        }
        else if (reward == 4)
        {
            scoreTotal += GOLDVALUE;
        }
        else if (reward == 5)
        {
            scoreTotal += DIAMONDVALUE;
        }

        //Updates score
        txtScoreTotal.setText(String.valueOf(scoreTotal));

        //Adds to high score
        ((LootDropApplication) getApplication()).addHighScore(scoreTotal);
    }

    //Animates lootbox action
    private void Animate()
    {
        Animation shake = AnimationUtils.loadAnimation(context, R.anim.shakeanimation);
        imgCurrentImage.startAnimation(shake);

        //Animates explosion
        imgCurrentImage.animate().alpha(1f).setDuration(250).setListener(
            new AnimatorListenerAdapter(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    //Sets to an explosion
                    imgCurrentImage.setImageResource(R.drawable.ic_explosion);

                    //Animates reward
                    imgCurrentImage.animate().alpha(1f).setDuration(250).setListener(
                        new AnimatorListenerAdapter(){
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                SetReward();
                                AddToScore();
                            }
                        }
                    );
                }
            }
        );
    }
    /*
    GameProcess function - further processes the game whenever called
     */
    private void GameProcess(AppCompatActivity self)
    {
        if (self != null)
        {
            gameFinished = false;
            purchaseMade = false;

            //Deduct from currency
            DeductCurrency();

            //If they don't have enough money to make a purchase
            if (purchaseMade)
            {
                //Resets everything
                //Resets timer
                UnlockButtons(true);
                txtStatus.setText(R.string.opening);
                txtReward.setVisibility(View.GONE);
                imgCurrentImage.setImageResource(R.drawable.ic_loot_box);
                imgCurrentImage.setVisibility(View.VISIBLE);

                //Locks buttons to prevent spam clicking
                LockButtons(true);

                //Determines Reward Received
                reward = DetermineReward(chestChosen, numberGenerated);

                //Plays lootbox animation
                Animate();

                //Sets gameFinished to true
                gameFinished = true;

                //Unlocks buttons
                UnlockButtons(true);

                //Adds reward to database
                ((LootDropApplication) getApplication()).addGameResult(chestChosen, reward);
            }
        }
        //If it's onClose
        else
        {
            saveState = sharedPreferences.getBoolean( "saveState", false);

            if (saveState)
            {
                SetReward();

                //Changes text
                txtCurrency.setText(String.valueOf(currencyTotal));
                txtScoreTotal.setText(String.valueOf(scoreTotal));
            }

            //If savestate pref was changed - hide previous result until user plays
            if(saveStateChanged != saveState)
            {
                //Calls editor
                SharedPreferences.Editor editor = sharedPreferences.edit();

                //Inserts values into editor
                editor.putInt("currencyTotal", currencyTotal);
                editor.putInt("reward", reward);
                editor.putInt("scoreTotal", scoreTotal);
                editor.putBoolean("gameOver", gameOver);
                editor.putBoolean("gameFinished", gameFinished);

                //Commits editor
                editor.commit();
                saveStateChanged = saveState;
            }
        }
    }

    //Unlock buttons after the animation has finished
    private void UnlockButtons(boolean setTimer){
        if (setTimer){

            if (timerForUnlockingButtons != null)
                timerForUnlockingButtons.cancel();

            timerForUnlockingButtons = new  Timer(true);

            timerForUnlockingButtons.schedule(new TimerTask() {
                @Override
                public void run() { //Belongs to the background thread, timer
                    timerForUnlockingButtons.cancel();
                    timerForUnlockingButtons = null;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() { //Belongs to the main UI thread of activity

                            //Unlocks buttons
                            LockButtons(false);
                        }
                    });
                }
            }, 500);
        }
        else{
            timerForUnlockingButtons.cancel();
        }
    }

    //Resets game
    private void ResetGame()
    {
        currencyTotal = 100;
        scoreTotal = 0;
        txtCurrency.setText(String.valueOf(currencyTotal));
        txtScoreTotal.setText(String.valueOf(scoreTotal));
        gameOver = false;
    }

    /*
  DisplayToast - displays a toast to encourage the user to keep playing
 */
    private void DisplayToast(boolean setTimer){
        if (setTimer){

            if (timerForNotification != null)
                timerForNotification.cancel();

            timerForNotification = new  Timer(true);

            timerForNotification.schedule(new TimerTask() {
                @Override
                public void run() { //Belongs to the background thread, timer
                    timerForNotification.cancel();
                    timerForNotification = null;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() { //Belongs to the main UI thread of activity


                        Snackbar.make(findViewById(R.id.mainLayout),  R.string.keepPlaying, Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }, 5000);
        }
        else{
            timerForNotification.cancel();
        }
    }

    //onPause override
    @Override
    protected void onPause() {
        //Calls editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Inserts values into editor
        editor.putInt("currencyTotal", currencyTotal);
        editor.putInt("scoreTotal", scoreTotal);
        editor.putInt("reward", reward);
        editor.putBoolean("gameOver", gameOver);
        editor.putBoolean("gameFinished", gameFinished);

        //Commits editor
        editor.commit();

        super.onPause();
    }

    //onResume override
    @Override
    protected void onResume() {
        //Gets values from sharedPreferences
        saveState = sharedPreferences.getBoolean( "saveState", false);
        darkTheme = sharedPreferences.getBoolean("theme", false);
        gameFinished = sharedPreferences.getBoolean("gameFinished", false);
        gameOver = sharedPreferences.getBoolean("gameOver", false);
        currencyTotal = sharedPreferences.getInt("currencyTotal", 100);
        scoreTotal = sharedPreferences.getInt("scoreTotal", 0);
        reward = sharedPreferences.getInt("reward", 0);

        //If game has finished - display results
        if (saveState || !creatingActivity)
        {
            if (gameFinished)
            {
                GameProcess(null);
            }
        }

        //Changes text
        txtCurrency.setText(String.valueOf(currencyTotal));
        txtScoreTotal.setText(String.valueOf(scoreTotal));

        creatingActivity = false;

        super.onResume();
    }

    @Override
    protected void onRestart() {
        darkTheme = sharedPreferences.getBoolean("theme", false);

        //If theme changes - restarts current app
        if (themeChanged != darkTheme)
        {
            finish();
            startActivity(getIntent());
            themeChanged = darkTheme;
        }

        super.onRestart();
    }

    //Menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lootdrop_menu, menu);
        return true;
    }

    //Menu options
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean ret = true;

        switch (item.getItemId())
        {
            case R.id.menuSettings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.menuStats:
                startActivity(new Intent(getApplicationContext(), StatsActivity.class));
                break;
            case R.id.menuCredits:
                startActivity(new Intent(getApplicationContext(), CreditsActivity.class));
                break;
            case R.id.menuReset:
                ResetGame();
                break;
            default:
                ret = super.onOptionsItemSelected(item);
                break;
        }
        return ret;
    }

    @Override
    protected void onStop() {
        startService(new Intent(getApplicationContext(), NotificationService.class));
        super.onStop();
    }
}
