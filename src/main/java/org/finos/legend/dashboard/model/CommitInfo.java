package org.finos.legend.dashboard.model;

public record CommitInfo(String hash, String message) {

    public String shortHash() {
        return hash.substring(0, Math.min(8, hash.length()));
    }
}
