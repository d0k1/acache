Simplified cache based on infinispan code base. Main feature - it is dumb cache that can work only in local mode. But it can notify any registered listener about any cache changes and listener can replicate them to any other caches in sync or async fashion. Besides, a listener that replicates data can easily organize distributed cache by itself.
Main reason to make yet-one-cache and not to use infinispan is to make simple and light cache without any clustering in mind. But, having notifications, any high-level cache user (higher level "component") can implement clustering itself. Moreover, using embedded infinispan that repilcates it's data isn't good idea, for me. Because, I can't control and affect to how infinispan works in replicated/distributed modes.
So, I started my own cache implementation.
ACache supports or going to support:
* transactions as infinispan does
* non-transactional mode
* optimistic/pessimistic locking
* notifications