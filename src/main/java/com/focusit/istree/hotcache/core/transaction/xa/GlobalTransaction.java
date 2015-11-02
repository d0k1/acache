package com.focusit.istree.hotcache.core.transaction.xa;

import java.util.concurrent.atomic.AtomicLong;

import com.focusit.istree.hotcache.core.remoting.transport.Address;


/**
 * Uniquely identifies a transaction that spans all JVMs in a cluster. This is used when replicating all modifications
 * in a transaction; the PREPARE and COMMIT (or ROLLBACK) messages have to have a unique identifier to associate the
 * changes with<br>. GlobalTransaction should be instantiated thorough {@link TransactionFactory} class,
 * as their type depends on the runtime configuration.
 *
 * @author <a href="mailto:bela@jboss.org">Bela Ban</a> Apr 12, 2003
 * @author <a href="mailto:manik@jboss.org">Manik Surtani (manik@jboss.org)</a>
 * @author Mircea.Markus@jboss.com
 * @since 4.0
 */
public class GlobalTransaction implements Cloneable {

   private static final AtomicLong sid = new AtomicLong(0);

   protected long id = -1;

   protected Address addr = null;
   private int hash_code = -1;  // in the worst case, hashCode() returns 0, then increases, so we're safe here
   private boolean remote = false;

   /**
    * empty ctor used by externalization.
    */
   protected GlobalTransaction() {
   }

   protected GlobalTransaction(Address addr, boolean remote) {
      this.id = sid.incrementAndGet();
      this.addr = addr;
      this.remote = remote;
   }

   public Address getAddress() {
      return addr;
   }

   public long getId() {
      return id;
   }

   public boolean isRemote() {
      return remote;
   }

   public void setRemote(boolean remote) {
      this.remote = remote;
   }

   @Override
   public int hashCode() {
      if (hash_code == -1) {
         hash_code = (addr != null ? addr.hashCode() : 0) + (int) id;
      }
      return hash_code;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other)
         return true;
      if (!(other instanceof GlobalTransaction))
         return false;

      GlobalTransaction otherGtx = (GlobalTransaction) other;
      boolean aeq = (addr == null) ? (otherGtx.addr == null) : addr.equals(otherGtx.addr);
      return aeq && (id == otherGtx.id);
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("GlobalTransaction:<").append(addr).append(">:").append(id).append(isRemote() ? ":remote" : ":local");
      return sb.toString();
   }

   /**
    * Returns a simplified representation of the transaction.
    */
   public final String globalId() {
      return getAddress() + ":" + getId();
   }

   public void setId(long id) {
      this.id = id;
   }

   public void setAddress(Address address) {
      this.addr = address;
   }

   @Override
   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException e) {
         throw new IllegalStateException("Impossible!");
      }
   }
}
