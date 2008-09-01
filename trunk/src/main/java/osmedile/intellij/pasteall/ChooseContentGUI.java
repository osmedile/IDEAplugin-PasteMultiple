package osmedile.intellij.pasteall;

import com.intellij.CommonBundle;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * @author Olivier Smedile
 * @version $Id$
 */
public class ChooseContentGUI extends DialogWrapper {
    private static final Icon textIcon =
            IconLoader.getIcon("/fileTypes/text.png");
    private JList myList;
    private Editor myViewer;
    private final boolean myUseIdeaEditor;
    private Splitter mySplitter;
    private Project myProject;

    private static class MyListCellRenderer extends ColoredListCellRenderer {
        protected void customizeCellRenderer(JList list, Object value,
                                             int index, boolean selected,
                                             boolean hasFocus) {
            setIcon(textIcon);
            if (index <= 9) {
                append((new StringBuilder())
                        .append(String.valueOf((index + 1) % 10))
                        .append("  ").toString(),
                        SimpleTextAttributes.GRAYED_ATTRIBUTES);
            }
            append((String) value, SimpleTextAttributes.REGULAR_ATTRIBUTES);
        }

        private MyListCellRenderer() {
        }
    }

    public ChooseContentGUI(Project project, String title,
                            boolean useIdeaEditor) {
        super(project, true);
        myProject = project;
        myUseIdeaEditor = useIdeaEditor;
        setOKButtonText(CommonBundle.getOkButtonText());
        setTitle(title);
        init();
    }

// --------------------- METHODS ---------------------

    protected JComponent createCenterPanel() {
        myList = new JList();
        myList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


        rebuildListContent();


        myList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.isConsumed() || e.getClickCount() != 2 ||
                        e.isPopupTrigger()) {
                } else {
                    close(0);
                }
            }
        }
        );

        myList.setCellRenderer(new MyListCellRenderer());
        myList.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    //TODO
//                    int selectedIndex = getSelectedIndex();
//                    int size = myAllContents.size();
//                    removeContentAt(myAllContents.get(selectedIndex));
//                    rebuildListContent();
//                    if (size == 1) {
//                        close(1);
//                        return;
//                    }
//                    myList.setSelectedIndex(
//                            Math.min(selectedIndex, myAllContents.size() - 1));
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    close(0);
                } else {
//                    char aChar = e.getKeyChar();
//                    if (aChar >= '0' && aChar <= '9') {
//                        int idx = aChar != '0' ? aChar - 49 : 9;
//                        if (idx < myAllContents.size()) {
//                            myList.setSelectedIndex(idx);
//                        }
//                    }
                }
            }

        }
        );
        mySplitter = new Splitter(true);
        mySplitter.setFirstComponent(new JScrollPane(myList));
        mySplitter.setSecondComponent(new JPanel());
        updateViewerForSelection();
        myList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateViewerForSelection();
            }
        }
        );
        mySplitter.setPreferredSize(new Dimension(500, 500));
        return mySplitter;
    }

    private void rebuildListContent() {
        Transferable[] trans = CopyPasteManager.getInstance().getAllContents();

        ArrayList<String> shortened = new ArrayList<String>();


        for (Transferable tran : trans) {
            String value = PasteUtils.getValue(tran);
            if (StringUtil.isNotEmpty(value)) {
                shortened.add(value);
            }
        }
        myList.setListData(shortened.toArray(new String[shortened.size()]));

    }


    private void updateViewerForSelection() {
        if (myList.getSelectedValue() == null) {
            return;
        }
        String fullString = (String) myList.getSelectedValue();
        fullString = StringUtil.convertLineSeparators(fullString);
        if (myViewer != null) {
            EditorFactory.getInstance().releaseEditor(myViewer);
        }
        if (myUseIdeaEditor) {
            Document doc =
                    EditorFactory.getInstance().createDocument(fullString);
            myViewer = EditorFactory.getInstance().createViewer(doc, myProject);
            myViewer.getComponent().setPreferredSize(new Dimension(300, 500));
            myViewer.getSettings().setFoldingOutlineShown(false);
            myViewer.getSettings().setLineNumbersShown(false);
            myViewer.getSettings().setLineMarkerAreaShown(false);
            mySplitter.setSecondComponent(myViewer.getComponent());
        } else {
            JTextArea textArea = new JTextArea(fullString);
            textArea.setRows(3);
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            textArea.setSelectionStart(0);
            textArea.setSelectionEnd(textArea.getText().length());
            textArea.setEditable(false);
            mySplitter.setSecondComponent(new JScrollPane(textArea));
        }
        mySplitter.revalidate();
    }

    public void dispose() {
        super.dispose();
        if (myViewer != null) {
            EditorFactory.getInstance().releaseEditor(myViewer);
            myViewer = null;
        }
    }

    protected String getDimensionServiceKey() {
        return "#com.intellij.openapi.editor.actions.MultiplePasteAction.Chooser";
    }

    public JComponent getPreferredFocusedComponent() {
        return myList;
    }

    public String[] getSelectedValues() {
        final Object[] selected = myList.getSelectedValues();
        String[] selects = new String[selected.length];
        System.arraycopy(selected, 0, selects, 0, selected.length);

        return selects;
    }
}
