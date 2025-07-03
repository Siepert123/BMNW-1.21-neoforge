package nl.melonstudios.bmnw.softcoded.recipe;

import java.util.Comparator;

/**
 * Sorts workbench recipes in the following order:
 * <p>
 * 1. Group*
 * <p>
 * 2. Tier index
 * <p>
 * 3. Display name
 * <p>
 * * NOTE: Recipes without a group are always last
 * @see Comparator
 * @see WorkbenchRecipe
 */
public class WorkbenchRecipeComparator implements Comparator<WorkbenchRecipe> {
    public static final WorkbenchRecipeComparator instance = new WorkbenchRecipeComparator();

    private WorkbenchRecipeComparator() {}

    @Override
    public int compare(WorkbenchRecipe o1, WorkbenchRecipe o2) {
        if (o1 == o2) return 0;
        int sortingIndex = this.compareGroups(o1.group(), o2.group());
        if (sortingIndex != 0) return sortingIndex;
        int tierIndex = Integer.compare(o1.requiredTier(), o2.requiredTier());
        if (tierIndex != 0) return tierIndex;
        return o1.result().getDisplayName().getString().compareTo(o2.result().getDisplayName().getString());
    }

    private int compareGroups(String g1, String g2) {
        if (g1.isEmpty() && g2.isEmpty()) return 0;
        if (g1.isEmpty()) return 1;
        if (g2.isEmpty()) return -1;
        return g1.compareTo(g2);
    }
}
