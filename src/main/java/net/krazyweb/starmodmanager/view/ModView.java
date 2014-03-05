package net.krazyweb.starmodmanager.view;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import net.krazyweb.starmodmanager.data.Localizer;
import net.krazyweb.starmodmanager.data.LocalizerFactory;
import net.krazyweb.starmodmanager.data.LocalizerModelInterface;
import net.krazyweb.starmodmanager.data.Mod;
import net.krazyweb.starmodmanager.data.ModList;
import net.krazyweb.starmodmanager.data.Observable;
import net.krazyweb.starmodmanager.data.Observer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModView implements Observer {
	
	@SuppressWarnings("unused")
	private static final Logger log = LogManager.getLogger(ModView.class);
	
	private GridPane collapsedRoot;
	
	private Button collapsedInstallButton;
	private Button collapsedExpandButton;
	private Text collapsedStatusText;
	private Text collapsedDisplayName;
	private Text collapsedModVersion;
	
	private HBox collapsedButtons;
	private Button collapsedDeleteButton;
	private Button collapsedHideButton;
	private Button collapsedLinkButton;
	
	private GridPane expandedRoot;
	private BorderPane expandedHeader;
	
	private Button expandedInstallButton;
	private Button expandedExpandButton;
	private Text expandedStatusText;
	private Text expandedDisplayName;
	private Text expandedModVersion;
	private Text expandedAuthor;
	private Text expandedDescription;
	//private ImageView expandedImage;

	private VBox expandedButtons;
	private Button expandedDeleteButton;
	private Button expandedHideButton;
	private Button expandedLinkButton;

	private Mod mod;
	private ModViewController controller;
	protected boolean moving;
	protected boolean showingMoreInfo;
	protected boolean expanded;
	
	private LocalizerModelInterface localizer;
	
	protected ModView(final Mod mod, final ModList modList) {
		this.mod = mod;
		this.mod.addObserver(this);
		localizer = new LocalizerFactory().getInstance();
		localizer.addObserver(this);
		controller = new ModViewController(this, modList, localizer);
	}
	
	protected void build(final boolean expanded) {

		this.expanded = expanded;
		
		buildExpanded();
		buildUnexpanded();
		
		createListeners();
		updateStrings();
		updateColors();
		updateImages();
		
	}
	
	private void buildExpanded() {
		
		expandedRoot = new GridPane();
		expandedHeader = new BorderPane();

		expandedInstallButton = new Button();
		expandedExpandButton = new Button();
		expandedStatusText = new Text();
		expandedDisplayName = new Text();
		expandedModVersion = new Text();
		expandedAuthor = new Text();
		expandedDescription = new Text();
		//expandedImage = new ImageView();
		
		expandedButtons = new VBox();
		expandedDeleteButton = new Button();
		expandedHideButton = new Button();
		expandedLinkButton = new Button();
		
		expandedRoot.setGridLinesVisible(true);
		//expandedRoot.setHgap(25.0);
		
		expandedHeader.setLeft(expandedStatusText);
		expandedHeader.setRight(expandedModVersion);
		
		expandedButtons.getChildren().addAll(
			expandedDeleteButton,
			expandedHideButton,
			expandedLinkButton
		);
		
		expandedRoot.add(expandedHeader, 1, 1);
		expandedRoot.add(expandedDisplayName, 1, 2);
		expandedRoot.add(expandedAuthor, 1, 3);
		expandedRoot.add(expandedInstallButton, 2, 2);
		expandedRoot.add(expandedExpandButton, 3, 2);
		
		GridPane.setColumnSpan(expandedHeader, 3);
		
		GridPane.setHgrow(expandedDisplayName, Priority.ALWAYS);
		
	}
	
	private void buildUnexpanded() {
		
		collapsedRoot = new GridPane();
		//collapsedRoot.setGridLinesVisible(true);
		collapsedRoot.setId("modview-container");
		collapsedRoot.setMinHeight(44.0);
		collapsedRoot.setPadding(new Insets(7, 21, 6, 16));
		
		collapsedDisplayName = new Text();
		collapsedDisplayName.setId("modview-title");
		
		collapsedStatusText = new Text();
		collapsedStatusText.setId("modview-small-info");
		
		collapsedModVersion = new Text();
		collapsedModVersion.setId("modview-small-info");
		
		collapsedInstallButton = new Button();
		collapsedInstallButton.setId("modview-install-button");
		collapsedInstallButton.setFocusTraversable(false);
		collapsedInstallButton.setPrefHeight(30.0);
		collapsedInstallButton.setPrefWidth(73.0);
		collapsedInstallButton.setAlignment(Pos.CENTER);
		
		collapsedExpandButton = new Button();
		collapsedExpandButton.setId("modview-expand-button");
		
		collapsedButtons = new HBox();
		collapsedDeleteButton = new Button();
		
		collapsedHideButton = new Button();
		collapsedLinkButton = new Button();
		
		collapsedButtons.getChildren().addAll(
			collapsedDeleteButton,
			collapsedHideButton,
			collapsedLinkButton
		);
		
		collapsedRoot.add(collapsedDisplayName, 1, 1);
		collapsedRoot.add(collapsedStatusText, 2, 1);
		collapsedRoot.add(collapsedModVersion, 2, 2);
		collapsedRoot.add(collapsedInstallButton, 3, 1);
		collapsedRoot.add(collapsedExpandButton, 4, 1);

		GridPane.setRowSpan(collapsedDisplayName, 2);
		GridPane.setRowSpan(collapsedInstallButton, 2);
		GridPane.setRowSpan(collapsedButtons, 2);
		GridPane.setColumnSpan(collapsedButtons, 2);
		GridPane.setRowSpan(collapsedExpandButton, 2);
		GridPane.setHgrow(collapsedDisplayName, Priority.ALWAYS);
		GridPane.setVgrow(collapsedDisplayName, Priority.ALWAYS);
		
		GridPane.setHalignment(collapsedStatusText, HPos.RIGHT);
		GridPane.setHalignment(collapsedModVersion, HPos.RIGHT);
		GridPane.setMargin(collapsedStatusText, new Insets(0, 9, 0, 0));
		GridPane.setMargin(collapsedModVersion, new Insets(1, 9, 0, 0));
		GridPane.setMargin(collapsedInstallButton, new Insets(0, 21, 0, 0));
		
	}
	
	protected void createListeners() {
		
		collapsedInstallButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				if (mod.isInstalled()) {
					controller.uninstallButtonClicked();
				} else {
					controller.installButtonClicked();
				}
			}
		});
		
		collapsedExpandButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				controller.moreInfoButtonClicked();
			}
		});

		collapsedDeleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				controller.deleteButtonClicked();
			}
		});
		
		collapsedHideButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				controller.hideButtonClicked();
			}
		});
		
		collapsedLinkButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				controller.linkButtonClicked();
			}
		});
		
		expandedInstallButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				if (mod.isInstalled()) {
					controller.uninstallButtonClicked();
				} else {
					controller.installButtonClicked();
				}
			}
		});
		
		expandedExpandButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				controller.moreInfoButtonClicked();
			}
		});

		expandedDeleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				controller.deleteButtonClicked();
			}
		});
		
		expandedHideButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				controller.hideButtonClicked();
			}
		});
		
		expandedLinkButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				controller.linkButtonClicked();
			}
		});
		
	}
	
	protected Mod getMod() {
		return mod;
	}

	private void updateStrings() {

		expandedInstallButton.setText(localizer.getMessage(mod.isInstalled() ? "modview.uninstall" : "modview.install"));
		expandedStatusText.setText(localizer.getMessage(mod.isInstalled() ? "modview.installed" : "modview.notinstalled"));
		expandedDisplayName.setText(mod.getDisplayName());
		expandedModVersion.setText(mod.getModVersion());
		expandedAuthor.setText(mod.getAuthor());
		expandedDescription.setText(mod.getDescription());
		
		//TODO Replace with images
		expandedExpandButton.setText("^");
		expandedDeleteButton.setText("DEL");
		expandedHideButton.setText("HID");
		expandedLinkButton.setText("LNK");
		
		collapsedInstallButton.setText(localizer.getMessage(mod.isInstalled() ? "modview.uninstall" : "modview.install"));
		collapsedStatusText.setText(localizer.getMessage(mod.isInstalled() ? "modview.installed" : "modview.notinstalled").toUpperCase());
		collapsedDisplayName.setText(mod.getDisplayName());
		collapsedModVersion.setText(mod.getModVersion().toUpperCase());
		
		//TODO Replace with images
		collapsedDeleteButton.setText("DEL");
		collapsedHideButton.setText("HID");
		collapsedLinkButton.setText("LNK");
		
	}
	
	private void updateImages() {

		if (showingMoreInfo) {
			collapsedExpandButton.setGraphic(new ImageView(new Image(ModView.class.getClassLoader().getResourceAsStream("collapse-arrow.png"))));
		} else {
			collapsedExpandButton.setGraphic(new ImageView(new Image(ModView.class.getClassLoader().getResourceAsStream("expand-arrow.png"))));
		}
		
	}
	
	protected GridPane getContent() {
		return expanded ? expandedRoot : collapsedRoot;
	}
	
	protected void toggleMoreInfo() {
		
		if (!showingMoreInfo) {
			
			collapsedRoot.getChildren().remove(collapsedInstallButton);
			collapsedRoot.getChildren().remove(collapsedModVersion);
			collapsedRoot.getChildren().remove(collapsedStatusText);
			
			collapsedRoot.add(collapsedButtons, 2, 1);

			expandedRoot.getChildren().remove(expandedExpandButton);
			expandedRoot.add(expandedButtons, 3, 4);
			expandedRoot.add(expandedExpandButton, 3, 5);
			expandedRoot.add(expandedDescription, 1, 4);
			
			showingMoreInfo = true;
			
		} else {

			collapsedRoot.getChildren().remove(collapsedButtons);

			collapsedRoot.add(collapsedStatusText, 2, 1);
			collapsedRoot.add(collapsedModVersion, 2, 2);
			collapsedRoot.add(collapsedInstallButton, 3, 1);

			expandedRoot.getChildren().remove(expandedExpandButton);
			expandedRoot.getChildren().remove(expandedButtons);
			expandedRoot.getChildren().remove(expandedDescription);
			expandedRoot.add(expandedExpandButton, 3, 2);
			
			showingMoreInfo = false;
			
		}
		
		updateImages();
		
	}
	
	public void expand(final boolean expand) {
		expanded = expand;
	}
	
	private void updateColors() {

		collapsedRoot.getStyleClass().clear();
		collapsedDisplayName.getStyleClass().clear();
		collapsedStatusText.getStyleClass().clear();
		collapsedModVersion.getStyleClass().clear();
		
		collapsedInstallButton.getStyleClass().remove("modview-installed-button-color");
		collapsedInstallButton.getStyleClass().remove("modview-not-installed-button-color");
		
		if (mod.isInstalled()) {
			
			collapsedRoot.getStyleClass().add("modview-installed");
			collapsedDisplayName.getStyleClass().add("modview-installed-text-color");
			collapsedStatusText.getStyleClass().add("modview-installed-text-color");
			collapsedModVersion.getStyleClass().add("modview-installed-text-color");
			collapsedInstallButton.getStyleClass().add("modview-installed-button-color");
			
		} else {
			
			collapsedRoot.getStyleClass().add("modview-not-installed");
			collapsedDisplayName.getStyleClass().add("modview-not-installed-text-color");
			collapsedStatusText.getStyleClass().add("modview-not-installed-text-color");
			collapsedModVersion.getStyleClass().add("modview-not-installed-text-color");
			collapsedInstallButton.getStyleClass().add("modview-not-installed-button-color");
			
		}
		
	}
	
	@Override
	public void update(final Observable observable, final Object message) {
		
		if (observable instanceof Localizer && message.equals("localechanged")) {
			updateStrings();
		} else if (observable == mod) {
			
			String msg = (String) message;
			
			switch (msg) {
				case "installstatuschanged":
					updateImages();
					updateColors();
					updateStrings();
					break;
			}
			
		}
		
	}

	
}