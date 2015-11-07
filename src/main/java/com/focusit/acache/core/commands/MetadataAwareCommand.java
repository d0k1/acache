package com.focusit.acache.core.commands;

import com.focusit.acache.metadata.Metadata;

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
