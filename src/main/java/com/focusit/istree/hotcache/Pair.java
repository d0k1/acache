package com.focusit.istree.hotcache;

import com.google.common.base.Objects;

public class Pair<F, S>
{
    public static <A, B> Pair<A, B> create(A a, B b)
    {
        return new Pair<A, B>(a, b);
    }

    private F first;

    private S second;

    public Pair(F first, S second)
    {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Pair))
        {
            return false;
        }
        Pair<?, ?> p = (Pair<?, ?>)o;
        return Objects.equal(p.first, first) && Objects.equal(p.second, second);
    }

    @Override
    public int hashCode()
    {
        return (first == null ? 0 : first.hashCode()) ^ (second == null ? 0 : second.hashCode());
    }
}
