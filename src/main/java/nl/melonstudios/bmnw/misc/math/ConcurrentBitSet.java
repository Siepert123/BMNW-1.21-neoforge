package nl.melonstudios.bmnw.misc.math;

import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.LongAdder;

public class ConcurrentBitSet {
    private final AtomicLongArray words;
    private final int size;
    private final LongAdder bitCount = new LongAdder();

    public ConcurrentBitSet(int size) {
        this.size = size;
        int wordCount = (size + 63) >>> 6;
        this.words = new AtomicLongArray(wordCount);
    }

    public void set(int bit) {
        if (bit < 0 || bit >= this.size) return;
        int wordIndex = bit >>> 6;
        long mask = 1L << (bit & 63);
        while (true) {
            long oldWord = this.words.get(wordIndex);
            long newWord = oldWord | mask;
            if (oldWord == newWord) return;
            if (this.words.compareAndSet(wordIndex, oldWord, newWord)) {
                this.bitCount.increment();
                return;
            }
        }
    }

    public void clear(int bit) {
        if (bit < 0 || bit >= this.size) return;
        int wordIndex = bit >>> 6;
        long mask = ~(1L << (bit & 63));
        while (true) {
            long oldWord = this.words.get(wordIndex);
            long newWord = oldWord & mask;
            if (oldWord == newWord) return;
            if (this.words.compareAndSet(wordIndex, oldWord, newWord)) {
                this.bitCount.decrement();
                return;
            }
        }
    }

    public int nextSetBit(int from) {
        if (from < 0) from = 0;
        int wordIndex = from >>> 6;
        if (wordIndex >= this.words.length()) return -1;
        long word = this.words.get(wordIndex) & (~0L << (from & 63));
        while (true) {
            if (word != 0) {
                int idx = (wordIndex << 6) + Long.numberOfTrailingZeros(word);
                return (idx < this.size) ? idx : -1;
            }
            wordIndex++;
            if (wordIndex >= this.words.length()) return -1;
            word = this.words.get(wordIndex);
        }
    }

    public boolean isEmpty() {
        return this.bitCount.sum() == 0;
    }
    public long cardinality() {
        return this.bitCount.sum();
    }
}