package com.zsdroid.adbwifidebug;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.view.View.*;
import android.net.*;
import android.net.wifi.*;
import java.io.*;
import java.lang.Process;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
	boolean status = false;
	Button button;
	EditText edittext;
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		//初始化控件
		button = (Button)findViewById(R.id.mainButton1);
		edittext = (EditText)findViewById(R.id.mainEditText1);
		edittext.setCursorVisible(false);
		edittext.setFocusable(false);
		edittext.setFocusableInTouchMode(false);
		//按钮事件
		button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					// TODO: Imtplement this method
					if(status == false)
					{
						button.setText("关闭");
						StartAdbWifiDebug();
						status = true;
					}
					else
					{
						button.setText("开启");
						StopAdbWifiDebug();
						status = false;
					}
				}
		});
    }
	//停止
	public void StopAdbWifiDebug()
	{
		// TODO: Implement this method
		ShowToast("stop");
		if(status == true)
		{
			
		}
	}
	//启动
	public void StartAdbWifiDebug()
	{
		// TODO: Implement this method
		ShowToast("start");
		if(status == false)
		{
			String ip = GetIP();
			ShowToast(ip);
			//执行开启命令
			Cmd("id;su;id;");
		}
	}
	//执行命令
	public void Cmd(String cmd)
	{
		Process pro;
		try
		{
			 pro = Runtime.getRuntime().exec(cmd);
			 InputStream is = pro.getInputStream();
			 InputStreamReader isr = new InputStreamReader(is);
			 BufferedReader br = new BufferedReader(isr);
			 String line = "";
			 StringBuilder sb = new StringBuilder(line);
			 while((line = br.readLine()) != null)
			 {
				 sb.append(line);
				 sb.append("\n");
			 }
			 String cmdtext = sb.toString();
			 ShowToast(cmdtext);
		}
		catch (IOException e)
		{}
	}
	//获取ip
	public String GetIP()
	{
		// TODO: Implement this method
		WifiManager wm = (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
		if(!wm.isWifiEnabled())
			wm.setWifiEnabled(true);
		WifiInfo wi = wm.getConnectionInfo();
		int ip = wi.getIpAddress();
		return IntToIp(ip);
	}
	//int转ip
	private String IntToIp(int i) 
	{
		return (i & 0xFF ) + "." + ((i >> 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) + "." + ( i >> 24 & 0xFF) ;
	}
	//显示日志
	public void ShowToast(String toast)
	{
		edittext.setText(edittext.getText()+"\n============\n>> "+toast);
		//Toast.makeText(getApplicationContext(),toast,Toast.LENGTH_SHORT);
	}
	public void ShowToast(int toast)
	{
		ShowToast(toast+"");
	}
}
