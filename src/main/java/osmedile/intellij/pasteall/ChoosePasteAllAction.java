package osmedile.intellij.pasteall;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;

/**
 * @author Olivier Smedile
 * @version $Id$
 */
public class ChoosePasteAllAction extends EditorAction {

    public ChoosePasteAllAction() {
        super(null);

        this.setupHandler(new EditorWriteActionHandler() {
            public void executeWriteAction(Editor editor,
                                           DataContext dataContext) {

                ChooseContentUI chooseContentUI = new ChooseContentUI(editor.getProject(), editor);
                chooseContentUI.pack();
                chooseContentUI.setLocationByPlatform(true);
                chooseContentUI.setVisible(true);
            }
        });
    }

}