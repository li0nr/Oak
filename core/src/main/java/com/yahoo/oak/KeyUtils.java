/*
 * Copyright 2020, Verizon Media.
 * Licensed under the terms of the Apache 2.0 license.
 * Please see LICENSE file in the project root for terms.
 */

package com.yahoo.oak;

import com.yahoo.oak.ValueUtils.ValueResult;

class KeyUtils {


    /* ==================== Methods for operating on existing off-heap keys ==================== */
    
    
    public static <K> int compareKeyAndSerializedKey(K key, OakScopedReadBuffer serializedKey, OakComparator<K> cmp) 
            throws DeletedMemoryAccessException {
        if (((ScopedReadBuffer) serializedKey).s.lockRead() != ValueResult.TRUE) {
            throw new DeletedMemoryAccessException();
        }
        try {
            int res = cmp.compareKeyAndSerializedKey(key, serializedKey);
            return res;
        } finally {
            ((ScopedReadBuffer) serializedKey).s.unlockRead();
        }
    }
    
    public static <K> int compareSerializedKeys(OakScopedReadBuffer serializedKey1,
            OakScopedReadBuffer serializedKey2, OakComparator<K> cmp) throws DeletedMemoryAccessException {
        if (    ((ScopedReadBuffer) serializedKey1).s.lockRead() != ValueResult.TRUE ||
                ((ScopedReadBuffer) serializedKey2).s.lockRead() != ValueResult.TRUE) {
            throw new DeletedMemoryAccessException();
        }
        try {
            int res = cmp.compareSerializedKeys(serializedKey1, serializedKey2);
            return res;
        } finally {
            ((ScopedReadBuffer) serializedKey1).s.unlockRead();
            ((ScopedReadBuffer) serializedKey2).s.unlockRead();
        }
    }
}
