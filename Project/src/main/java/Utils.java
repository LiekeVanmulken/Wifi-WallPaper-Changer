import com.sun.jna.Library;
import com.sun.jna.Native;
//import com.sun.jna.platform.win32.WinDef.HWND;
//import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.win32.W32APIOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wouter on 10-7-2016.
 */
public class Utils {

	//source : https://stackoverflow.com/questions/4750372/can-i-change-my-windows-desktop-wallpaper-programmatically-in-java-groovy
	public static interface User32 extends Library {
		User32 INSTANCE = (User32) Native.loadLibrary("user32",User32.class,W32APIOptions.DEFAULT_OPTIONS);
		boolean SystemParametersInfo (int one, int two, String s ,int three);
	}
	public static void main(String[] args) {
//		User32.INSTANCE.SystemParametersInfo(0x0014, 0, "C:\\Users\\Public\\Pictures\\Sample Pictures\\Chrysanthemum.jpg" , 1);
		User32.INSTANCE.SystemParametersInfo(0x0014, 0, "D:\\background.jpg" , 1);
	}
	public static boolean setBackground(String filePath) {
//		User32.INSTANCE.SystemParametersInfo(0x0014, 0, "C:\\Users\\Public\\Pictures\\Sample Pictures\\Chrysanthemum.jpg" , 1);
		if(new File(filePath).exists()) {
			User32.INSTANCE.SystemParametersInfo(0x0014, 0, filePath, 1);
			return true;
		}else {
			return false;
		}
	}
	//source : https://stackoverflow.com/questions/5378103/finding-ssid-of-a-wireless-network-with-java/30297032#30297032
	public static List<String> getSSIDS() throws IOException {
		ArrayList<String> ssids=new ArrayList<String>();
		ArrayList<String>signals=new ArrayList<String>();
		ProcessBuilder builder = new ProcessBuilder(
				"cmd.exe", "/c", "netsh wlan show all");
		builder.redirectErrorStream(true);
		Process p = builder.start();
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while (r.read()!=-1) {
			line = r.readLine();
			if (line.contains("SSID")||line.contains("Signal")){
				if(!line.contains("BSSID"))
					if(line.contains("SSID")&&!line.contains("name")&&!line.contains("SSIDs"))
					{
						line=line.substring(8);
						ssids.add(line);

					}
				if(line.contains("Signal"))
				{
					line=line.substring(30);
					signals.add(line);

				}

				if(signals.size()==7)
				{
					break;
				}

			}

		}
		for (int i=0;i<ssids.size();i++)
		{
//			System.out.println(ssids.get(i).substring(ssids.get(i).indexOf(": ")+2));
			ssids.set(i,ssids.get(i).substring(ssids.get(i).indexOf(": ")+2));
//			System.out.println(ssids.get(i));
//			System.out.println("SSID name == "+ssids.get(i)+"   and its signal == "+signals.get(i)  );
		}
		return ssids;
	}

}