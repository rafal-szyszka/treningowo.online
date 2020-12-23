package com.prodactivv.app.core.files;

public class UnsupportedStorageTypeException extends Throwable {
    public UnsupportedStorageTypeException(DatabaseFileService.StorageType storageType) {
        super(
                String.format("Storage type %s in unsupported.", storageType.getType())
        );
    }
}
