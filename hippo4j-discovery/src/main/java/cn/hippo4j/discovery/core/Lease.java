/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.discovery.core;

/**
 * Lease.
 */
public class Lease<T> {

    enum Action {
        /**
         * REGISTER
         */
        REGISTER,

        /**
         * CANCEL
         */
        CANCEL,

        /**
         * RENEW
         */
        RENEW
    }

    private T holder;

    private long evictionTimestamp;

    private long registrationTimestamp;

    private long serviceUpTimestamp;

    /**
     * Make it volatile so that the expiration task would see this quicker
     */
    private volatile long lastUpdateTimestamp;

    private long duration;

    public static final int DEFAULT_DURATION_IN_SECS = 90;

    public Lease(T r) {
        holder = r;
        registrationTimestamp = System.currentTimeMillis();
        lastUpdateTimestamp = registrationTimestamp;
        duration = DEFAULT_DURATION_IN_SECS * 1000;
    }

    public void renew() {
        lastUpdateTimestamp = System.currentTimeMillis() + duration;
    }

    public void cancel() {
        if (evictionTimestamp <= 0) {
            evictionTimestamp = System.currentTimeMillis();
        }
    }

    public void serviceUp() {
        if (serviceUpTimestamp == 0) {
            serviceUpTimestamp = System.currentTimeMillis();
        }
    }

    public void setServiceUpTimestamp(long serviceUpTimestamp) {
        this.serviceUpTimestamp = serviceUpTimestamp;
    }

    public boolean isExpired() {
        return isExpired(0L);
    }

    public boolean isExpired(long additionalLeaseMs) {
        return (evictionTimestamp > 0 || System.currentTimeMillis() > (lastUpdateTimestamp + duration + additionalLeaseMs));
    }

    public long getRegistrationTimestamp() {
        return registrationTimestamp;
    }

    public long getLastRenewalTimestamp() {
        return lastUpdateTimestamp;
    }

    public long getEvictionTimestamp() {
        return evictionTimestamp;
    }

    public long getServiceUpTimestamp() {
        return serviceUpTimestamp;
    }

    public T getHolder() {
        return holder;
    }
}
