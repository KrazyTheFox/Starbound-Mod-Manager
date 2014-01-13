package net.krazyweb.starmodmanager;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class ModList {
	
	private boolean locked;
	
	private ArrayList<Mod> mods;
	
	public ModList() {
		//TODO Get locked status from settings.
		System.out.println("Mod list created.");
		try {
			mods = Database.getModList();
			
			for (Mod mod : mods) {
				mod.setOrder(mods.indexOf(mod));
				Database.updateMod(mod);
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addMod(final File file) {
		
		Mod mod = Mod.load(file, mods.size());
		
		if (mod != null) {
			mods.add(mod);
		}
		
	}
	
	public void deleteMod(final String name) {
		
		Mod mod = getModByName(name);
		
		if (mod == null) {
			return;
		}
		
	}
	
	public void installMod(final String name) {
		
		Mod mod = getModByName(name);
		
		if (mod == null) {
			return;
		}
		
		
		
	}
	
	public void uninstallMod(final String name) {
		
		Mod mod = getModByName(name);
		
		if (mod == null) {
			return;
		}
		
		
		
	}
	
	public void hideMod(final String name) {
		
		Mod mod = getModByName(name);
		
		if (mod == null) {
			return;
		}
		
		mod.setHidden(true);
		
	}
	
	/*
	 * See: http://stackoverflow.com/questions/4938626/moving-items-around-in-an-arraylist
	 */
	public void moveMod(final String name, final int amount) {
		
		if (locked) {
			System.out.println("Mod list locked; cannot move mod: " + name);
			return;
		}
		
		Mod mod = getModByName(name);
		
		if (mod == null) {
			System.out.println("Mod '" + name + "' not found.");
			return;
		}
		
		System.out.println("=============\nPerforming rotation, results:");
		
		if (amount > 0) {
			
			if (mods.indexOf(mod) - amount > 0) {
				Collections.rotate(mods.subList(mods.indexOf(mod) - amount, mods.indexOf(mod) + 1), 1);
			} else {
				Collections.rotate(mods.subList(0, mods.indexOf(mod) + 1), 1);
			}
			
		} else {
			
			if (mods.indexOf(mod) - amount + 1 <= mods.size()) {
				Collections.rotate(mods.subList(mods.indexOf(mod), mods.indexOf(mod) - amount + 1), -1);
			} else {
				Collections.rotate(mods.subList(mods.indexOf(mod), mods.size()), -1);
			}
			
		}
		
		for (Mod m : mods) {
			m.setOrder(mods.indexOf(m));
			System.out.println("[" + m.getOrder() + "] \t" + m.getInternalName());
			try {
				Database.updateMod(m);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void lockList() {
		locked = true;
	}
	
	public void unlockList() {
		locked = false;
	}
	
	public void refreshMods() {
		try {
			Database.getModList();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private Mod getModByName(final String name) {
		
		for (Mod mod : mods) {
			if (mod.getInternalName().equals(name)) {
				return mod;
			}
		}
		
		return null;
		
	}
	
}