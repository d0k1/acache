# Transactions, Caches and Smoking trees ~~(Lock, Stock and Two Smoking Barrels)~~

One day found myself trapped into a weird problem: how to efficiently work with hierarchical data atomically.
So, I have Infinispan cache, trivial spring context, JBoss JTA, and a **wast of hierarchical data**.
And I want to read, write cache items (parent, child, siblings, subtrees) under **transactions**.
And I want do it as **efficient as possible**.
So here is some benchmarks to determine the the right way.
