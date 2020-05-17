package com.sukaiyi.weedclient.core;

public class WeedRestEndpoint {

    /**
     * Check System Status
     */
    public static final String DIR_STATUS = "/dir/status";
    /**
     * Assign a file key
     */
    public static final String ASSIGN_FILE_KEY = "/dir/assign";
    /**
     * Lookup volume
     */
    public static final String LOOKUP_VOLUME = "/dir/lookup";
    /**
     * Force garbage collection
     */
    public static final String VOL_VACUUM = "/vol/vacuum";
    /**
     * Pre-Allocate Volumes
     */
    public static final String VOL_GROW = "/vol/grow";
    /**
     * Delete Collection
     */
    public static final String COL_DELETE = "/col/delete";
    /**
     * Check System Status
     */
    public static final String CLUSTER_STATUS = "/cluster/status";


    private WeedRestEndpoint() {

    }
}
