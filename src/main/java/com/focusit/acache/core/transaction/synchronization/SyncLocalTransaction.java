package com.focusit.acache.core.transaction.synchronization;

import javax.transaction.Transaction;

import com.focusit.acache.core.equivalence.Equivalence;
import com.focusit.acache.core.transaction.impl.LocalTransaction;
import com.focusit.acache.core.transaction.xa.GlobalTransaction;

/**
 * {@link LocalTransaction} implementation to be used with {@link SynchronizationAdapter}.
 *
 * @author Mircea.Markus@jboss.com
 * @since 5.0
 */
public class SyncLocalTransaction extends LocalTransaction {

   public SyncLocalTransaction(Transaction transaction, GlobalTransaction tx,
         boolean implicitTransaction, int topologyId, Equivalence<Object> keyEquivalence, long txCreationTime) {
      super(transaction, tx, implicitTransaction, topologyId, keyEquivalence, txCreationTime);
   }

   private boolean enlisted;

   @Override
   public boolean isEnlisted() {
      return enlisted;
   }

   public void setEnlisted(boolean enlisted) {
      this.enlisted = enlisted;
   }
}
