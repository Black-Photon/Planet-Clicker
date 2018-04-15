package com.blackphoton.planetclicker.objectType.table.entries;

import com.blackphoton.planetclicker.core.Data;
import com.blackphoton.planetclicker.objectType.Era;
import com.blackphoton.planetclicker.objectType.RequiredResource;
import com.blackphoton.planetclicker.resources.ResourceType;
import com.blackphoton.planetclicker.resources.ResourceMaterial;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class ResourcesEntry extends TableEntry {
	private ResourceMaterial material;
	private boolean absolute;
	private ResourcesEntry multiplierEntry;
	private int initialValue;

	public ResourcesEntry(ResourceType type, String name, final int value, Era requiredEra, ArrayList<RequiredResource> resourcesNeeded, TableEntry upgradeTo, ResourceBundle resources) {
		super(type, name, value, requiredEra, upgradeTo, resourcesNeeded, null, resources);
		if(name==null) return;
		material = (ResourceMaterial) resources.getObject(resources.getKeys().nextElement());
		absolute = (Boolean) resources.getObject(resources.getKeys().nextElement());
		multiplierEntry = (ResourcesEntry) resources.getObject(resources.getKeys().nextElement());
		initialValue = value;

		if(absolute) {
			new Thread() {
				@Override
				public synchronized void run() {
					while (!Thread.currentThread().isInterrupted()) {
						if(multiplierEntry!=null){
							ResourcesEntry.super.setValue(initialValue*(multiplierEntry.getNumberOf()+1));
						}

						try {
							switch (material) {
								case WOOD:
									if (Data.main.getWoodCount() != ResourcesEntry.super.numberOf)
										ResourcesEntry.super.numberOf = Data.main.getWoodCount();
									break;
								case STONE:
									if (Data.main.getStoneCount() != ResourcesEntry.super.numberOf)
										ResourcesEntry.super.numberOf = Data.main.getStoneCount();
									break;
								case IRON:
									if (Data.main.getIronCount() != ResourcesEntry.super.numberOf)
										ResourcesEntry.super.numberOf = Data.main.getIronCount();
									break;
								case BRONZE:
									if (Data.main.getBronzeCount() != ResourcesEntry.super.numberOf)
										ResourcesEntry.super.numberOf = Data.main.getBronzeCount();
									break;
							}
							sleep(10);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				}
			}.start();
		}else {
			final ArrayList<RequiredResource> initialCost = new ArrayList<RequiredResource>();
			if(getResourcesNeeded()!=null) {
				for (int i = 0; i < getResourcesNeeded().size(); i++)
					initialCost.add(new RequiredResource(getResourcesNeeded().get(i).getMaterial(), getResourcesNeeded().get(i).getNumberRequired()));
			}

			new Thread() {
				@Override
				public void run() {
					while (!Thread.currentThread().isInterrupted()) {
						try {
							if(ResourcesEntry.this.resourcesNeeded!=null)
							for(int i=0; i<ResourcesEntry.this.resourcesNeeded.size(); i++){
								ResourcesEntry.this.resourcesNeeded.get(i).setNumberRequired(initialCost.get(i).getNumberRequired()*(numberOf+1));
							}
							sleep(100);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				}
			}.start();
		}
	}

	public ResourceMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ResourceMaterial material) {
		this.material = material;
	}

	public boolean isAbsolute() {
		return absolute;
	}

	public void setAbsolute(boolean absolute) {
		this.absolute = absolute;
	}

	public void setValueLabelText() {
		if(absolute)
			this.valueLabel.setText(Integer.toString(value));
		else
			this.valueLabel.setText("x"+Integer.toString(numberOf+1));
	}
}
