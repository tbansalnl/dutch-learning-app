package org.tbansal.dutch.learning.app.vaadin.components;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.tbansal.dutch.learning.app.entities.DutchWords;
import org.tbansal.dutch.learning.app.service.WordService;

@SpringComponent
@UIScope
public class WordEditorComponent extends VerticalLayout implements KeyNotifier {

    private final WordService wordService;

    private DutchWords dutchWords;

    private final TextField dutchWord = new TextField("Dutch Word");
    private final TextField englishTranslation = new TextField("Translation");
    private final TextField example = new TextField("Example");
    private final TextField type = new TextField("Type");
    private final Button addButton = new Button("Add", VaadinIcon.CHECK.create());
    private final Button cancelButton = new Button("Delete", VaadinIcon.BUTTON.create());
    Button deleteButton = new Button("Delete", VaadinIcon.TRASH.create());
    HorizontalLayout actions = new HorizontalLayout(addButton, cancelButton, deleteButton);

    Binder<DutchWords> binder = new Binder<>(DutchWords.class);
    private ChangeHandler changeHandler;

    public WordEditorComponent(final WordService wordService) {
        this.wordService = wordService;

        add(dutchWord, englishTranslation, example, type, actions);

        binder.bindInstanceFields(this);

        setSpacing(true);

        addButton.getElement().getThemeList().add("primary");
        deleteButton.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        addButton.addClickListener(e -> save());
        deleteButton.addClickListener(e -> delete());
        cancelButton.addClickListener(e -> editWord(dutchWords));
        setVisible(false);
    }

    void delete() {
        wordService.delete(dutchWords);
        changeHandler.onChange();

    }

    void save() {
        wordService.save(dutchWords);
        changeHandler.onChange();
    }

    public interface ChangeHandler {
        void onChange();
    }


    public final void editWord(DutchWords c) {
        if (c == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = c.getId() != null;
        if (persisted) {
            dutchWords = wordService.findById(c.getId()).get();
        } else {
            dutchWords = c;
        }

        cancelButton.setVisible(persisted);
        binder.setBean(dutchWords);
        setVisible(true);
        dutchWord.focus();
    }

    public void setChangeHandler(ChangeHandler h) {
        changeHandler = h;
    }
}
