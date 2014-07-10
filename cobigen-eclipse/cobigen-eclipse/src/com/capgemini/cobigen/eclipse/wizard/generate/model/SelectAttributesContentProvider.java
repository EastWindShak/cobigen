/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.eclipse.wizard.generate.model;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Provides the Contents for a {@link Map} such that each element is an map {@link Entry}
 * @author mbrunnli (12.03.2013)
 */
public class SelectAttributesContentProvider implements IStructuredContentProvider {

    /**
     * {@inheritDoc}
     * @author mbrunnli (12.03.2013)
     */
    @Override
    public void dispose() {
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (12.03.2013)
     */
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (12.03.2013)
     */
    @Override
    public Object[] getElements(Object inputElement) {
        Object[] result = new Object[0];
        if (inputElement instanceof Map<?, ?>) {
            result = new Object[((Map<?, ?>) inputElement).size()];
            int i = 0;
            for (Object entry : ((Map<?, ?>) inputElement).entrySet()) {
                result[i] = entry;
                i++;
            }
        }
        return result;
    }
}
