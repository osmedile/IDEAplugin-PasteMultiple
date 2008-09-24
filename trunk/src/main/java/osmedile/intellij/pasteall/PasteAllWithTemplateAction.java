package osmedile.intellij.pasteall;

import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;

import javax.swing.*;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * Show a popup to select a template. List of templates is taken from "Live templates" marked as
 * "surround templates" (usage of $SELECTION$ variable).
 *
 * Paste all items (limited by the mark) with older items first and apply the selected template.
 *
 * @author Olivier Smedile
 * @version $Id$
 */
public class PasteAllWithTemplateAction extends AnAction {

    private List<TemplateImpl> templates;

    /**
     * JList of templates
     */
    private JList templateList;

    /**
     * Popup to select a template from the JList
     */
    private PopupChooserBuilder popup;

    private Editor editor;

    public PasteAllWithTemplateAction() {
        templates = TemplateUtils.getSelectionTemplates();
        templateList = new JList(ChooseContentUI.getTemplateDesc(templates));
        popup = JBPopupFactory.getInstance().createListPopupBuilder(templateList);

        popup.setTitle("Paste with template");
        popup.setItemChoosenCallback(new Runnable() {
            public void run() {
                CommandProcessor.getInstance().executeCommand(editor.getProject(), new Runnable() {
                    public void run() {
                        pasteItIDEA();
                    }
                },
                        "Paste with template", null,
                        UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);


            }
        });
        popup.setMovable(true);
    }

    public void actionPerformed(AnActionEvent e) {
        editor = e.getData(DataKeys.EDITOR);

        //remove all listeners, otherwise they will be registered once again
        // every time the action is called
        final MouseListener[] listeners = templateList.getMouseListeners();
        for (MouseListener listener : listeners) {
            templateList.removeMouseListener(listener);
        }

        popup.createPopup().showCenteredInCurrentWindow(editor.getProject());
    }

    public void pasteItIDEA() {
        if (editor.isViewer()) {
            return;
        }

        if (!editor.getDocument().isWritable()) {
            if (!FileDocumentManager.fileForDocumentCheckedOutSuccessfully(
                    editor.getDocument(),
                    editor.getProject())) {
                return;
            }
        }

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                pasteIt(editor);
            }
        });
    }

    private void pasteIt(Editor editor) {
        TemplateImpl tpl = templates.get(templateList.getSelectedIndex());

        PasteUtils.pasteAll(editor, CopyPasteManager.getInstance().getAllContents(),
                true, getOlderFirst(), PasteAllAction.getNumberOfItems(), tpl.getString());
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public boolean getOlderFirst() {
        return true;
    }
}