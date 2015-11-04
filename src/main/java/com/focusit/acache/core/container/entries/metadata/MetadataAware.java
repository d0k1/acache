package com.focusit.acache.core.container.entries.metadata;

import com.focusit.acache.core.metadata.Metadata;

/**
 * Metdata aware cache entry.
 *
 * @author Galder Zamarreño
 * @since 5.3
 */
public interface MetadataAware {

   /**
    * Get metadata of this cache entry.
    *
    * @return a Metadata instance
    */
   Metadata getMetadata();

   /**
    * Set the metadata in the cache entry.
    *
    * @param metadata to apply to the cache entry
    */
   void setMetadata(Metadata metadata);

}