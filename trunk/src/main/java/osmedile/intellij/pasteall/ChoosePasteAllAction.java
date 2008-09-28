package osmedile.intellij.pasteall;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;

/**
 * Show a dialog to paste multiple items.
 *
 * @author Olivier Smedile
 * @version $Id$
 */
public class ChoosePasteAllAction extends EditorAction {

    /**
     * Used to remember "use template" checkbox status.
     */
    private boolean useTemplate;

    /**
     * Used to remember the selected template in Combo box of available templates.
     */
    private int tplIdx;

    /**
     * Used to remember the template.
     */
    private String template = null;

    /**
     * Used to remember if items are pasted with older first.
     */
    private boolean olderFirst = true;

    public ChoosePasteAllAction() {
        super(null);

        this.setupHandler(new EditorWriteActionHandler() {
            public void executeWriteAction(Editor editor, DataContext dc) {

                //reapply saved configuration of dialog
                ChooseContentUI chooseContentUI =
                        new ChooseContentUI(editor, ChoosePasteAllAction.this);


                chooseContentUI.pack();
                chooseContentUI.setLocationByPlatform(true);
                chooseContentUI.setVisible(true);
            }
        });
    }


    public boolean getUseTemplate() {
        return useTemplate;
    }

    public void setUseTemplate(boolean useTemplate) {
        this.useTemplate = useTemplate;
    }

    public int getTplIdx() {
        return tplIdx;
    }

    public void setTplIdx(int tplIdx) {
        this.tplIdx = tplIdx;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public boolean isOlderFirst() {
        return olderFirst;
    }

    public void setOlderFirst(boolean olderFirst) {
        this.olderFirst = olderFirst;
    }
}