package com.example.winson.charades;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private int count;
    private TextView t;
    private TextView timer;
    private TextView sensText;
    private TextView title;
    private TextView scores;
    private TextView namesText;
    private Button b;
    private Button help;
    private Button hsButton;
    private Button showWordListButton;
    private RelativeLayout relativeLayout;
    private String[] array;
    private int score;
    private ArrayList<String> arrayList;
    private ArrayList<String> wordOrder;
    private ArrayList<Boolean> isCorrect;
    private double sensitivity;
    private RadioGroup radioGroup;
    private ObjectAnimator time;
    private int timeLeft;
    private float translationY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        t = (TextView) findViewById(R.id.textView);
        t.setVisibility(View.INVISIBLE);
        timer = (TextView) findViewById(R.id.timer);
        timer.setVisibility(View.INVISIBLE);
        sensText=(TextView)findViewById(R.id.sensitivity);
        title=(TextView)findViewById(R.id.title);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "Xoxoxa.ttf");
        title.setTypeface(myTypeface);

        scores=(TextView)findViewById(R.id.scores);
        namesText=(TextView)findViewById(R.id.names);
        b = (Button) findViewById(R.id.button);
        help=(Button)findViewById(R.id.help);
        hsButton=(Button)findViewById(R.id.hsButton);
        showWordListButton=(Button)findViewById(R.id.showWordsListButton);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_main);
        array = getResources().getStringArray(R.array.words);
        sensitivity=3;
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);

        if(!getPreferences(MODE_PRIVATE).contains("names")) {
            resetHighscores();
        }
    }
    private void calculateTranslationY(){
        translationY=(relativeLayout.getHeight()-t.getHeight())/2;
    }
    private void resetHighscores(){
        getPreferences(MODE_PRIVATE).edit().clear().apply();
        getPreferences(MODE_PRIVATE).edit().putInt("Daniel",12).apply();
        getPreferences(MODE_PRIVATE).edit().putInt("Shane",9).apply();
        getPreferences(MODE_PRIVATE).edit().putInt("Samantha",8).apply();
        getPreferences(MODE_PRIVATE).edit().putInt("Kelly",7).apply();
        getPreferences(MODE_PRIVATE).edit().putInt("Andrew",5).apply();
        getPreferences(MODE_PRIVATE).edit().putString("names","Daniel,Shane,Samantha,Kelly,Andrew").apply();
    }

    public void showHighscores(View view){
        SharedPreferences pref=getPreferences(MODE_PRIVATE);
        AlertDialog dialog= new AlertDialog.Builder(this).create();
        dialog.setTitle("Highscores");
        dialog.setIcon(getResources().getIdentifier("highscores","drawable",getPackageName()));
        LinearLayout ll=new LinearLayout(this);
        ll.setPadding(20,0,20,20);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        TextView t1=new TextView(this);
        t1.setText("");
        t1.setTextSize(20);
        t1.setPadding(0,0,0,20);
        TextView t2=new TextView(this);
        t2.setText("");
        t2.setTextSize(20);
        t2.setGravity(Gravity.RIGHT);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight=15;
        LinearLayout.LayoutParams params2=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        Scanner sc=new Scanner(pref.getString("names",""));
        sc.useDelimiter(",");
        int i=0;
        String s;
        while(sc.hasNext()){
            ++i;
            s=sc.next();
            t1.setText(t1.getText()+"\n"+i+". "+s);
            t2.setText(t2.getText()+"\n"+pref.getInt(s,0));
        }
        ll.addView(t1,params);
        params2.weight=1;
        ll.addView(t2,params2);
        dialog.setView(ll);
        dialog.show();

    }
    public void showHelp(View view){
        AlertDialog dialog= new AlertDialog.Builder(this).create();
        dialog.setTitle("How To Play");
        dialog.setIcon(getResources().getIdentifier("how_to_play","drawable",getPackageName()));
        dialog.setMessage("Charades! NUSH Edition is exactly as its name implies. Hold your phone in front of your forehead and get some friends to act out the word or phrase! If you guess correctly, tilt your phone downwards until you feel a vibration. To pass, tilt upwards. Get as many right as you can in 1 minute!");
        dialog.show();
    }
    public void showWordList(View view){
        AlertDialog dialog=new AlertDialog.Builder(this).create();
        dialog.setTitle("Words List");
        ScrollView scrollView=new ScrollView(this);
        LinearLayout ll=new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(20,0,20,20);
        for(int i=0;i<wordOrder.size();++i){
            TextView textView=new TextView(this);
            textView.setText(wordOrder.get(i));
            textView.setTextSize(20);
            textView.setGravity(Gravity.CENTER);
            if(i==isCorrect.size()){
                textView.setText(wordOrder.get(i)+" (NOT ANSWERED)");
            }
            else {
                if (isCorrect.get(i)) {
                    textView.setTextColor(Color.GREEN);
                } else {
                    textView.setTextColor(Color.RED);
                }
            }
            ll.addView(textView);
        }
        scrollView.addView(ll);
        dialog.setIcon(ContextCompat.getDrawable(this,R.drawable.words_list));
        dialog.setView(scrollView);
        dialog.show();
    }

    public void play(View view) {
        hsButton.setVisibility(View.INVISIBLE);
        help.setVisibility(View.INVISIBLE);
        radioGroup.setVisibility(View.INVISIBLE);
        radioGroup.setEnabled(false);
        sensText.setVisibility(View.INVISIBLE);
        title.setVisibility(View.INVISIBLE);
        if(((RadioButton)findViewById(R.id.lowSens)).isChecked()){//low sensitivity
            sensitivity=4;
        }
        else if(((RadioButton)findViewById(R.id.normalSens)).isChecked()){//normal sensitivity
            sensitivity=3;
        }
        else{//high sensitivity
            sensitivity=2.4;
        }

        arrayList = new ArrayList<String>();
        for (String s : array) {
            arrayList.add(s);

        }
        score = 0;
        wordOrder=new ArrayList<>();
        isCorrect=new ArrayList<>();
        relativeLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_background));
        b.setVisibility(View.INVISIBLE);
        b.setEnabled(false);
        t.setVisibility(View.VISIBLE);
        t.setText("3");
        final ObjectAnimator animator = ObjectAnimator.ofFloat(t, "alphaBy", 0, 0).setDuration(1000);
        animator.setRepeatCount(3);
        final Vibrator v=(Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(400);
        animator.addListener(new AnimatorListenerAdapter() {
            int i = 3;

            @Override
            public void onAnimationRepeat(Animator animation) {
                --i;
                if (i == 0) {
                    v.vibrate(750);
                    start();
                    animator.cancel();
                    return;
                }
                v.vibrate(400);
                t.setText(i + "");
            }
        });
        animator.start();
        calculateTranslationY();
    }

    public void start() {
        timer.setVisibility(View.VISIBLE);
        timer.setText("60");
        count = 0;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
        Random r = new Random();
        int next=r.nextInt(arrayList.size());
        t.setText(arrayList.get(next).toUpperCase());
        wordOrder.add(arrayList.get(next).toUpperCase());
        arrayList.remove(next);
        timeLeft=120;
        time = ObjectAnimator.ofFloat(timer, "alphaBy", 0, 0).setDuration(500);
        time.setRepeatCount(120);
        time.setStartDelay(1000);
        time.addListener(new AnimatorListenerAdapter() {
            @Override
            //called when animation first starts or when game resumes after tilting
            public void onAnimationStart(Animator animation){
                --timeLeft;
                if(timeLeft==1){
                    sensorManager.unregisterListener(MainActivity.this);
                    time.removeAllListeners();
                    endGame();
                    return;
                }
                timer.setText(timeLeft/2 + "");
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
                --timeLeft;
                if (timeLeft == 1) {
                    sensorManager.unregisterListener(MainActivity.this);
                    time.removeAllListeners();
                    endGame();
                    return;
                }
                timer.setText(timeLeft/2 + "");
            }
        });
        time.start();
    }
    public void endGame(){
        relativeLayout.setBackground(ContextCompat.getDrawable(this,R.drawable.main_screen));
        timer.setVisibility(View.INVISIBLE);
        //update and display highscores
        final ArrayList<String> names=new ArrayList<String>();
        final SharedPreferences pref=getPreferences(MODE_PRIVATE);
        Scanner sc = new Scanner(pref.getString("names","Daniel,Shane,Samantha,Kelly,Andrew"));
        sc.useDelimiter(",");
        boolean added=false;
        String next;
        while(sc.hasNext()){
            next=sc.next();
            if(score>pref.getInt(next,0)&&!added){
                names.add("Your Name?");
                pref.edit().putInt("Your Name?",score).apply();
                added=true;
            }
            names.add(next);
        }
        final boolean isNewHighscore=added;

        namesText.setVisibility(View.VISIBLE);
        namesText.setAlpha(0);
        namesText.setText("Highscores\n1. "+names.get(0)+"\n2. "+names.get(1)+"\n3. "+names.get(2)+"\n4. "+names.get(3)+"\n5. "+names.get(4));
        namesText.animate().alphaBy(1).setDuration(1000).start();

        showWordListButton.setVisibility(View.VISIBLE);
        showWordListButton.setAlpha(0);
        showWordListButton.animate().alphaBy(1).setDuration(1000).start();

        scores.setVisibility(View.VISIBLE);
        scores.setAlpha(0);
        scores.setText("\n"+pref.getInt(names.get(0),12)+"\n"+pref.getInt(names.get(1),9)+"\n"+pref.getInt(names.get(2),8)+"\n"+pref.getInt(names.get(3),7)+"\n"+pref.getInt(names.get(4),5));
        scores.animate().alphaBy(1).setDuration(1000).start();

        t.setText("YOUR SCORE: " + score);
        t.animate().translationYBy(-translationY).setDuration(750).setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                t.animate().setListener(null);
                if(isNewHighscore) {
                    final EditText editText = new EditText(MainActivity.this);
                    editText.setHint("ENTER YOUR NAME");
                    editText.setWidth(relativeLayout.getWidth()/4*3);
                    editText.setTextSize(25);
                    editText.setId(View.generateViewId());
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW, t.getId());
                    relativeLayout.addView(editText, params);
                    editText.setTranslationY(t.getTranslationY());

                    final Button ok=new Button(MainActivity.this);
                    ok.setTextSize(25);
                    ok.setWidth(relativeLayout.getWidth()/6);
                    ok.setText("OK");
                    RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params2.addRule(RelativeLayout.RIGHT_OF,editText.getId());
                    params2.addRule(RelativeLayout.BELOW,t.getId());
                    params2.setMarginStart(20);
                    relativeLayout.addView(ok,params2);
                    ok.setTranslationY(t.getTranslationY());

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(editText.getText().length()>25){
                                editText.setText("");
                                editText.setHint("25 CHARACTERS MAX");
                            }
                            else if(editText.getText().equals("")){
                                editText.setHint("CANNOT BE EMPTY");
                            }
                            else{
                                if(editText.getText().toString().matches("[a-zA-Z ]+")){
                                    namesText.setText(namesText.getText().toString().replace("Your Name?",editText.getText()));
                                    pref.edit().putInt(editText.getText().toString(),score).apply();
                                    names.add(names.indexOf("Your Name?"),editText.getText().toString());
                                    names.remove("Your Name?");
                                    pref.edit().putString("names",names.get(0)+","+names.get(1)+","+names.get(2)+","+names.get(3)+","+names.get(4)).apply();
                                    pref.edit().remove("Your Name?").apply();
                                    relativeLayout.removeView(editText);
                                    relativeLayout.removeView(ok);
                                    final Button returnButton=new Button(MainActivity.this);
                                    RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    params3.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                    params3.addRule(RelativeLayout.BELOW,t.getId());
                                    returnButton.setTranslationY(-translationY);
                                    returnButton.setText("RETURN");
                                    returnButton.setTextSize(25);
                                    relativeLayout.addView(returnButton,params3);
                                    returnButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            relativeLayout.removeView(returnButton);
                                            returnClick();
                                        }
                                    });
                                }
                                else{
                                    editText.setText("");
                                    editText.setHint("ONLY A-Z OR SPACE ALLOWED");
                                }
                            }
                        }
                    });
                }
                else{
                    final Button returnButton=new Button(MainActivity.this);
                    RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params3.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    params3.addRule(RelativeLayout.BELOW,t.getId());
                    returnButton.setTranslationY(-translationY);
                    returnButton.setText("RETURN");
                    returnButton.setTextSize(25);
                    relativeLayout.addView(returnButton,params3);
                    returnButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            relativeLayout.removeView(returnButton);
                            returnClick();
                        }
                    });
                }
            }
        }).start();
    }
    public void returnClick(){//when user returns to main screen
        namesText.setVisibility(View.INVISIBLE);
        scores.setVisibility(View.INVISIBLE);

        help.setVisibility(View.VISIBLE);
        hsButton.setVisibility(View.VISIBLE);
        showWordListButton.setVisibility(View.INVISIBLE);
        title.setVisibility(View.VISIBLE);
        t.setVisibility(View.INVISIBLE);
        t.setTranslationY(0);
        b.setVisibility(View.VISIBLE);
        b.setEnabled(true);
        sensText.setVisibility(View.VISIBLE);
        radioGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if(count==0&&Math.abs(event.values[1]+0.046)>sensitivity){
                sensorManager.unregisterListener(this);
                if(event.values[1]>0){
                    answered(true);
                }else{
                    answered(false);
                }
            }
        }
    }

    public void answered(boolean pass){
        if(timeLeft<=2){
            return;
        }
        final long playTime=time.getCurrentPlayTime();
        if(time!=null)
            time.end();
        final Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        if(pass) {
            relativeLayout.setBackgroundColor(Color.RED);
            t.setText("PASS");
            v.vibrate(400);
            isCorrect.add(false);
        }
        else{
            relativeLayout.setBackgroundColor(Color.GREEN);
            v.vibrate(750);
            t.setText("CORRECT");
            isCorrect.add(true);
            ++score;
        }
        b.animate().alphaBy(0).setDuration(1000).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                b.animate().setListener(null);
                //this ensures that the timer has the EXACT amount of time left
                time.setRepeatCount(timeLeft-1);//minus the time taken for starting the animation
                time.setStartDelay(500-playTime);//if the animation was paused in between, this makes up for the time needed to finish the animation
                relativeLayout.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.blue_background));
                if(arrayList.size()>0) {
                    int next = new Random().nextInt(arrayList.size());
                    t.setText(arrayList.get(next).toUpperCase());
                    wordOrder.add(arrayList.get(next).toUpperCase());
                    arrayList.remove(next);
                    sensorManager.registerListener(MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
                    time.start();
                }
                else{
                    t.setText("WOW, YOU'VE GUESSED EVERYTHING!");
                    v.vibrate(new long[]{250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250},-1);
                    b.animate().alphaBy(0).setDuration(4000).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            b.animate().setListener(null);
                            v.vibrate(new long[]{250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250},-1);
                            t.setText("LET'S SEE HOW MANY YOU GOT RIGHT");
                            b.animate().alphaBy(0).setDuration(4000).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    b.animate().setListener(null);
                                    time.removeAllListeners();
                                    endGame();
                                }
                            }).start();
                        }
                    }).start();

                }
            }
        }).start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onPause(){//stop the sensor when app is not running
        super.onPause();
        if(sensorManager!=null)
            sensorManager.unregisterListener(this);
    }
}
