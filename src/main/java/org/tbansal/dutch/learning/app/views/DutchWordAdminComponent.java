package org.tbansal.dutch.learning.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.util.StringUtils;
import org.tbansal.dutch.learning.app.entities.DutchWords;
import org.tbansal.dutch.learning.app.service.WordService;
import org.tbansal.dutch.learning.app.vaadin.components.WordEditorComponent;
import org.vaadin.klaudeta.PaginatedGrid;

public class DutchWordAdminComponent extends VerticalLayout {

    private final WordService wordService;

    private final Grid<DutchWords> grid;
    private final WordEditorComponent editor;

    private final TextField filterTextBox;
    private final Button addNewButton;
    ;

    public DutchWordAdminComponent(final WordService wordService, final WordEditorComponent editor) {
        this.wordService = wordService;
        this.editor = editor;
        grid = initGrid();
        filterTextBox = initFilter();
        addNewButton = initAddButton();
        setId("word-editor-view");


        HorizontalLayout actions = new HorizontalLayout(filterTextBox, addNewButton);
        add(actions, grid, editor);

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            refreshGrid(filterTextBox.getValue());
        });


        refreshGrid(null);
    }

    private Button initAddButton() {
        final Button addNewButton = new Button("New Word", VaadinIcon.PLUS.create());
        addNewButton.addClickListener(e -> editor.editWord(new DutchWords()));
        return addNewButton;
    }


    private TextField initFilter() {
        final TextField filterTextBox = new TextField();
        filterTextBox.setPlaceholder("Filter by name...");
        filterTextBox.setClearButtonVisible(true);
        filterTextBox.setValueChangeMode(ValueChangeMode.EAGER);
        filterTextBox.addValueChangeListener(e -> refreshGrid(e.getValue()));
        return filterTextBox;
    }


    public void refreshGrid(String filterText) {
        if (!StringUtils.isEmpty(filterText)) {
            grid.setItems(wordService.search(filterText));
        } else {
            grid.setItems(wordService.findAll());
        }
    }

    private Grid<DutchWords> initGrid() {
        final PaginatedGrid<DutchWords> grid = new PaginatedGrid<>(DutchWords.class);
        grid.setColumns("id", "dutchWord", "englishTranslation", "example", "failedAttempts", "type");
        grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);
        grid.setPageSize(5);
        grid.setPaginatorSize(5);
        grid.asSingleSelect().addValueChangeListener(e -> {
            editor.editWord(e.getValue());
        });
        return grid;
    }

}


