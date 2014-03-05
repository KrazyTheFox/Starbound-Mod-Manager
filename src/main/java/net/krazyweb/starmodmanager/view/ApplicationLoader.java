package net.krazyweb.starmodmanager.view;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.concurrent.Task;
import net.krazyweb.starmodmanager.data.DatabaseFactory;
import net.krazyweb.starmodmanager.data.DatabaseModelInterface;
import net.krazyweb.starmodmanager.data.LocalizerFactory;
import net.krazyweb.starmodmanager.data.LocalizerModelInterface;
import net.krazyweb.starmodmanager.data.ModList;
import net.krazyweb.starmodmanager.data.Observable;
import net.krazyweb.starmodmanager.data.Observer;
import net.krazyweb.starmodmanager.data.SettingsFactory;
import net.krazyweb.starmodmanager.data.SettingsModelInterface;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApplicationLoader implements Observer {
	
	@SuppressWarnings("unused")
	private static final Logger log = LogManager.getLogger(ApplicationLoader.class);
	
	private static final double STEP_MULTIPLIER = 1.0 / 5.0;
	
	private LoaderView view;
	private ModList modList;
	
	private SettingsModelInterface settings;
	private DatabaseModelInterface database;
	private LocalizerModelInterface localizer;
	
	public ApplicationLoader() {
		
		view = new LoaderView();
		view.build();
		
		settings = new SettingsFactory().getInstance();
		settings.addObserver(this);
		
		configureLogger();
		
	}
	
	private void configureLogger() {
		
		Task<Void> task = settings.getInitializeLoggerTask();
		
		setProgressProperties(task.progressProperty(), task.messageProperty(), 1);
		
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.setName("Settings Initialization Thread");
		thread.start();
		
	}
	
	private void initDatabase() {
		
		database = new DatabaseFactory().getInstance();
		database.addObserver(this);
		
		Task<Void> task = database.getInitializerTask();
		
		setProgressProperties(task.progressProperty(), task.messageProperty(), 2);
		
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.setName("Database Initialization Thread");
		thread.start();
		
	}
	
	private void loadSettings() {
		
		Task<Void> task = settings.getLoadSettingsTask();
		
		setProgressProperties(task.progressProperty(), task.messageProperty(), 3);
		
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.setName("Settings Loading Thread");
		thread.start();
		
	}
	
	private void initializeLocalizer() {
		
		localizer = new LocalizerFactory().getInstance();
		localizer.addObserver(this);
		
		Task<Void> task = localizer.getInitializerTask();
		
		setProgressProperties(task.progressProperty(), task.messageProperty(), 4);
		
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.setName("Localizer Initialization Thread");
		thread.start();
		
	}
	
	private void loadModList() {
		
		modList = new ModList(new SettingsFactory(), new DatabaseFactory(), new LocalizerFactory());
		modList.addObserver(this);
		
		Task<Void> task = modList.getLoadTask();
		
		setProgressProperties(task.progressProperty(), task.messageProperty(), 5);
		
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.setName("Mod List Loading Thread");
		thread.start();
		
	}
	
	private void completeLoading() {
		
		settings.removeObserver(this);
		database.removeObserver(this);
		localizer.removeObserver(this);
		modList.removeObserver(this);
		
		view.close();
		
		new MainViewController(modList);
		
	}

	@Override
	public void update(final Observable observable, final Object data) {
		
		if (data instanceof String) {
			
			String message = (String) data;
			
			switch (message) {
				case "loggerconfigured":
					initDatabase();
					break;
				case "databaseinitialized":
					loadSettings();
					break;
				case "settingsloaded":
					initializeLocalizer();
					break;
				case "localizerloaded":
					loadModList();
					break;
				case "modlistupdated":
					completeLoading();
					break;
			}
			
		}
		
	}
	
	private void setProgressProperties(final ReadOnlyDoubleProperty progress, final ReadOnlyStringProperty message, final int step) {
		view.getProgressBar().bind(progress.multiply(STEP_MULTIPLIER).add((double) (step - 1) * STEP_MULTIPLIER), 1.0);
		view.getText().setText("Loading");
	}
	
}