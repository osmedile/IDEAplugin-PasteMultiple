package osmedile.intellij.pasteall;

import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.util.Ref;

import javax.swing.*;
import java.util.List;

/**
 * @author Olivier Smedile
 * @version $Id$
 */
public class PasteAllWithTemplateAction extends EditorAction {

    private boolean newLine = true;

    private List<TemplateImpl> templates;
    private JList list;
    private PopupChooserBuilder popup;


    public PasteAllWithTemplateAction(EditorActionHandler editorActionHandler) {
        super(editorActionHandler);
    }

    public PasteAllWithTemplateAction() {
        super(null);

        this.setupHandler(new EditorWriteActionHandler() {
            public void executeWriteAction(final Editor editor,
                                           DataContext dataContext) {

                if (popup == null) {
                    templates = TemplateUtils.getSelectionTemplates();

                    list = new JList(
                            ChooseContentUI.getTemplateDesc(templates));

                    popup = JBPopupFactory.getInstance()
                            .createListPopupBuilder(list);
                }

                popup.setItemChoosenCallback(new Runnable() {
                    public void run() {
                        CommandProcessor.getInstance().executeCommand(
                                editor.getProject(), new Runnable() {
                            public void run() {
                                pasteItIDEA(editor);
                            }
                        },
                                "Paste with template",
                                new Ref(editor.getDocument()));
                    }
                });
                popup.createPopup().showInBestPositionFor(editor);
            }
        });
    }

    public void pasteItIDEA(final Editor editor) {
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
        TemplateImpl tpl = templates.get(list.getSelectedIndex());
        PasteUtils.pasteAll(editor,
                CopyPasteManager.getInstance().getAllContents(),
                getNewLine(), getOlderFirst(),
                PasteAllAction.getNumberOfItems(), tpl.getString());
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public boolean getNewLine() {
        return newLine;
    }

    public boolean getOlderFirst() {
        return true;
    }
}