package com.shinsw.seungwon.mute;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {//설정창 액티비티

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle("Settings");

        final SeekBar clusterbar=(SeekBar)findViewById(R.id.c_clusterbar);
        clusterbar.setMax(11);
        SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
        clusterbar.setProgress(pref.getInt("cluster", 1) - 1);

        clusterbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int temp = clusterbar.getProgress();
                if (temp == 0)
                    temp = 1;
                ApplicationClass.user.clusternumber = temp;
                SharedPreferences shared = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putInt("cluster", clusterbar.getProgress() + 1);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        TextView v=(TextView)findViewById(R.id.setting_duptext);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingActivity.this, DeleteActivity.class);
                startActivity(i);
            }
        });
        SeekBar s=(SeekBar)findViewById(R.id.setting_seekbar);
        s.setMax(4);

        s.setProgress(ApplicationClass.user.mode-1);
        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser)
                    return;

                ApplicationClass.user.mode = progress + 1;
                SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("mode", ApplicationClass.user.mode);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final CheckBox c=(CheckBox)findViewById(R.id.setting_motion);

        if(ApplicationClass.user.usehand==0)
            c.setChecked(false);
        else
            c.setChecked(true);

        c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ApplicationClass.user.usehand=1;
                }else{
                    ApplicationClass.user.usehand=0;
                }
                SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("usehand", ApplicationClass.user.usehand);
                editor.commit();
            }
        });

        TextView v2=(TextView)findViewById(R.id.setting_nettext);
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SettingActivity.this,CoreService.class);
                i.setAction(Actions.command.network);
                startService(i);
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        registerReceiver(b,new IntentFilter(Actions.command.networktoast));

        super.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(b);
    }

    BroadcastReceiver b=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(SettingActivity.this,CoreService.results+" Songs Updated",Toast.LENGTH_SHORT).show();
            Toast.makeText(SettingActivity.this,intent.getExtras().getString("tag"),Toast.LENGTH_SHORT).show();
        }
    };
}
