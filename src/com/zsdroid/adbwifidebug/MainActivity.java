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
import android.text.method.*;

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

		//判断是否root
		boolean root = IsRoot();
		if (!root)
		{
			ShowToast(getResources().getString(R.string.no_root_msg));
			button.setEnabled(false);
			return ;
		}
		//按钮事件
		button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					// TODO: Imtplement this method
					if (status == false)
					{
						button.setText(getResources().getString(R.string.button_status_close));
						StartAdbWifiDebug();
						status = true;
					}
					else
					{
						button.setText(getResources().getString(R.string.button_status_open));
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
		if (status == true)
		{
			Command("stop adbd");
			ShowToast(getResources().getString(R.string.debug_status_close));
		}
	}
	//启动
	public void StartAdbWifiDebug()
	{
		// TODO: Implement this method
		if (status == false)
		{
			String ip = GetIP();
			if (ip.equals(""))
			{
				ShowToast(getResources().getString(R.string.no_connect_wifi));
				return ;
			}
			//执行开启命令
			Command("setprop service.adb.tcp.port 5555;start adbd");
			ShowToast(getResources().getString(R.string.debug_status_open) + " " + ip);
		}
	}
	//执行命令
	public void Command(String cmd)
	{
		try
		{
			Process p = Runtime.getRuntime().exec("su");
			OutputStream os = p.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write(cmd + "\n");
			bw.write("exit \n");
			bw.close();
			osw.close();
			os.close();
		}
		catch (IOException e)
		{
		}
	}
	/**
	 * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
	 * 
	 * @return 应用程序是/否获取Root权限
	 */
	private boolean IsRoot()
	{
		Process process = null;
		try
		{
			process = Runtime.getRuntime().exec("su");
			process.getOutputStream().write("exit\n".getBytes());
			process.getOutputStream().flush();
			int i = process.waitFor();
			if (0 == i)
			{
				process = Runtime.getRuntime().exec("su");
				return true;
			}
		}
		catch (Exception e)
		{
			return false;
		}
		return false;
	}
	//获取ip
	public String GetIP()
	{
		// TODO: Implement this method
		WifiManager wm = (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
		if (!wm.isWifiEnabled())
		{
			wm.setWifiEnabled(true);
		}
		WifiInfo wi = wm.getConnectionInfo();
		int ip = wi.getIpAddress();
		if (ip == 0)
		{
			return "";
		}
		return IntToIp(ip);
	}
	//int转ip
	private String IntToIp(int i) 
	{
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF) ;
	}
	//显示
	public void ShowToast(String toast)
	{
		if (!toast.equals(""))
		{
			edittext.setText(edittext.getText() + "\n============\n>> " + toast);

			edittext.setSelection(edittext.getText().length(), edittext.getText().length());
		}
	}
	public void ShowToast(int toast)
	{
		ShowToast(toast + "");
	}
}
