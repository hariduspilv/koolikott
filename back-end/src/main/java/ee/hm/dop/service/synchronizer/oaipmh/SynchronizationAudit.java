package ee.hm.dop.service.synchronizer.oaipmh;

public class SynchronizationAudit {

    private long failedToDownload;
    private long successfullyDownloaded;
    private long deletedMaterialsDownloaded;
    private long existingMaterialsUpdated;
    private long existingMaterialsDeleted;
    private long newMaterialsCreated;

    public long getFailedToDownload() {
        return failedToDownload;
    }

    public void failedToDownload() {
        this.failedToDownload++;
    }

    public long getSuccessfullyDownloaded() {
        return successfullyDownloaded;
    }

    public void successfullyDownloaded() {
        this.successfullyDownloaded++;
    }

    public long getDeletedMaterialsDownloaded() {
        return deletedMaterialsDownloaded;
    }

    public void deletedMaterialDownloaded() {
        this.deletedMaterialsDownloaded++;
    }

    public long getExistingMaterialsUpdated() {
        return existingMaterialsUpdated;
    }

    public void existingMaterialUpdated() {
        this.existingMaterialsUpdated++;
    }

    public long getExistingMaterialsDeleted() {
        return existingMaterialsDeleted;
    }

    public void existingMaterialDeleted() {
        this.existingMaterialsDeleted++;
    }

    public long getNewMaterialsCreated() {
        return newMaterialsCreated;
    }

    public void newMaterialCreated() {
        this.newMaterialsCreated++;
    }
}
