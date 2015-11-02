package com.focusit.istree.hotcache.core.commands;

import java.util.Set;

import com.focusit.istree.hotcache.core.context.Flag;

/**
 * Base class for those local commands that can carry flags.
 *
 * @author William Burns
 * @since 6.0
 */
public abstract class AbstractLocalFlagAffectedCommand implements LocalFlagAffectedCommand {

   protected Set<Flag> flags;

   @Override
   public Set<Flag> getFlags() {
      return flags;
   }

   @Override
   public void setFlags(Set<Flag> flags) {
      this.flags = flags;
   }
}
