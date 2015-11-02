package com.focusit.istree.hotcache.core.transaction.tm;

import javax.transaction.xa.XAResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple transaction manager implementation that maintains transaction state in memory only.
 *
 * @author bela
 *         <p/>
 *         Date: May 15, 2003 Time: 4:11:37 PM
 * @since 4.0
 */
public class DummyTransactionManager extends DummyBaseTransactionManager {

   protected static final Logger log = LoggerFactory.getLogger(DummyTransactionManager.class);

   private static final long serialVersionUID = 4396695354693176535L;

   public static DummyTransactionManager getInstance() {
      return LazyInitializeHolder.dummyTMInstance;
   }

   public static DummyUserTransaction getUserTransaction() {
      return LazyInitializeHolder.utx;
   }

   public static void destroy() {
      setTransaction(null);
   }

   public XAResource firstEnlistedResource() {
      return getTransaction().firstEnlistedResource();
   }

   private static class LazyInitializeHolder {
      static final DummyTransactionManager dummyTMInstance = new DummyTransactionManager();
      static final DummyUserTransaction utx = new DummyUserTransaction(dummyTMInstance);
   }
}
