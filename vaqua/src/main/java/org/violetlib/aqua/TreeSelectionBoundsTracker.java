/*
 * Copyright (c) 2015-2018 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */

package org.violetlib.aqua;

import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;

/**
 * Update the bounds of the selected rows of a JTree. The update() method must be called after potential changes to the
 * tree layout.
 */
public abstract class TreeSelectionBoundsTracker implements SelectionBoundsTracker {
    protected JTree tree;
    protected Consumer<SelectionBoundsDescription> consumer;

    private /* @Nullable */ SelectionBoundsDescription currentSelectionDescription;

    public TreeSelectionBoundsTracker(JTree tree, Consumer<SelectionBoundsDescription> consumer) {
        this.tree = tree;
        this.consumer = consumer;

        update();

        if (consumer != null && currentSelectionDescription == null) {
            consumer.accept(null);
        }
    }

    @Override
    public void dispose() {
        tree = null;
        consumer = null;
    }

    @Override
    public void reset() {
        if (currentSelectionDescription != null) {
            currentSelectionDescription = null;
            if (consumer != null) {
                consumer.accept(null);
            }
        }
    }

    @Override
    public void setConsumer(Consumer<SelectionBoundsDescription> consumer) {
        if (consumer != this.consumer) {
            this.consumer = consumer;
            if (consumer != null) {
                consumer.accept(currentSelectionDescription);
            }
        }
    }

    /**
     * Call this method when the tree layout (row bounds) may have changed.
     */
    @Override
    public void update() {
        if (tree != null) {
            TreeSelectionModel sm = tree.getSelectionModel();
            int[] selectedRows = sm != null ? sm.getSelectionRows() : null;
            if (selectedRows != null && selectedRows.length == 0) {
                selectedRows = null;
            }
            try {
                updateFromSelectedRows(selectedRows);
            } catch (IllegalComponentStateException ex) {
                // interaction with AquaUtils.paintImmediately()
            }
        }
    }

    protected void updateFromSelectedRows(/* @Nullable */ int[] rows) {
        SelectionBoundsDescription newSelectionDescription = createSelectionDescription(rows);
        if (!Objects.equals(newSelectionDescription, currentSelectionDescription)) {
            currentSelectionDescription = newSelectionDescription;
            if (consumer != null) {
                consumer.accept(currentSelectionDescription);
            }
        }
    }

    private /* @Nullable */ SelectionBoundsDescription createSelectionDescription(int[] rows) {
        if (rows == null) {
            return null;
        }
        SelectionBoundsDescription sd = new SelectionBoundsDescription(rows.length);
        for (int row : rows) {
            Rectangle bounds = tree.getRowBounds(row);
            int y = convertRowYCoordinateToSelectionDescription(bounds.y);
            sd.addRegion(y, bounds.height);
        }
        return sd;
    }

    /**
     * Map the Y location of a row in the tree coordinate system to the Y location to be stored in the selection
     * description.
     */
    protected int convertRowYCoordinateToSelectionDescription(int y) {
        return y;
    }
}
