package com.focusit.istree.hotcache.core.notifications.cachelistener;

import org.infinispan.compat.TypeConverter;
import org.infinispan.notifications.ClassLoaderAwareListenable;
import org.infinispan.topology.CacheTopology;
import org.infinispan.transaction.xa.GlobalTransaction;

import com.focusit.istree.hotcache.core.commands.FlagAffectedCommand;
import com.focusit.istree.hotcache.core.container.entries.InternalCacheEntry;
import com.focusit.istree.hotcache.core.context.InvocationContext;
import com.focusit.istree.hotcache.core.distribution.ch.ConsistentHash;
import com.focusit.istree.hotcache.core.metadata.Metadata;
import com.focusit.istree.hotcache.core.notifications.ClassLoaderAwareFilteringListenable;
import com.focusit.istree.hotcache.core.partitionhandling.AvailabilityMode;

import java.util.Collection;

/**
 * Public interface with all allowed notifications.
 *
 * @author Mircea.Markus@jboss.com
 * @since 4.0
 */
public interface CacheNotifier<K, V> extends ClassLoaderAwareFilteringListenable<K, V>, ClassLoaderAwareListenable {

   /**
    * Notifies all registered listeners of a {@link org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent} event.
    */
   void notifyCacheEntryCreated(K key, V value, Metadata metadata, boolean pre, InvocationContext ctx, FlagAffectedCommand command);

   /**
    * Notifies all registered listeners of a {@link org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent} event.
    */
   void notifyCacheEntryModified(K key, V value, Metadata metadata, V previousValue, Metadata previousMetadata, boolean pre,
                                 InvocationContext ctx, FlagAffectedCommand command);

   /**
    * Notifies all registered listeners of a {@link org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent} event.
    */
   void notifyCacheEntryRemoved(K key, V previousValue, Metadata previousMetadata, boolean pre, InvocationContext ctx,
                                FlagAffectedCommand command);

   /**
    * Notifies all registered listeners of a {@link org.infinispan.notifications.cachelistener.event.CacheEntryVisitedEvent} event.
    */
   void notifyCacheEntryVisited(K key, V value, boolean pre,
         InvocationContext ctx, FlagAffectedCommand command);

   /**
    * Notifies all registered listeners of a {@link org.infinispan.notifications.cachelistener.event.CacheEntriesEvictedEvent} event.
    */
   void notifyCacheEntriesEvicted(Collection<InternalCacheEntry<? extends K, ? extends V>> entries,
         InvocationContext ctx, FlagAffectedCommand command);

   /**
    * Notifies all registered listeners of a CacheEntryExpired event.
    */
   void notifyCacheEntryExpired(K key, V value, Metadata metadata, InvocationContext ctx);

   /**
    * Notifies all registered listeners of a {@link org.infinispan.notifications.cachelistener.event.CacheEntryInvalidatedEvent} event.
    */
   void notifyCacheEntryInvalidated(K key, V value, Metadata metadata, boolean pre,
         InvocationContext ctx, FlagAffectedCommand command);

   /**
    * Notifies all registered listeners of a {@link org.infinispan.notifications.cachelistener.event.CacheEntryLoadedEvent} event.
    */
   void notifyCacheEntryLoaded(K key, V value, boolean pre,
         InvocationContext ctx, FlagAffectedCommand command);

   /**
    * Notifies all registered listeners of a {@link org.infinispan.notifications.cachelistener.event.CacheEntryActivatedEvent} event.
    */
   void notifyCacheEntryActivated(K key, V value, boolean pre,
         InvocationContext ctx, FlagAffectedCommand command);

   /**
    * Notifies all registered listeners of a {@link org.infinispan.notifications.cachelistener.event.CacheEntryPassivatedEvent} event.
    */
   void notifyCacheEntryPassivated(K key, V value, boolean pre,
         InvocationContext ctx, FlagAffectedCommand command);

   /**
    * Notifies all registered listeners of a transaction completion event.
    *
    * @param transaction the transaction that has just completed
    * @param successful  if true, the transaction committed.  If false, this is a rollback event
    */
   void notifyTransactionCompleted(GlobalTransaction transaction, boolean successful, InvocationContext ctx);

   /**
    * Notifies all registered listeners of a transaction registration event.
    *
    * @param globalTransaction
    */
   void notifyTransactionRegistered(GlobalTransaction globalTransaction, boolean isOriginLocal);

   void notifyDataRehashed(ConsistentHash oldCH, ConsistentHash newCH, ConsistentHash unionCH, int newTopologyId, boolean pre);

   void notifyTopologyChanged(CacheTopology oldTopology, CacheTopology newTopology, int newTopologyId, boolean pre);

   void notifyPartitionStatusChanged(AvailabilityMode mode, boolean pre);

   /**
    * Set an optional converter to be used for converting the key/value of the event before notifying the listeners.
    *
    * @param typeConverter the converter instance; can be {@code null}
    */
   void setTypeConverter(TypeConverter typeConverter);
}
