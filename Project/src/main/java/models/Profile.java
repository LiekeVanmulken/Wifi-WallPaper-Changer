package models;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wouter on 14-7-2016.
 */
public class Profile implements Serializable{
	private String name;
	private List<WallpaperFile> wallpaperFiles;
	private List<String> wifiNames;

	public Profile(String name, List<WallpaperFile> wallpaperFiles, List<String> wifiNames) {
		this.name = name;
		this.wallpaperFiles = wallpaperFiles;
		this.wifiNames = wifiNames;
	}
	public Profile(String name) {
		this.name = name;
		this.wallpaperFiles = new LinkedList<>();
		this.wifiNames = new LinkedList<>();
	}
	public void addToWallpaperFiles(WallpaperFile wf) {
		wallpaperFiles.add(wf);
	}
	public void removeFromWallpaperFiles(WallpaperFile wf) {
		wallpaperFiles.remove(wf);
	}
	public List<WallpaperFile> getWallpaperFiles() {

		return wallpaperFiles;
	}

	public List<String> getWifiNames() {
		return wifiNames;
	}

	public String getName() {
		return name;
	}
}
