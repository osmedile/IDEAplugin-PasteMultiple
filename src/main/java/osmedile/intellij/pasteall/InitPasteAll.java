package osmedile.intellij.pasteall;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.Transferable;

/**
 * @author Olivier Smedile
 * @version $Id$
 */
public class InitPasteAll implements ApplicationComponent {
    public InitPasteAll() {
    }

    public void initComponent() {
        CopyPasteManager.getInstance().addContentChangedListener(
                new CopyPasteManager.ContentChangedListener() {
                    public void contentChanged(Transferable oldCopied, Transferable newCopied) {

                        String oldVal = PasteUtils.getValue(oldCopied);
                        String s = PasteUtils.getValue(newCopied);
                        //same value copied twice, ignore it
                        if (oldVal.equals(s)) {
                            return;
                        }

                        if (StringUtil.isNotEmpty(s) && PasteAllAction.getNumberOfItems() != -1) {
                            boolean alreadyExist = false;
                            //search if s is already in the applicable transferable

                            Transferable[] trans = CopyPasteManager.getInstance().getAllContents();

                            int itemChecked = 0;
                            for (int i = 1; i < trans.length &&
                                    itemChecked < PasteAllAction.getNumberOfItems();
                                 i++) {
                                String value = PasteUtils.getValue(trans[i]);
                                if (StringUtil.isNotEmpty(value)) {
                                    if (s.equals(value)) {
                                        alreadyExist = true;
                                    }
                                    itemChecked++;
                                }
                            }
                            if (!alreadyExist) {
                                //if not it's a new item
                                PasteAllAction.incNumberOfItems();
                            } else {
                                //The transferable object will be moved to
                                // the first case of array:
                                //CopyPasteManager.getInstance().getAllContents()
                                //Do not touch number of items to paste
                            }
                        }
                    }
                });

    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "InitPasteAll";
    }
}
