package org.tbansal.dutch.learning.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.springframework.util.StringUtils;
import org.tbansal.dutch.learning.app.entities.DutchWords;
import org.tbansal.dutch.learning.app.service.WordService;
import org.vaadin.klaudeta.PaginatedGrid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.WeakHashMap;

public class DutchExamComponent extends VerticalLayout {

    private final WordService wordService;

    private final PaginatedGrid<DutchWords> grid = new PaginatedGrid<>();

    private final TextField filterTextBox;
    private final Button addNewButton;
    private final Button saveAll;

    ;

    public DutchExamComponent(final WordService wordService) {
        this.wordService = wordService;
        filterTextBox = initFilter();
        addNewButton = initAddButton();
        saveAll = initSaveAllButton();
        setId("word-editor-view");
        HorizontalLayout actions = new HorizontalLayout(filterTextBox, addNewButton);

        add(actions);
        initGrid();
        add(saveAll);
    }

    private Button initAddButton() {
        final Button addNewButton = new Button("Generate Test", VaadinIcon.NATIVE_BUTTON.create());
        addNewButton.addClickListener(e -> {
            this.grid.setItems(wordService.getRandomItems());
        });

        return addNewButton;
    }

    private Button initSaveAllButton() {
        final Button saveAllButton = new Button("Save All", VaadinIcon.NATIVE_BUTTON.create());
        saveAllButton.addClickListener(e -> {
            wordService.countFiledAttemptAndSave(new ArrayList<>(this.grid.getSelectedItems()));
        });
        return saveAllButton;
    }


    private TextField initFilter() {
        final TextField filterTextBox = new TextField();
        filterTextBox.setPlaceholder("Filter by name...");
        filterTextBox.setClearButtonVisible(true);
        filterTextBox.setValueChangeMode(ValueChangeMode.EAGER);
        filterTextBox.addValueChangeListener(e -> refreshGrid(e.getValue()));
        return filterTextBox;
    }


    private void refreshGrid(String filterText) {
        if (!StringUtils.isEmpty(filterText)) {
            grid.setItems(wordService.search(filterText));
        } else {
            grid.setItems(wordService.findAll());
        }
    }

    private HorizontalLayout initGrid() {
        HorizontalLayout layout = new HorizontalLayout();
        grid.setPageSize(5);
        grid.setPaginatorSize(3);
        Grid.Column<DutchWords> idColumn = grid.addColumn(DutchWords::getId)
                .setHeader("ID");
        Grid.Column<DutchWords> wordsColumn = grid.addColumn(DutchWords::getDutchWord)
                .setHeader("Word");
        Grid.Column<DutchWords> translationColumn = grid.addColumn(DutchWords::getEnglishTranslation)
                .setHeader("Translation");
        Grid.Column<DutchWords> exampleColumn = grid.addColumn(DutchWords::getExample)
                .setHeader("Example");


        Binder<DutchWords> binder = new Binder<>(DutchWords.class);
        Editor<DutchWords> editor = getGridEditor(binder);

        Div validationStatus = getMessageDiv();
        TextField answerField = initAnswerField(binder, validationStatus);
        initMatchingField(binder, validationStatus);
        createButtons(grid, editor, answerField);
        refreshGrid(null);
        setClassGenerator();

        add(validationStatus, grid);
        return layout;
    }

    private void setClassGenerator() {
        grid.setClassNameGenerator(dutchWords -> dutchWords.isMatching() ? "matching" : "not-matching");
    }

    private Div getMessageDiv() {
        Div validationStatus = new Div();
        validationStatus.setId("validation");
        return validationStatus;
    }

    private Editor<DutchWords> getGridEditor(Binder<DutchWords> binder) {
        Editor<DutchWords> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);
        return editor;
    }

    private TextField initAnswerField(Binder<DutchWords> binder, Div validationStatus) {
        Grid.Column<DutchWords> answerColumn = grid.addColumn(DutchWords::getAnswer)
                .setHeader("Answer");
        TextField answerField = new TextField();
        binder.forField(answerField)
                .withValidator(new StringLengthValidator("Answer length must be between 3 and 50.", 3, 50))
                .withStatusLabel(validationStatus).bind("answer");
        answerColumn.setEditorComponent(answerField);
        return answerField;
    }

    private void initMatchingField(Binder<DutchWords> binder, Div validationStatus) {
        Grid.Column<DutchWords> matchingColumn = grid.addColumn(DutchWords::isMatching)
                .setHeader("Matching?");
        Checkbox matchingField = new Checkbox();
        binder.forField(matchingField)
                .withStatusLabel(validationStatus).bind("matching");
        matchingColumn.setEditorComponent(matchingField);
    }


    private void createButtons(Grid grid, Editor<DutchWords> editor, TextField focusField) {
        Collection<Button> editButtons = Collections
                .newSetFromMap(new WeakHashMap<>());

        Grid.Column<DutchWords> editorColumn = grid.addComponentColumn(dutchWord -> {
            Button edit = new Button("Edit");
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                editor.editItem((DutchWords) dutchWord);
                focusField.focus();
            });
            edit.setEnabled(!editor.isOpen());
            editButtons.add(edit);
            return edit;
        });

        editor.addOpenListener(e -> editButtons.stream()
                .forEach(button -> {
//                    System.out.println("opoen listener" + button.getId());
                    button.setEnabled(!editor.isOpen());
                }));
        editor.addCloseListener(e -> editButtons.stream()
                .forEach(button -> {
//                    System.out.println("close listener" + button.getId());
                    button.setEnabled(!editor.isOpen());
                }));
        Button save = new Button("Save", e -> {
//            System.out.println("Saving");
            editor.save();
        });
        save.addClassName("save");

        Button cancel = new Button("Cancel", e -> {
//            System.out.println("Cancelling");
            editor.cancel();
        });
        cancel.addClassName("cancel");
//
// Add a keypress listener that listens for an escape key up event.
//                Note !some browsers return key as Escape and some as Esc
        grid.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);

        editor.addSaveListener(
                event -> {
                    System.out.println(event.getItem().getAnswer());
                    wordService.checkAndUpdate(event.getItem());
                });

    }

}
