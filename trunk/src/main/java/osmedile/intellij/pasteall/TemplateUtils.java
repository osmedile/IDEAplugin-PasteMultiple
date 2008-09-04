package osmedile.intellij.pasteall;

import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInsight.template.impl.TemplateSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olivier Smedile
 * @version $Id$
 */
public class TemplateUtils {

    public static List<TemplateImpl> getSelectionTemplates() {
        TemplateImpl atemplateimpl[] =
                TemplateSettings.getInstance().getTemplates();
        ArrayList<TemplateImpl> arraylist = new ArrayList<TemplateImpl>();
        int k = atemplateimpl.length;
        for (int l = 0; l < k; l++) {
            TemplateImpl templateimpl = atemplateimpl[l];
            if (!templateimpl.isDeactivated() &&
//                    templateimpl.getTemplateContext().isInContext(j) &&
                    templateimpl.isSelectionTemplate()) {
                arraylist.add(templateimpl);
            }
        }

        return arraylist;
    }
}
