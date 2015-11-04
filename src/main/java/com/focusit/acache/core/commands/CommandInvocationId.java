package com.focusit.acache.core.commands;

import org.infinispan.remoting.transport.Address;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents an unique identified for non-transaction write commands.
 *
 * It is used to lock the key for a specific command.
 *
 * @author Pedro Ruivo
 * @since 8.0
 */
public class CommandInvocationId {

   private static final AtomicLong nextId = new AtomicLong(0);

   private final Address address;
   private final long id;

   private CommandInvocationId(Address address, long id) {
      this.address = address;
      this.id = id;
   }

   public static CommandInvocationId generateId(Address address) {
      return new CommandInvocationId(address, nextId.getAndIncrement());
   }

   public static CommandInvocationId generateIdFrom(CommandInvocationId commandInvocationId) {
      return new CommandInvocationId(commandInvocationId.address, nextId.getAndIncrement());
   }


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      CommandInvocationId that = (CommandInvocationId) o;

      return id == that.id && !(address != null ? !address.equals(that.address) : that.address != null);

   }

   @Override
   public int hashCode() {
      int result = address != null ? address.hashCode() : 0;
      result = 31 * result + (int) (id ^ (id >>> 32));
      return result;
   }

   @Override
   public String toString() {
      return "CommandUUID{" +
            "address=" + address +
            ", id=" + id +
            '}';
   }
}
