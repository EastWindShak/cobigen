package com.capgemini.cobigen.eclipse.generator.entity;

import java.util.List;

import com.capgemini.cobigen.extension.to.IncrementTo;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.google.common.collect.Lists;

/**
 * Comparable {@link IncrementTo}, which compares the Increments on their description
 * @author mbrunnli (09.04.2014)
 */
public class ComparableIncrement extends IncrementTo implements Comparable<IncrementTo> {

    /**
     * All dependent increments, means all increments which are sub increments of this
     */
    private List<ComparableIncrement> dependentIncrements = Lists.newLinkedList();

    /**
     * Creates a new comparable increment with the given properties
     * @param id
     *            of the increment
     * @param description
     *            of the increment
     * @param triggerId
     *            id of the trigger, the increment correlates to
     * @param templates
     *            templates (recursively resolved)
     * @param dependentIncrements
     *            all dependent increments, means all increments which are sub increments of this
     * @author mbrunnli (09.04.2014)
     */
    public ComparableIncrement(String id, String description, String triggerId, List<TemplateTo> templates,
        List<IncrementTo> dependentIncrements) {
        super(id, description, triggerId, templates, dependentIncrements);
        this.dependentIncrements = convertIncrements(dependentIncrements);
    }

    /**
     * Converts all {@link IncrementTo}s to {@link ComparableIncrement}s
     * @param increments
     *            {@link List} of {@link IncrementTo}s to be converted
     * @return the {@link List} of converted {@link ComparableIncrement}s
     * @author mbrunnli (10.04.2014)
     */
    private List<ComparableIncrement> convertIncrements(List<IncrementTo> increments) {
        List<ComparableIncrement> comparableIncrements = Lists.newLinkedList();
        for (IncrementTo increment : increments) {
            comparableIncrements.add(new ComparableIncrement(increment.getId(), increment.getDescription(),
                increment.getTriggerId(), increment.getTemplates(), increment.getDependentIncrements()));
        }
        return comparableIncrements;
    }

    /**
     * Adds a new {@link TemplateTo} to this increment
     * @param template
     *            {@link TemplateTo} to be added
     * @author mbrunnli (09.04.2014)
     */
    public void addTemplate(TemplateTo template) {
        templates.add(template);
    }

    @Override
    public List<TemplateTo> getTemplates() {
        return templates;
    }

    /**
     * Returns all dependent increments
     * @return all dependent increments
     * @author mbrunnli (10.04.2014)
     */
    public List<ComparableIncrement> getDependentComparableIncrements() {
        return dependentIncrements;
    }

    @Override
    public int compareTo(IncrementTo o) {
        return getDescription().compareTo(o.getDescription());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof ComparableIncrement) {
            String objId = ((ComparableIncrement) obj).getId();
            String objTriggerId = ((ComparableIncrement) obj).getTriggerId();
            if ((objId == null ^ getId() == null)) {
                return false;
            } else if (objId != null && getId() != null) {
                if (objId.equals("all") && objId.equals(getId())) {
                    // Exception "all" increment
                    return true;
                } else if (objTriggerId == null ^ getTriggerId() == null) {
                    return false;
                }
                if (objTriggerId != null && getTriggerId() != null) {
                    return getTriggerId().equals(objTriggerId) && getId().equals(objId);
                } else {
                    getId().equals(objId);
                }
            } else if (objTriggerId != null && getTriggerId() != null) {
                return getTriggerId().equals(objTriggerId);
            } else {
                return true;
            }

        }
        return false;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
