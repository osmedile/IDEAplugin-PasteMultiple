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

    private boolean useTemplate = false;
    private int tplIdx = 0;
    private String template = "";
    private boolean olderFirst = true;

    public ChoosePasteAllAction() {
        super(null);

        this.setupHandler(new EditorWriteActionHandler() {
            public void executeWriteAction(Editor editor, DataContext dc) {

                ChooseContentUI chooseContentUI =
                        new ChooseContentUI(editor.getProject(), editor);
                chooseContentUI.getUseTemplatChk().setSelected(useTemplate);
                chooseContentUI.getTemplateBox().setSelectedIndex(tplIdx);
                chooseContentUI.getTemplateViewer().getDocument()
                        .setText(template);
                if (olderFirst) {
                    chooseContentUI.getOlderFirst().setSelected(true);
                } else {
                    chooseContentUI.getRecentFirst().setSelected(true);
                }


                chooseContentUI.pack();
                chooseContentUI.setLocationByPlatform(true);
                chooseContentUI.setVisible(true);

                useTemplate = chooseContentUI.getUseTemplatChk().isSelected();
                tplIdx = chooseContentUI.getTemplateBox().getSelectedIndex();
                template = chooseContentUI.getTemplateViewer().getDocument()
                        .getText();
                olderFirst = chooseContentUI.getOlderFirst().isSelected();

            }
        });
    }

}