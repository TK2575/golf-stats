package dev.tk2575.golfstats.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;

@Route("")
public class HomeView extends AppLayout {

	public HomeView() {
		Tabs menu = new Tabs();
		menu.setOrientation(Tabs.Orientation.HORIZONTAL);
		
	}
}
