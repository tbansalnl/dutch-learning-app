package org.tbansal.dutch.learning.app.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import org.tbansal.dutch.learning.app.service.WordService;
import org.tbansal.dutch.learning.app.vaadin.components.WordEditorComponent;

import java.util.HashMap;
import java.util.Map;

@Route()
@CssImport(value = "./styles/views/main-view.css", themeFor = "vaadin-grid")
public class MainView extends VerticalLayout {
    private final WordService wordService;

    public MainView(final WordService wordService, final WordEditorComponent editor) {
        this.wordService = wordService;

        Tab tab1 = new Tab("Test");
        VerticalLayout page1 = new DutchExamComponent(wordService);

        Tab tab2 = new Tab("Admin");
        VerticalLayout page2 = new DutchWordAdminComponent(wordService, editor);
        page2.setVisible(false);


        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(tab1, page1);
        tabsToPages.put(tab2, page2);
        Tabs tabs = new Tabs(tab1, tab2);
        Div pages = new Div(page1, page2);
        pages.setWidthFull();
        tabs.setWidthFull();

        tabs.addSelectedChangeListener(event -> {
            tabsToPages.values().forEach(page -> page.setVisible(false));
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);
        });

        add(tabs, pages);
    }
}
