package com.focusit.istree.tree;

import java.io.Serializable;
import java.util.Comparator;

public class FastFqnComparator implements Comparator<FastFqn>, Serializable
{
    public static final FastFqnComparator INSTANCE = new FastFqnComparator();
    private static final long serialVersionUID = -1357631755443829281L;

    /**
     * Returns -1 if the first comes before; 0 if they are the same; 1 if the second Fqn comes before.  <code>null</code>
     * always comes first.
     */
    @Override
    public int compare(FastFqn fqn1, FastFqn fqn2)
    {
        int s1 = fqn1.size();
        int s2 = fqn2.size();

        if (s1 == 0)
        {
            return (s2 == 0) ? 0 : -1;
        }

        if (s2 == 0)
        {
            return 1;
        }

        int size = Math.min(s1, s2);

        for (int i = 0; i < size; i++)
        {
            long e1 = fqn1.getLong(i);
            long e2 = fqn2.getLong(i);
            if (e1 == e2)
            {
                continue;
            }
            if (e1 == -1)
            {
                return 0;
            }
            if (e2 == -1)
            {
                return 1;
            }
            if (e1 != e2)
            {
                int c = (int)(e1 - e2);
                if (c != 0)
                {
                    return c;
                }
            }
        }

        return s1 - s2;
    }
}