/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package showcase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author oleg
 */
public class CheckboxList<T> extends JList {
    private final Map<T, JCheckBox> myItemMap = new HashMap<T, JCheckBox>();

    public CheckboxList() {
        this(new DefaultListModel());
    }

    public CheckboxList(final DefaultListModel dataModel) {
        super();
        //noinspection unchecked
        setModel(dataModel);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }



    @Nullable
    private static Point getChildLocationRelativeToAncestor(@Nonnull Component ancestor, @Nonnull Component child) {
        int dx = 0, dy = 0;
        Component c = child;
        while (c != null && c != ancestor) {
            Point p = c.getLocation();
            dx += p.x;
            dy += p.y;
            c = child.getParent();
        }
        return c == ancestor ? new Point(dx, dy) : null;
    }


    @Nonnull
    private JCheckBox getCheckBoxAt(int index) {
        return (JCheckBox)getModel().getElementAt(index);
    }

    public void setStringItems(final Map<String, Boolean> items) {
        clear();
        for (Map.Entry<String, Boolean> entry : items.entrySet()) {
            //noinspection unchecked
            addItem((T)entry.getKey(), entry.getKey(), entry.getValue());
        }
    }

    public void setItems(final List<T> items, @Nullable Function<T, String> converter) {
        setItems(items, converter, t -> false);
    }

    public void setItems(final List<T> items, @Nullable Function<T, String> converter, Function<T, Boolean> stateFunc) {
        clear();
        for (T item : items) {
            String text = converter != null ? converter.apply(item) : item.toString();
            addItem(item, text, stateFunc.apply(item));
        }
    }

    public void addItem(T item, String text, boolean selected) {
        JCheckBox checkBox = new JCheckBox(text, selected) {
            @Override
            public void paint(Graphics g) {
                System.out.println(getBounds());
                super.paint(g);
            }

            @Override
            public void updateUI() {

                super.updateUI();

                ButtonUI ui = getUI();

                System.out.println("test");
            }
        };
        checkBox.setOpaque(true); // to paint selection background
        myItemMap.put(item, checkBox);
        //noinspection unchecked
        ((DefaultListModel)getModel()).addElement(checkBox);
    }

    public void updateItem(@Nonnull T oldItem, @Nonnull T newItem, @Nonnull String newText) {
        JCheckBox checkBox = myItemMap.remove(oldItem);
        myItemMap.put(newItem, checkBox);
        checkBox.setText(newText);
        DefaultListModel model = (DefaultListModel)getModel();
        int ind = model.indexOf(checkBox);
        if (ind >= 0) {
            model.set(ind, checkBox); // to fire contentsChanged event
        }
    }

    public int getItemIndex(T item) {
        JCheckBox checkBox = myItemMap.get(item);
        if(checkBox == null) {
            return -1;
        }
        return ((DefaultListModel) getModel()).indexOf(checkBox);
    }

    public void clear() {
        ((DefaultListModel)getModel()).clear();
        myItemMap.clear();
    }

    public boolean isItemSelected(int index) {
        return ((JCheckBox)getModel().getElementAt(index)).isSelected();
    }

    public boolean isItemSelected(T item) {
        JCheckBox checkBox = myItemMap.get(item);
        return checkBox != null && checkBox.isSelected();
    }

    public void setItemSelected(T item, boolean selected) {
        JCheckBox checkBox = myItemMap.get(item);
        if (checkBox != null) {
            checkBox.setSelected(selected);
        }
    }

    private void setSelected(JCheckBox checkbox, int index, boolean value) {
        checkbox.setSelected(value);
        repaint();

        // fire change notification in case if we've already initialized model
        final ListModel model = getModel();
        if (model instanceof DefaultListModel) {
            //noinspection unchecked
            ((DefaultListModel)model).setElementAt(getModel().getElementAt(index), index);
        }


    }


    protected JComponent adjustRendering(JComponent rootComponent, final JCheckBox checkBox, int index, final boolean selected, final boolean hasFocus) {
        return rootComponent;
    }


    @Nullable
    protected String getSecondaryText(int index) {
        return null;
    }

    protected Color getBackground(final boolean isSelected) {
        return isSelected ? getSelectionBackground() : getBackground();
    }

    protected Color getForeground(final boolean isSelected) {
        return isSelected ? getSelectionForeground() : getForeground();
    }
}
