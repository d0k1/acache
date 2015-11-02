package com.focusit.istree.hotcache.core.commands;

import com.focusit.istree.hotcache.core.metadata.Metadata;

/**
 * A command that contains metadata information.
 *
 * @author Galder Zamarreño
 * @since 5.3
 */
public interface MetadataAwareCommand {

   /**
    * Get metadata of this command.
    *
    * @return an instance of Metadata
    */
   Metadata getMetadata();

   /**
    * Sets metadata for this command.
    */
   void setMetadata(Metadata metadata);

}
