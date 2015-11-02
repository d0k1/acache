package com.focusit.istree.hotcache.core.container.versioning;

/**
 * Numeric version
 *
 * @author Galder Zamarreño
 * @since 5.3
 */
public class NumericVersion implements IncrementableEntryVersion {

   private final long version;

   public NumericVersion(long version) {
      this.version = version;
   }

   public long getVersion() {
      return version;
   }

   @Override
   public InequalVersionComparisonResult compareTo(EntryVersion other) {
      if (other instanceof NumericVersion) {
         NumericVersion otherVersion = (NumericVersion) other;
         if (version < otherVersion.version)
            return InequalVersionComparisonResult.BEFORE;
         else if (version > otherVersion.version)
            return InequalVersionComparisonResult.AFTER;
         else
            return InequalVersionComparisonResult.EQUAL;
      }

      throw new IllegalArgumentException(
            "Unable to compare other types: " + other.getClass().getName());
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      NumericVersion that = (NumericVersion) o;

      if (version != that.version) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return (int) (version ^ (version >>> 32));
   }

   @Override
   public String toString() {
      return "NumericVersion{" +
            "version=" + version +
            '}';
   }
}
