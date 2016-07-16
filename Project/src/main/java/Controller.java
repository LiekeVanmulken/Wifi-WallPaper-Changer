/**
 * Created by wouter on 10-7-2016.
 */

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import models.Profile;
import models.WallpaperFile;

import java.io.*;
import java.util.*;

/**
 * Created by wouter on 26-3-2016.
 */
public class Controller {

	private final TextField txtName;

	//	private final TextField txtLastName;
	private final Button addButton;
	private final Button deleteButton;
	private final Button addCurrentWifiButton;
	private final Button tableAddBtn;
	private final Button tableDeleteBtn;

	TreeView<String> treeview = new TreeView<>();
	TableView<WallpaperFile> tableView = new TableView<>();

	List<WallpaperFile> wallpaperFiles = new LinkedList<>();
	Timer t = new Timer();

	boolean a = true;
	Stage primaryStage;
	BorderPane border;

	public Controller() {

		t.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					//clusterfuck mutherfuckaaaaa Todo : fix this shit
					for (String networkSSID : Utils.getSSIDS()) {
						for (TreeItem profileItem : treeview.getRoot().getChildren()) {
							for (Object wifiItem : profileItem.getChildren()) {

								if (networkSSID.toLowerCase().contains(((TreeItem)wifiItem).getValue().toString().toLowerCase())) {
									List<WallpaperFile> files = new ArrayList<>();
									for (WallpaperFile wf : wallpaperFiles) {
										if (wf.getParent() == profileItem) {
											files.add(wf);
										}
									}
									if (files.size() > 0) {
										Utils.setBackground(files.get(0).getFile().getPath());
									}
								}
							}
						}
					}


				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}, 0, 1000);
		primaryStage = new Stage();

		TreeItem<String> rootItem = new TreeItem<String>("Profiles");
		rootItem.setExpanded(true);
		HBox topBox = new HBox();
		VBox treeBox = new VBox();
		VBox tableBox = new VBox();


		treeview = new TreeView<String>(rootItem);
		treeview.getSelectionModel().selectedItemProperty().addListener(new javafx.beans.value.ChangeListener<TreeItem<String>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {
				//set table
				tableView.getItems().clear();
				for (WallpaperFile wf : wallpaperFiles) {
					if (wf.getParent() == newValue || wf.getParent() == newValue.getParent()) {
						tableView.getItems().add(wf);
					}
				}
				boolean shouldBeVisible = true;
				//root selected
				if(newValue==rootItem){
					txtName.setPromptText("Profile");
				}
				//profile selected
				else if(newValue.getParent()==rootItem){
					txtName.setPromptText("Wifi name");
					shouldBeVisible = false;
				}
				//wifi selected
				else if(newValue.getParent()!=null && newValue.getParent().getParent()==rootItem){
					txtName.setPromptText("Wifi name");
					shouldBeVisible = true;
				}
				addButton.setDisable(shouldBeVisible);
				addCurrentWifiButton.setDisable(shouldBeVisible);

			}
		});
		treeview.setEditable(true);
		treeview.setCellFactory(new Callback<TreeView<String>,TreeCell<String>>(){
			@Override
			public TreeCell<String> call(TreeView<String> p) {
				return new TextFieldTreeCellImpl();
			}
		});


		tableView = new TableView<>();
		TableColumn fileNameCol = new TableColumn("File name");
		fileNameCol.setMinWidth(100);
		fileNameCol.setCellValueFactory(
				new PropertyValueFactory<WallpaperFile, String>("file"));


		tableView.getColumns().add(fileNameCol);

		StackPane root = new StackPane();
		root.getChildren().add(topBox);
		topBox.getChildren().add(treeBox);

		topBox.getChildren().add(tableBox);
		tableBox.getChildren().add(tableView);

		treeBox.getChildren().add(treeview);

		HBox buttonBox = new HBox();
		buttonBox.setPadding(new Insets(10, 10, 10, 10));
		buttonBox.setSpacing(10);

		txtName = new TextField();
		txtName.setPromptText("Profiles");
		txtName.setMinWidth(100);

		addButton = new Button("Add");
		addButton.setOnAction(e -> addButtonClicked());

		deleteButton = new Button("Delete");
		deleteButton.setOnAction(e -> deleteButtonClicked());

		addCurrentWifiButton = new Button("Add current Network");
		addCurrentWifiButton.setOnAction(e -> addCurrentWifiButtonClicked());

		buttonBox.getChildren().addAll(txtName, addButton, addCurrentWifiButton, deleteButton);
		treeBox.getChildren().add(buttonBox);


		HBox tableButtonBox = new HBox();
		tableButtonBox.setPadding(new Insets(10, 10, 10, 10));
		tableButtonBox.setSpacing(10);

		tableAddBtn = new Button("Add");
		tableAddBtn.setOnAction(e -> tableAddButtonClicked());

		tableDeleteBtn = new Button("Delete");
		tableDeleteBtn.setOnAction(e -> tableDeleteButtonClicked());

		Button saveBtn= new Button("Save");
		saveBtn.setOnAction(e -> save());

		Button loadBtn= new Button("Load");
		loadBtn.setOnAction(e -> load());

		tableButtonBox.getChildren().addAll(tableAddBtn, tableDeleteBtn,saveBtn,loadBtn);
		tableBox.getChildren().add(tableButtonBox);


		load();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				save();
				System.out.println("saved");
			}
		});
		primaryStage.setResizable(false);
		primaryStage.setTitle("Wallpaperchanger");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	private void addButtonClicked() {
		TreeItem c = (TreeItem) treeview.getSelectionModel().getSelectedItem();
		TreeItem itemToAdd = new TreeItem<>(txtName.getText());
		c.getChildren().add(itemToAdd);
		c.setExpanded(true);
	}

	private void addCurrentWifiButtonClicked() {
		TreeItem selectedItem = treeview.getSelectionModel().getSelectedItem();
		try {
			for (String s : Utils.getSSIDS()) {
				selectedItem.getChildren().add(new TreeItem<>(s));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void changeButtonClicked() {
		TreeItem c = (TreeItem) treeview.getSelectionModel().getSelectedItem();
		c.setValue(txtName.getText());
	}

	private void deleteButtonClicked() {
		TreeItem c = (TreeItem) treeview.getSelectionModel().getSelectedItem();
		c.getParent().getChildren().remove(c);
	}

	private void tableAddButtonClicked() {
		FileChooser fc = new FileChooser();
		List<File> files = fc.showOpenMultipleDialog(primaryStage);
		for (File f : files) {
			WallpaperFile wf = new WallpaperFile(treeview.getSelectionModel().getSelectedItem(), f);
			wallpaperFiles.add(wf);
			tableView.getItems().add(wf);
		}
	}

	private void tableDeleteButtonClicked() {
		WallpaperFile c = (WallpaperFile) tableView.getSelectionModel().getSelectedItem();
		wallpaperFiles.remove(c);
		boolean remove = tableView.getItems().remove(c);
	}

	private void save(){

		try
		{
			FileOutputStream fileOut =
					new FileOutputStream("savefile");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
//			out.writeObject(treeview.getRoot());
//			out.writeObject(wallpaperFiles);

			TreeItem root = treeview.getRoot();
			List<Profile> profiles = new LinkedList<>();
			for(Object profile: root.getChildren()){

				List<WallpaperFile> files = new LinkedList<>();
				for(WallpaperFile wf: wallpaperFiles){
					if(wf.getParent()==profile){files.add(wf);}
				}
				List<String> ssids = new LinkedList<>();
				for(Object ssid : ((TreeItem) profile).getChildren()){
					ssids.add(((TreeItem) ssid).getValue().toString());
				}
				profiles.add(new Profile(((TreeItem) profile).getValue().toString(),files, ssids));

			}
			out.writeObject(profiles);
			out.close();
			fileOut.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void load(){
		System.out.println("loaded");
		if(!new File("savefile").exists()){
			return;
		}
		try
		{
			File f = new File("savefile");
			FileInputStream fileIn = new FileInputStream(f);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			List<Profile> profiles = (List<Profile>)in.readObject();

			List<WallpaperFile> wallpaperFiles = new LinkedList<>();

			TreeItem root = treeview.getRoot();
			root.getChildren().clear();

			for(Profile profile:profiles){

				TreeItem profileItem = new TreeItem(profile.getName());
				for (String ssid :profile.getWifiNames()){
					profileItem.getChildren().add(new TreeItem<>(ssid));
				}

				for(WallpaperFile wf : profile.getWallpaperFiles()){
					wf.setParent(profileItem);
				}
				wallpaperFiles.addAll(profile.getWallpaperFiles());


				root.getChildren().add(profileItem);
			}
			this.wallpaperFiles = wallpaperFiles;
//			TreeItem a = (TreeItem) in.readObject();
//			treeview.setRoot(a);
//			List<WallpaperFile> savedWallpaperFiles = (List<WallpaperFile>) in.readObject();
//			this.wallpaperFiles = savedWallpaperFiles;
			in.close();
			fileIn.close();
		}catch(Exception e)
		{e.printStackTrace();}
	}
}


final class TextFieldTreeCellImpl extends TreeCell<String> {

	private TextField textField;

	public TextFieldTreeCellImpl() {
	}

	@Override
	public void startEdit() {
		super.startEdit();

		if (textField == null) {
			createTextField();
		}
		setText(null);
		setGraphic(textField);
		textField.selectAll();
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setText((String) getItem());
		setGraphic(getTreeItem().getGraphic());
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (textField != null) {
					textField.setText(getString());
				}
				setText(null);
				setGraphic(textField);
			} else {
				setText(getString());
				setGraphic(getTreeItem().getGraphic());
			}
		}
	}

	private void createTextField() {
		textField = new TextField(getString());
		textField.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent t) {
				if (t.getCode() == KeyCode.ENTER) {
					commitEdit(textField.getText());
				} else if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			}
		});
	}

	private String getString() {
		return getItem() == null ? "" : getItem().toString();
	}
}
