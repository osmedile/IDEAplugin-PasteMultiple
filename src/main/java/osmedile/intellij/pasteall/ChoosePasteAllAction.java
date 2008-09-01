package osmedile.intellij.pasteall;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;

/**
 * @author Olivier Smedile
 * @version $Id$
 */
public class ChoosePasteAllAction extends PasteAllAction {

    private boolean newLine = true;

    private boolean olderFirst = true;

    public ChoosePasteAllAction() {
        super(null);

        this.setupHandler(new EditorWriteActionHandler() {
            public void executeWriteAction(Editor editor,
                                           DataContext dataContext) {

                showPopup(editor);
            }
        });
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public boolean isNewLine() {
        return newLine;
    }

    public boolean getNewLine() {
        return newLine;
    }

    public void setNewLine(boolean newLine) {
        this.newLine = newLine;
    }

    public boolean getOlderFirst() {
        return olderFirst;
    }

    public void setOlderFirst(boolean olderFirst) {
        this.olderFirst = olderFirst;
    }


    public void showPopup(Editor editor) {
        ChooseContentGUI contentchooser =
                new ChooseContentGUI(editor.getProject(), "Paste All", true);

        contentchooser.show();

        pasteAll(editor, newLine, olderFirst,
                contentchooser.getSelectedValues());

    }

}