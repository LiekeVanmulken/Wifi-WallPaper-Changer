package models;

import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.Serializable;

/**
 * Created by wouter on 14-7-2016.
 */
public class WallpaperFile implements Serializable{

	private transient TreeItem parent;
	private File file;


	public void setParent(TreeItem parent) {
		this.parent = parent;
	}

	public TreeItem getParent() {

		return parent;

	}

	public File getFile() {
		return file;
	}

	public WallpaperFile(TreeItem parent, File file) {
		this.parent = parent;
		this.file = file;
	}
}
