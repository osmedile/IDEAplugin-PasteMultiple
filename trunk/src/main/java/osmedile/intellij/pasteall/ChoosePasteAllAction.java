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
    private boolean useTemplate = false;

    /**
     * Used to remember the selected template in Combo box of available templates.
     */
    private int tplIdx = 0;

    /**
     * Used to remember the template.
     */
    private String template = "";

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
                        new ChooseContentUI(editor.getProject(), editor);
                chooseContentUI.getUseTemplatChk().setSelected(useTemplate);
                chooseContentUI.getTemplateBox().setSelectedIndex(tplIdx);
                chooseContentUI.getTemplateViewer().getDocument().setText(template);
                if (olderFirst) {
                    chooseContentUI.getOlderFirst().setSelected(true);
                } else {
                    chooseContentUI.getRecentFirst().setSelected(true);
                }


                chooseContentUI.pack();
                chooseContentUI.setLocationByPlatform(true);
                chooseContentUI.getTransList().requestFocus();
                chooseContentUI.getTransList().requestFocusInWindow();
                chooseContentUI.setVisible(true);

                //save dialog configuration
                useTemplate = chooseContentUI.getUseTemplatChk().isSelected();
                tplIdx = chooseContentUI.getTemplateBox().getSelectedIndex();
                template = chooseContentUI.getTemplateViewer().getDocument().getText();
                olderFirst = chooseContentUI.getOlderFirst().isSelected();

            }
        });
    }

}